package de.bodden.mopbox.generic.def;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	private final List<K> variables;
	
	public Alphabet(Set<K> variables) {
		this.variables = new ArrayList<K>(variables);		
	}

	/**
	 * Creates a new symbol for the given label, adding the symbol to this
	 * alphabet.
	 */
	@Override
	public ISymbol<L,K> makeNewSymbol(L label, K... variables) {
		assert variables().containsAll(Arrays.asList(variables)):
			"Symbol uses undefined variables: "+Arrays.asList(variables)+" vs. "+variables();
		
		/* Compute the binding index. This index must be unique to the particular subset of
		 * variables this symbol binds.
		 */
		int bindingIndex = 0;
		for(K var: variables) {
			bindingIndex |= (1 << variables().indexOf(var));
		}
		
		Symbol<L,K> symbol = new Symbol<L,K>(nextSymbolIndex++,label,bindingIndex,variables);
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
	public List<K> variables() {
		return variables;
	}
	
}
