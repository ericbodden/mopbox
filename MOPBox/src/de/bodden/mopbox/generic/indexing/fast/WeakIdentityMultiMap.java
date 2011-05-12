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
 * By default, expired mappings are purged in the thread that created this map.
 * To have the mappings purged in another thread, use the constructor
 * {@link #WeakIdentityMultiMap(int, float, boolean)}, supplying <code>true</code>.
 * 
 * <b>Note that this class is currently not thread safe!</b>
 * 
 * @param <K> The type of all they keys.
 * @param <V> The value type.
 */
public class WeakIdentityMultiMap<K, V> {
	
	protected final Map<WeakIdentityMultiKey,V> backingMap;
	
	protected final ReferenceQueue<K> refQueue = new ReferenceQueue<K>();
	
	protected final int PURGE_EVERY_X_CYCLES;
	
	protected final Thread purger;

	protected int cycles = 0; 
	
	
	public WeakIdentityMultiMap() {
		this(16);
	}
	
	public WeakIdentityMultiMap(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}

	public WeakIdentityMultiMap(int initialCapacity, float loadFactor) {
		this(initialCapacity, 0.75f, false);
	}
	
	public WeakIdentityMultiMap(int initialCapacity, float loadFactor, boolean purgeInSeparateThread) {
		backingMap = new HashMap<WeakIdentityMultiKey, V>(initialCapacity, loadFactor);
		if(purgeInSeparateThread) {
			//disable purging by this thread
			PURGE_EVERY_X_CYCLES = Integer.MAX_VALUE;			
			purger = new Thread() {
				@SuppressWarnings("unchecked")
				public void run() {					
					while(true) {
						try {
							WeakReferenceWithBacklink<K> ref = (WeakReferenceWithBacklink<K>) refQueue.remove();
							WeakIdentityMultiKey owningMultiKey = ref.getOwningMultiKey();
							backingMap.remove(owningMultiKey);
						} catch (InterruptedException e) {
							return;
						}
					}
				}
			};
			purger.setPriority(Thread.NORM_PRIORITY-1);
			purger.setDaemon(true);
			purger.start();
		} else {
			PURGE_EVERY_X_CYCLES = 100;
			purger = null;
		}
	}

	public V put(V value, K... keys) {
		maybePurgeExpiredEntries();
		WeakIdentityMultiKey multiKey = new WeakIdentityMultiKey(keys);
		return backingMap.put(multiKey, value);
	}
	
	public V get(K... keys) {
		maybePurgeExpiredEntries();
		WeakIdentityMultiKey multiKey = new WeakIdentityMultiKey(keys);
		return backingMap.get(multiKey);
	}
	
	public boolean containsKey(K... keys) {
		maybePurgeExpiredEntries();
		WeakIdentityMultiKey multiKey = new WeakIdentityMultiKey(keys);
		return backingMap.containsKey(multiKey);
	}
	
	public V remove(K... keys) {
		maybePurgeExpiredEntries();
		WeakIdentityMultiKey multiKey = new WeakIdentityMultiKey(keys);
		return backingMap.remove(multiKey);
	}
	
	public int size() {
		maybePurgeExpiredEntries();
		return backingMap.size();
	}

	protected void maybePurgeExpiredEntries() {
		//purge only every PURGE_EVERY_X_CYCLES cycles
		if((cycles++) % PURGE_EVERY_X_CYCLES != 0) return;
		
		purgeExpiredEntries();
	}

	@SuppressWarnings("unchecked")
	public void purgeExpiredEntries() {
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