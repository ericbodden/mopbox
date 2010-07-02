package de.bodden.rvlib.generic;


public interface IMonitor<M extends IMonitor<M,L>,L> {
	
	boolean processEvent(ISymbol<L> iSymbol);
	
	M copy();
	
}
