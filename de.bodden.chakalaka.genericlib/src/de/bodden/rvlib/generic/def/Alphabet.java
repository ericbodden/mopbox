package de.bodden.rvlib.generic.def;

import java.util.HashSet;

import de.bodden.rvlib.generic.IAlphabet;
import de.bodden.rvlib.generic.ISymbol;

/**
 * This is the default implementation of the {@link IAlphabet} interface.
 *
 * @param <L> The type of labels used to label symbols of this alphabet.
 */
@SuppressWarnings("serial")
public class Alphabet<L> extends HashSet<ISymbol<L>> implements IAlphabet<L> {

}
