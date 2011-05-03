package de.bodden.rvlib.generic;

import java.util.Set;

/**
 * An {@link IAlphabet} is the set of symbols that makes up the alphabet over which
 * a monitor is evaluated. 
 */
public interface IAlphabet<L> extends Set<ISymbol<L>> {

}
