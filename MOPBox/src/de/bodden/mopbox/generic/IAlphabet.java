package de.bodden.mopbox.generic;

import java.util.Set;

/**
 * An {@link IAlphabet} is the set of symbols that makes up the alphabet over which
 * a monitor is evaluated. 
 */
public interface IAlphabet<L> extends Set<ISymbol<L>> {

	/**
	 * Creates a new symbol for the given label, adding the symbol to this
	 * alphabet.
	 */
	public ISymbol<L> makeNewSymbol(L label);

	/**
	 * Retrieves a symbol by its label.
	 */
	public ISymbol<L> getSymbolByLabel(L label);
}
