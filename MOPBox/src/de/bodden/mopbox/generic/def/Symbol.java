package de.bodden.mopbox.generic.def;

import de.bodden.mopbox.generic.AbstractMonitorTemplate;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.ISymbol;

/**
 * This class represents a symbol. A symbol is member of an alphabet
 * which a monitor is evaluated over. Symbols can not only be labeled with
 * Strings but also with other Objects of the generic Type L.
 * 
 * Symbols are indexed, usually starting at 0. Clients can exploit this 
 * for efficiency.
 * 
 * Symbols are not usually created directly but by calling
 * {@link IAlphabet#makeNewSymbol(Object)}
 * 
 * @param <L> The type to use as label for a symbol. Often {@link String}.
 * @param <K> The type of keys used for variable bindings.
 */
public class Symbol<L,K> implements ISymbol<L,K> {
	
	private final int uniqueNumber;
	
	private final L label;

	private final K[] variables;

	private final int bindingIndex;

	/**
	 * Symbols are not usually created directly but by calling
	 * {@link AbstractMonitorTemplate#makeNewSymbol}
	 */
	public Symbol(int uniqueNumber, L label, int bindingIndex, K... variables) {
		this.uniqueNumber = uniqueNumber;
		this.label = label;
		this.variables = variables;
		this.bindingIndex = bindingIndex;
	}

	public int getIndex() {
		return uniqueNumber;
	}
	
	public L getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return getLabel().toString();
	}

	@Override
	public K[] getVariables() {
		return variables;
	}
	
	public int getBindingIndex() {
		return bindingIndex;
	}
}
