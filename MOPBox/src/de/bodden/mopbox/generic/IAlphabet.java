package de.bodden.mopbox.generic;

import java.util.Set;


/**
 * An {@link IAlphabet} is the set of symbols that makes up the alphabet over which
 * a monitor is evaluated. 
 */
public interface IAlphabet<L> extends Iterable<ISymbol<L>> {

	/**
	 * Creates a new symbol for the given label, adding the symbol to this
	 * alphabet.
	 */
	public ISymbol<L> makeNewSymbol(L label);

	/**
	 * Retrieves a symbol by its label.
	 */
	public ISymbol<L> getSymbolByLabel(L label);

	/**
	 * Returns the number of symbols in this alphabet.
	 */
	public int size();
	
	/**
	 * Returns a read-only set representation of this alphabet.
	 */
	public Set<ISymbol<L>> asSet();
}
