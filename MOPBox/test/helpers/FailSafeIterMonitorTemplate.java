package helpers;

import static helpers.FailSafeIterMonitorTemplate.Var.C;
import static helpers.FailSafeIterMonitorTemplate.Var.I;

import java.util.Arrays;
import java.util.Set;

import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import helpers.FailSafeIterMonitorTemplate.Var;

/**
 * This is a concrete monitor template for the following regular-expression property:
 * <code>create(c,i) update(c)* iter(i)+ update(c)+ iter(i)</code> 
 */
public class FailSafeIterMonitorTemplate extends AbstractFSMMonitorTestTemplate<String,Var,Object> {

	public enum Var{ C, I }

	@Override
	protected void fillVariables(Set<Var> variables) {
		variables.addAll(Arrays.asList(Var.values()));
	}

	@Override
	protected void fillAlphabet(IAlphabet<String,Var> a) {
		a.makeNewSymbol("create", C, I);
		a.makeNewSymbol("update", C);
		a.makeNewSymbol("iter", I);
	}
	
	protected State<String> setupStatesAndTransitions() {
		State<String> initial = makeState(false);
		State<String> iterating = makeState(false);
		State<String> updated = makeState(false);
		State<String> error = makeState(true);
		
		initial.addTransition(getSymbolByLabel("create"), iterating);
		initial.addTransition(getSymbolByLabel("update"), initial);
		initial.addTransition(getSymbolByLabel("iter"), initial);
		iterating.addTransition(getSymbolByLabel("iter"), iterating);
		iterating.addTransition(getSymbolByLabel("update"), updated);
		updated.addTransition(getSymbolByLabel("update"), updated);
		updated.addTransition(getSymbolByLabel("iter"), error);
		return initial;
	}

}
