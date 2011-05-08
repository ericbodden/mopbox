package de.bodden.mopbox.generic.indexing.fast;

import java.lang.ref.WeakReference;

public class WeakIdentityMultiKey<K> {
	
	protected final WeakReference<K>[] keyRefs;
	private final int hashCode;
	
	@SuppressWarnings("unchecked")
	public WeakIdentityMultiKey(K... keys) {
		int hash = 0;
		keyRefs = new WeakReference[keys.length];
		for(int i=0;i<keys.length;i++) {
			keyRefs[i] = new WeakReference<K>(keys[i]);
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
		if(other instanceof WeakIdentityMultiKey) {
			@SuppressWarnings("unchecked")
			WeakIdentityMultiKey<K> otherKey = (WeakIdentityMultiKey<K>) other;
			if(otherKey.keyRefs.length!=keyRefs.length) return false;
			//FIXME may have a race with the garbage collector here
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
