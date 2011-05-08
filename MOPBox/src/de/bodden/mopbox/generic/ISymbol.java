package de.bodden.mopbox.generic;

/**
 * A symbol is member of an alphabet
 * which a monitor is evaluated over. Symbols can not only be labeled with
 * Strings but also with other Objects of the generic Type L.
 * 
 * Symbols are not usually created directly but by calling
 * {@link AbstractMonitorTemplate#makeNewSymbol}
 * 
 * For efficiency, symbols are uniquely indexed. This allows for
 * array-based implementations. 
 * 
 * @param <L> The type to use as label for a symbol. Often {@link String}.
 * @param <K> The type of keys used for variable bindings.
 */
public interface ISymbol<L,K> {

	int getIndex();

	L getLabel();
	
	K[] getVariables();
	
	int getBindingIndex();
	
}