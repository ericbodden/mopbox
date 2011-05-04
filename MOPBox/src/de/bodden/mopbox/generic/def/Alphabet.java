package de.bodden.mopbox.generic.def;

import java.util.HashSet;

import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.ISymbol;

/**
 * This is the default implementation of the {@link IAlphabet} interface.
 *
 * @param <L> The type of labels used to label symbols of this alphabet.
 */
@SuppressWarnings("serial")
public class Alphabet<L> extends HashSet<ISymbol<L>> implements IAlphabet<L> {

	private int nextSymbolIndex = 0;

	/**
	 * Creates a new symbol for the given label, adding the symbol to this
	 * alphabet.
	 */
	@Override
	public ISymbol<L> makeNewSymbol(L label) {
		Symbol<L> symbol = new Symbol<L>(label,nextSymbolIndex++);
		add(symbol);
		return symbol;
	}
	
	/**
	 * Retrieves a symbol by its label.
	 */
	@Override
	public ISymbol<L> getSymbolByLabel(L label) {
		for (ISymbol<L> sym : this) {
			if(sym.getLabel().equals(label)) {
				return sym;
			}
		}
		throw new IllegalArgumentException("Unknown symbol:" +label);
	}
	
}
