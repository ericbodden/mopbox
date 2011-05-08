package de.bodden.mopbox.generic;

import java.util.List;
import java.util.Set;


/**
 * An {@link IAlphabet} is the set of symbols that makes up the alphabet over which
 * a monitor is evaluated. Symbols can declare the variables they bind.
 * @param <L> The type of objects that symbols are labeled with.
 * @param <K> The type of keys used for variable bindings.
 */
public interface IAlphabet<L,K> extends Iterable<ISymbol<L,K>> {

	/**
	 * Creates a new symbol for the given label, adding the symbol to this
	 * alphabet.
	 * @param label the object (typically String or Enum) this transition is labeled with
	 * @param variables the variables that events with this label bind
	 */
	public ISymbol<L,K> makeNewSymbol(L label,K... variables);

	/**
	 * Retrieves a symbol by its label.
	 */
	public ISymbol<L,K> getSymbolByLabel(L label);

	/**
	 * Returns the number of symbols in this alphabet.
	 */
	public int size();
	
	/**
	 * Returns all variables declared for this alphabet.
	 */
	public List<K> variables();

	/**
	 * Returns a read-only set representation of this alphabet.
	 */
	public Set<ISymbol<L,K>> asSet();
	
}
