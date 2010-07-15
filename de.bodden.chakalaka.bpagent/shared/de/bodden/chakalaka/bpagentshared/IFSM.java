package de.bodden.chakalaka.bpagentshared;

import java.io.Serializable;
import java.util.Set;

public interface IFSM extends Serializable {
	
	public Set<String> symbols();
	
	public String classNameForSymbol(String symbol);

	public int lineNumberForSymbol(String symbol);
	
	public int numberOfStates();
	
	public int succ(int stateNum, String symbol);

}
