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

}
