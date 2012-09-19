package de.bodden.mopbox.generic.def;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.ISymbol;

public class DynamicAlphabet<L,K> implements IAlphabet<L,K> {

	private int nextSymbolIndex = 0;
	private Set<ISymbol<L,K>> backingSet = new HashSet<ISymbol<L,K>>();
	private Map<L,ISymbol<L,K>> labelToSymbol = new HashMap<L,ISymbol<L,K>>();
	
	/**
	 * Creates a new symbol for the given label, adding the symbol to this
	 * alphabet.
	 */
	@Override
	public ISymbol<L,K> makeNewSymbol(L label, K... variables) {
		Symbol<L,K> symbol = new Symbol<L,K>(nextSymbolIndex++,label,0);
		backingSet.add(symbol);
		labelToSymbol.put(label, symbol);
		return symbol;
	}
	
	/**
	 * Retrieves a symbol by its label.
	 * TODO speed this up or avoid altogether - is called at every event!
	 */
	@Override
	public ISymbol<L,K> getSymbolByLabel(L label) {
		ISymbol<L, K> sym = labelToSymbol.get(label);
		if(sym==null) {
			sym=makeNewSymbol(label);
		}
		return sym;
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
	public List<K> variables() {
		return Collections.emptyList();
	}
	
}
