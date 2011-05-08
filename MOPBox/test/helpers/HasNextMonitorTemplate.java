package helpers;

import static helpers.HasNextMonitorTemplate.Var.I;

import java.util.Arrays;
import java.util.Set;

import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import helpers.HasNextMonitorTemplate.Var;

/**
 * This is a concrete monitor template for the following regular-expression property:
 * <code>next(i) next(i)</code> over the alphabet <code>{hasNext, next}</code> 
 */
public class HasNextMonitorTemplate extends AbstractFSMMonitorTestTemplate<String,Var,Object> {

	public enum Var { I }
	
	@Override
	protected void fillVariables(Set<Var> variables) {
		variables.addAll(Arrays.asList(Var.values()));
	}

	@Override
	protected void fillAlphabet(IAlphabet<String,Var> a) {
		a.makeNewSymbol("hasNext", I);
		a.makeNewSymbol("next", I);
	}
	
	protected State<String> setupStatesAndTransitions() {
		State<String> initial = makeState(false);
		State<String> middle = makeState(false);
		State<String> error = makeState(true);
		
		initial.addTransition(getSymbolByLabel("next"), middle);
		middle.addTransition(getSymbolByLabel("next"), error);
		error.addTransition(getSymbolByLabel("next"), error);
		return initial;
	}

}
