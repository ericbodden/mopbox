package de.bodden.mopbox.generic.indexing.fast;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a map that maps from a vector of multiple keys to a single value.
 * Comparison is performed on identity, not equality. Keys are referenced
 * weakly, i.e., they are referenced through {@link WeakReference}s, and therefore
 * can be garbage collected. When a key expires, i.e., is about to be garbage collected,
 * this map is notified through a {@link ReferenceQueue} and all mappings for this key
 * are automatically scheduled for removal. 
 *
 * @param <K> The type of all they keys.
 * @param <V> The value type.
 */
public class WeakIdentityMultiMap<K, V> {
	
	protected final Map<WeakIdentityMultiKey,V> backingMap
		= new HashMap<WeakIdentityMultiKey, V>();
	
	protected final ReferenceQueue<K> refQueue = new ReferenceQueue<K>();
	
	protected final int PURGE_EVERY_X_CYCLES=100;
	
	protected int cycles = 0; 

	public V put(V value, K... keys) {
		purgeExpiredEntries();
		WeakIdentityMultiKey multiKey = new WeakIdentityMultiKey(keys);
		return backingMap.put(multiKey, value);
	}
	
	public V get(K... keys) {
		purgeExpiredEntries();
		WeakIdentityMultiKey multiKey = new WeakIdentityMultiKey(keys);
		return backingMap.get(multiKey);
	}
	
	public boolean containsKey(K... keys) {
		purgeExpiredEntries();
		WeakIdentityMultiKey multiKey = new WeakIdentityMultiKey(keys);
		return backingMap.containsKey(multiKey);
	}
	
	public V remove(K... keys) {
		purgeExpiredEntries();
		WeakIdentityMultiKey multiKey = new WeakIdentityMultiKey(keys);
		return backingMap.remove(multiKey);
	}

	@SuppressWarnings("unchecked")
	protected void purgeExpiredEntries() {
		//purge only every PURGE_EVERY_X_CYCLES cyles
		if((cycles++) % PURGE_EVERY_X_CYCLES != 0) return;
		
		WeakReferenceWithBacklink<K> ref;
		while((ref = (WeakReferenceWithBacklink<K>) refQueue.poll())!=null) {
			WeakIdentityMultiKey owningMultiKey = ref.getOwningMultiKey();
			backingMap.remove(owningMultiKey);
		}
	}

	protected class WeakReferenceWithBacklink<T extends K> extends WeakReference<K> {

		private final WeakIdentityMultiKey owningMultiKey;

		public WeakReferenceWithBacklink(K referent, ReferenceQueue<? super K> q, WeakIdentityMultiKey owningMultiKey) {
			super(referent, q);
			this.owningMultiKey = owningMultiKey;
		}
		
		public WeakIdentityMultiKey getOwningMultiKey() {
			return owningMultiKey;
		}
	}
	
	/**
	 * A key consisting of a vector of individual keys.
	 * Uses weak references and comparison on identity. 
	 */
	protected class WeakIdentityMultiKey {
		
		protected final WeakReference<K>[] keyRefs;
		private final int hashCode;
		
		@SuppressWarnings("unchecked")
		public WeakIdentityMultiKey(K... keys) {
			int hash = 0;
			keyRefs = new WeakReference[keys.length];
			for(int i=0;i<keys.length;i++) {
				keyRefs[i] = new WeakReferenceWithBacklink<K>(keys[i],refQueue,this);
	            hash ^= System.identityHashCode(keys[i]);
			}			
			this.hashCode = hash;
		}
			
		@Override
		public int hashCode() {
			return hashCode;
		}
		
		@Override
		public boolean equals(Object other) {
			if(other==this) return true;
			if(other instanceof WeakIdentityMultiMap.WeakIdentityMultiKey) {
				@SuppressWarnings("unchecked")
				WeakIdentityMultiKey otherKey = (WeakIdentityMultiKey) other;
				if(otherKey.keyRefs.length!=keyRefs.length) return false;
				//FIXME may we have a race with the garbage collector here?
				//probably not, as the values we look up are still strongly referenced somewhere
				for(int i=0;i<keyRefs.length;i++) {
					K thisRef = keyRefs[i].get();
					K otherRef = otherKey.keyRefs[i].get();
					//comparison on identity
					if(thisRef!=otherRef) return false;
				}
			}
			return true;
		}
	}

}