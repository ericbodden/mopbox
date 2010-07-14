package de.bodden.chakalaka.bpagentshared;

import java.io.Serializable;
import java.util.Set;

public interface IFSM extends Serializable {
	
	public Set<String> symbols();
	
	public String classNameForLabel(String symbol);

	public int lineNumberForLabel(String symbol);
	
	public int numberOfStates();
	
	public int succ(int stateNum, String symbol);

}
