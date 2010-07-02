package de.bodden.rvlib.finitestate;

import de.bodden.rvlib.generic.IAlphabet;
import de.bodden.rvlib.generic.ISymbol;

public class State<L> implements IState<State<L>,L> {
	
	private State<L>[] successorTable;
	private final boolean isFinal;
	private final String label;//for display purposes only
	
	@SuppressWarnings("unchecked")
	public State(IAlphabet<L> alphabet, boolean isFinal, String label){
		this.isFinal = isFinal;
		this.label = label;
		successorTable = new State[alphabet.size()];
	}
	
	/* (non-Javadoc)
	 * @see de.bodden.rvlib.impl.IState#registerSuccessor(de.bodden.rvlib.impl.Symbol, de.bodden.rvlib.impl.IState)
	 */
	public void addTransition(ISymbol<L> sym, State<L> succ) {
		assert successorTable[sym.getIndex()]==null : "successor already set";
		successorTable[sym.getIndex()] = succ;
	}
	
	/* (non-Javadoc)
	 * @see de.bodden.rvlib.impl.IState#successor(de.bodden.rvlib.impl.Symbol)
	 */
	public State<L> successor(ISymbol<L> sym) {
		return successorTable[sym.getIndex()];
	}

	@Override
	public boolean isFinal() {
		return isFinal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public State<L> copy() {
		try {
			return (State<L>) clone();
		} catch (CloneNotSupportedException e) {
			//cannot be thrown
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		String s = label;
		if(isFinal()) s += "*";
		return s;
	}

	public State<L> targetFor(ISymbol<L> s) {
		return successorTable[s.getIndex()];
	}
}
