package de.bodden.mopbox.generic.def;

import java.util.Arrays;
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
 * @param <K> The type of keys used for variable bindings.
*/
public class Alphabet<L,K> implements IAlphabet<L,K> {

	private int nextSymbolIndex = 0;
	private HashSet<ISymbol<L,K>> backingSet = new HashSet<ISymbol<L,K>>();

	/**
	 * Creates a new symbol for the given label, adding the symbol to this
	 * alphabet.
	 */
	@Override
	public ISymbol<L,K> makeNewSymbol(L label, K... variables) {
		Symbol<L,K> symbol = new Symbol<L,K>(nextSymbolIndex++,label,variables);
		backingSet.add(symbol);
		return symbol;
	}
	
	/**
	 * Retrieves a symbol by its label.
	 * TODO speed this up or avoid altogether - is called at every event!
	 */
	@Override
	public ISymbol<L,K> getSymbolByLabel(L label) {
		for (ISymbol<L,K> sym : this) {
			if(sym.getLabel().equals(label)) {
				return sym;
			}
		}
		throw new IllegalArgumentException("Unknown symbol:" +label);
	}

	@Override
	public Iterator<ISymbol<L,K>> iterator() {
		return backingSet.iterator();
	}

	@Override
	public int size() {
		return backingSet.size();
	}

	@Override
	public Set<ISymbol<L,K>> asSet() {
		return Collections.unmodifiableSet(backingSet);
	}

	@Override
	public Set<K> variables() {
		Set<K> res = new HashSet<K>();
		for (ISymbol<L, K> sym : this) {
			res.addAll(Arrays.asList(sym.getVariables()));
		}
		return res;
	}
	
}
