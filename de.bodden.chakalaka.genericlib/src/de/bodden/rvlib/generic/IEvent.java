package de.bodden.rvlib.generic;

public interface IEvent<L,K,V> {
	
	ISymbol<L> getSymbol();
	
	IVariableBinding<K,V> getVariableBinding();

}