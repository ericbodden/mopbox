package de.bodden.mopbox.generic.def;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.ISymbol;

/**
 * This is the default implementation of the {@link IAlphabet} interface.
 * Symbols are indexed, starting at 0.
 *
 * @param <L> The type of labels used to label symbols of this alphabet.
 */
public class Alphabet<L> implements IAlphabet<L> {

	private int nextSymbolIndex = 0;
	private HashSet<ISymbol<L>> backingSet = new HashSet<ISymbol<L>>();

	/**
	 * Creates a new symbol for the given label, adding the symbol to this
	 * alphabet.
	 */
	@Override
	public ISymbol<L> makeNewSymbol(L label) {
		Symbol<L> symbol = new Symbol<L>(label,nextSymbolIndex++);
		backingSet.add(symbol);
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

	@Override
	public Iterator<ISymbol<L>> iterator() {
		return backingSet.iterator();
	}

	@Override
	public int size() {
		return backingSet.size();
	}

	@Override
	public Set<ISymbol<L>> asSet() {
		return Collections.unmodifiableSet(backingSet);
	}
	
}
