package de.bodden.rvlib.generic.def;

import java.util.HashSet;

import de.bodden.rvlib.generic.IAlphabet;
import de.bodden.rvlib.generic.ISymbol;

@SuppressWarnings("serial")
public class Alphabet<L> extends HashSet<ISymbol<L>> implements IAlphabet<L> {

}
