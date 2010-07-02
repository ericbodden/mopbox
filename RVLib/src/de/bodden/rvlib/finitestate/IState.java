package de.bodden.rvlib.finitestate;

import de.bodden.rvlib.generic.ISymbol;


public interface IState<S extends IState<S,L>,L> extends Cloneable {

	public void addTransition(ISymbol<L> sym, S succ);

	public IState<S,L> successor(ISymbol<L> sym);
	
	public boolean isFinal();
	
	public S copy();

	public State<L> targetFor(ISymbol<L> s);

}