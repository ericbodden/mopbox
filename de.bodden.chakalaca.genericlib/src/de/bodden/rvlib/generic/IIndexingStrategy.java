package de.bodden.rvlib.generic;

public interface IIndexingStrategy<L,K,V> {

	void processEvent(IEvent<L,K,V> e);

}
