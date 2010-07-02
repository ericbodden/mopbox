package de.bodden.rvlib.generic.def;

import de.bodden.rvlib.generic.IEvent;
import de.bodden.rvlib.generic.IMonitor;
import de.bodden.rvlib.generic.ISymbol;
import de.bodden.rvlib.generic.IVariableBinding;

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
