package de.bodden.mopbox.generic.def;

import de.bodden.mopbox.generic.IEvent;
import de.bodden.mopbox.generic.IMonitor;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;

/**
 * This is the default implementation of the {@link IEvent} interface.
 * The class essentially just implements a struct.
 *
 * @param <M> The concrete monitor type.
 * @param <L> The type of labels used at transitions.
 * @param <K> The type of keys used for the variable bindings.
 * @param <V> The type of values used for the variable bindings.
 */
public class Event<M extends IMonitor<M,L>,L,K,V> implements IEvent<L,K,V> {
	
	private final ISymbol<L> symbol;
	private final IVariableBinding<K,V> binding;

	public Event(ISymbol<L> symbol, IVariableBinding<K,V> binding) {
		this.symbol = symbol;
		this.binding = binding;
	}

	public ISymbol<L> getSymbol() {
		return symbol;
	}
	
	@Override
	public IVariableBinding<K,V> getVariableBinding() {
		return binding;
	}
	
	@Override
	public String toString() {
		return getSymbol()+getVariableBinding().toString();
	}
}
