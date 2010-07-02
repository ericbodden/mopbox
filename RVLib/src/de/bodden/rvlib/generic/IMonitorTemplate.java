package de.bodden.rvlib.generic;

import java.util.Set;

public interface IMonitorTemplate<M extends IMonitor<M,L>,L,K,V> {
	
	void processEvent(IEvent<L,K,V> e);
	
	void matchCompleted(IVariableBinding<K,V> binding);

	M createMonitorPrototype();

	Set<ISymbol<L>> computeCreationSymbols();
	
	IAlphabet<L> getAlphabet();	
}
