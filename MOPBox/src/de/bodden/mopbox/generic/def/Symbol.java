package de.bodden.mopbox.generic.def;

import de.bodden.mopbox.generic.AbstractMonitorTemplate;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.ISymbol;

/**
 * This class represents a symbol. A symbol is member of an alphabet
 * which a monitor is evaluated over. Symbols can not only be labeled with
 * Strings but also with other Objects of the generic Type T.
 * 
 * Symbols are indexed, usually starting at 0. Clients can exploit this 
 * for efficiency.
 * 
 * Symbols are not usually created directly but by calling
 * {@link IAlphabet#makeNewSymbol(Object)}
 * 
 * @param <T> The type to use as label for a symbol. Often {@link String}.
 */
public class Symbol<T> implements ISymbol<T> {
	
	private int uniqueNumber;
	
	private T label;

	/**
	 * Symbols are not usually created directly but by calling
	 * {@link AbstractMonitorTemplate#makeNewSymbol}
	 */
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
