package helpers;

import static helpers.ConnectionClosedMonitorTemplate.Var.C;

import java.util.Arrays;
import java.util.Set;

import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import helpers.ConnectionClosedMonitorTemplate.Var;

/**
 * This is a concrete monitor template for the following regular-expression property:
 * <code>close+(c) write(c)</code> 
 */
public class ConnectionClosedMonitorTemplate extends AbstractFSMMonitorTestTemplate<String,Var,Object> {

	public enum Var { C }
	
	@Override
	protected void fillVariables(Set<Var> variables) {
		variables.addAll(Arrays.asList(Var.values()));
	}
	
	@Override
	protected void fillAlphabet(IAlphabet<String,Var> a) {
		a.makeNewSymbol("close", C);
		a.makeNewSymbol("write", C);
	}

	protected State<String> setupStatesAndTransitions() {
		State<String> initial = makeState(false);
		State<String> closed = makeState(false);
		State<String> error = makeState(true);
		
		initial.addTransition(getSymbolByLabel("close"), closed);
		closed.addTransition(getSymbolByLabel("close"),closed);
		closed.addTransition(getSymbolByLabel("write"), error);
		return initial;
	}



}
