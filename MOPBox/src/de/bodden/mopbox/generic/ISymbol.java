package de.bodden.mopbox.generic;

/**
 * A symbol is member of an alphabet
 * which a monitor is evaluated over. Symbols can not only be labeled with
 * Strings but also with other Objects of the generic Type L.
 * 
 * Symbols are not usually created directly but by calling
 * {@link AbstractMonitorTemplate#makeNewSymbol}
 * 
 * @param <L> The type to use as label for a symbol. Often {@link String}.
 */
public interface ISymbol<L> {

	int getIndex();

	L getLabel();
	

}