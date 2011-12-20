package de.bodden.mopbox.finitestate;

import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.ISymbol;

/**
 * A default implementation of the {@link IState} interface.
 */
public class State<L> implements IState<State<L>,L> {
	
	private State<L>[] successorTable;
	private final boolean isFinal;
	private final String label;//for display purposes only
	
	@SuppressWarnings("unchecked")
	public State(IAlphabet<L,?> alphabet, boolean isFinal, String label){
		this.isFinal = isFinal;
		this.label = label;
		successorTable = new State[alphabet.size()];
	}
	
	/* (non-Javadoc)
	 * @see de.bodden.mopbox.impl.IState#registerSuccessor(de.bodden.mopbox.impl.Symbol, de.bodden.mopbox.impl.IState)
	 */
	public void addTransition(ISymbol<L,?> sym, State<L> succ) {
		assert successorTable[sym.getIndex()]==null : "successor already set";
		successorTable[sym.getIndex()] = succ;
	}
	
	/* (non-Javadoc)
	 * @see de.bodden.mopbox.impl.IState#successor(de.bodden.mopbox.impl.Symbol)
	 */
	public State<L> successor(ISymbol<L,?> sym) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isFinal ? 1231 : 1237);
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		State other = (State) obj;
		if (isFinal != other.isFinal)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
	
}
