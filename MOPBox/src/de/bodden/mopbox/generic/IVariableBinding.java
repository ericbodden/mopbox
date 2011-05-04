package de.bodden.mopbox.generic;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An {@link IVariableBinding} maps user-declared variables to concrete values.
 * Keys are compared by identity however. Hence, if {@link String}s are used as
 * keys, they should be interned (by calling {@link String#intern()} before. 
 */
public interface IVariableBinding<K,V> extends Map<K, V> {
	
	/**
	 * Returns true if other binds all variables that both
	 * this and other bind to the same objects as this.
	 * Note that this method is symmetric.
	 */
	boolean isCompatibleWith(IVariableBinding<K,V> other);
	
	/**
	 * Returns true if this binds only variables that other
	 * binds and if it binds all those variables to the
	 * same objects. less informative implies
	 * compatible. This method is <i>not</i> symmetric.
	 * @see #isCompatibleWith(IVariableBinding) 
	 */
	boolean isLessInformativeThan(IVariableBinding<K,V> other);
	
	IVariableBinding<K,V> computeJoinWith(IVariableBinding<K,V> other);

	/**
	 * Returns the most informative binding (i.e., the one that binds most variables)
	 * in set that less informative than this. Note that
	 * such binding is only unique if the set is closed under least-upper bounds (lub-closed).
	 * See Technical Report UIUCDCS-R-2008-2977 Section 4.3 for details.
	 * @see #isCompatibleWith(IVariableBinding)
	 */
	IVariableBinding<K,V> findMax(Set<IVariableBinding<K,V>> set);
	
	/**
	 * Returns all bindings that are strictly less informative than this.
	 * The bindings are ordered in reversed topological order, i.e., from larger to smaller.
	 */
	List<IVariableBinding<K,V>> strictlyLessInformativeBindingsOrdered() ;

}
