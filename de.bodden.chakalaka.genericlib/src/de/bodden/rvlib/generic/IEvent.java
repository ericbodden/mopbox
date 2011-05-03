package de.bodden.rvlib.generic;

/**
 * Objects implementing this interface encapsulate a single event. An event is defined through a symbol
 * (which is usually labeled with a String but could in principle hold other labels) and by a variable binding.
 *
 * @param <L> The type of labels used at transitions.
 * @param <K> The type of keys used for the variable bindings.
 * @param <V> The type of values used for the variable bindings.
 */
public interface IEvent<L,K,V> {
	
	/**
	 * Returns the symbol of this event.
	 */
	ISymbol<L> getSymbol();
	
	/**
	 * Returns the variable binding of this event.
	 */
	IVariableBinding<K,V> getVariableBinding();

}