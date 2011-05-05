package de.bodden.mopbox.generic.def;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.AbstractReferenceMap;
import org.apache.commons.collections15.map.ReferenceIdentityMap;

import de.bodden.mopbox.generic.IVariableBinding;

/**
 * A {@link VariableBinding} maps user-declared variables to concrete values. It is similar to a {@link HashMap} and
 * indeed uses such a map as super class. Keys are compared by identity however. Hence, if {@link String}s are used as
 * keys, they should be interned (by calling {@link String#intern()} before. 
 */
@SuppressWarnings("serial")
public class VariableBinding<K,V> extends ReferenceIdentityMap<K, V> implements IVariableBinding<K,V>, Cloneable {

	public VariableBinding(Map<K,V> map) {
		//TODO want to use weak values here but the problem is that these maps are used as keys
		//in other maps; hence need to update the parent maps!
		super(AbstractReferenceMap.HARD,AbstractReferenceMap.HARD);
		putAll(map);
	}

	public VariableBinding() {
		this(Collections.<K,V>emptyMap());
	}

	@Override
	public boolean isCompatibleWith(IVariableBinding<K,V> other) {
		for(K s: keySet()) {
			V otherBinding = other.get(s);
			if (otherBinding!=null && !equalBindings(otherBinding,get(s))) {
				return false;
			}
		}
		return true; 
	}
	
	@Override
	public boolean isLessInformativeThan(IVariableBinding<K,V> other) {
		for(K s: keySet()) {
			V otherBinding = other.get(s);
			if (otherBinding==null || !equalBindings(otherBinding,get(s))) {
				return false;
			}
		}
		assert isCompatibleWith(other); //"less informative" should always imply "compatible"
		return true; 
	}

	protected boolean equalBindings(V binding, V otherBinding) {
		//by default, we use pointer equality
		return binding==otherBinding;
	}

	@SuppressWarnings("unchecked")
	@Override
	public VariableBinding<K,V> computeJoinWith(IVariableBinding<K,V> other) {
		if(!isCompatibleWith(other)) throw new IllegalArgumentException("bindings not compatible! can only compute join over compatible bindings!");
		
		VariableBinding<K,V> v = (VariableBinding<K,V>) clone();
		for(Entry<K,V> otherEntry: other.entrySet()) {
			v.put(otherEntry.getKey(), otherEntry.getValue());
		}
		
		return v;
	}

	@Override
	public IVariableBinding<K,V> findMax(Set<IVariableBinding<K,V>> set) {
		Set<IVariableBinding<K,V>> lessInformativeBindings = new HashSet<IVariableBinding<K,V>>();
		for (IVariableBinding<K,V> b : set) {
			if(b.isLessInformativeThan(this)) {
				lessInformativeBindings.add(b);
			}
		}
		
		assert set!=null;
		IVariableBinding<K,V> max = null;
		int maxSize = -1;
		for (IVariableBinding<K,V> current : lessInformativeBindings) {
			if(current.size()>maxSize && current.size()<=size()) {
				max = current;
				maxSize = max.size();
			}
		}
		return max;
	}	
	
	public List<IVariableBinding<K,V>> strictlyLessInformativeBindingsOrdered() {
		ArrayList<IVariableBinding<K,V>> res = new ArrayList<IVariableBinding<K,V>>();
		List<IVariableBinding<K,V>> lastBindings = Collections.<IVariableBinding<K,V>>singletonList(this);
		//as long as we have not produced the empty binding...
		while(lastBindings.get(0).size()>0) {
			List<IVariableBinding<K,V>> nextBindings = new ArrayList<IVariableBinding<K,V>>();
			for (IVariableBinding<K,V> b : lastBindings) {
				nextBindings.addAll(((VariableBinding<K,V>)b).strictlyLessInformativeBindingsOrderedOneLevel());
			}
			for (IVariableBinding<K,V> b : nextBindings) {
				if(!res.contains(b)) res.add(b);
			}
			lastBindings = nextBindings;
		}
		
		return res;
	}

	public List<IVariableBinding<K,V>> strictlyLessInformativeBindingsOrderedOneLevel() {
		ArrayList<IVariableBinding<K,V>> res = new ArrayList<IVariableBinding<K,V>>();
		for (K key : keySet()) {
			VariableBinding<K,V> copy = copy();
			copy.remove(key);
			res.add(copy);
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
	private VariableBinding<K,V> copy() {
		return (VariableBinding<K,V>) clone();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		List<K> keys = new ArrayList<K>(keySet());
		Collections.sort(keys, new Comparator<K>() {
			public int compare(K o1, K o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		for (Iterator<K> iterator = keys.iterator(); iterator.hasNext();) {
			K k = iterator.next();
			sb.append(k);
			sb.append("=");
			sb.append(get(k));
			if(iterator.hasNext()) sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}
	
}
