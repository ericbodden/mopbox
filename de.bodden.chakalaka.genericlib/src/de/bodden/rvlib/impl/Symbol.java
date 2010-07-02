package de.bodden.rvlib.impl;

import de.bodden.rvlib.generic.ISymbol;

public class Symbol<T> implements ISymbol<T> {
	
	private int uniqueNumber;
	
	private T label;

	public Symbol(T label, int uniqueNumber) {
		this.uniqueNumber = uniqueNumber;
		this.label = label;
	}

	public int getIndex() {
		return uniqueNumber;
	}
	
	public T getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return getLabel().toString();
	}
}
