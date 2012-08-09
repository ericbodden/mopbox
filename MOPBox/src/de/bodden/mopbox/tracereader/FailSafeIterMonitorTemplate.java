package de.bodden.mopbox.tracereader;

import java.util.Set;

import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.IVariableBinding;

/**
 * This is a concrete monitor template for the following regular-expression property:
 * <code>create(c,i) update(c)* next(i)+ update(c)+ next(i)</code> 
 */
public class FailSafeIterMonitorTemplate extends OpenFSMMonitorTemplate<String,String,Integer> {

	public FailSafeIterMonitorTemplate() {
		initialize();
	}
	
	@Override
	protected void fillVariables(Set<String> variables) {
		variables.add("c");
		variables.add("i");
	}

	@Override
	protected void fillAlphabet(IAlphabet<String,String> a) {
		a.makeNewSymbol("create", "c", "i");
		a.makeNewSymbol("update", "c");
		a.makeNewSymbol("next", "i");
	}
	
	protected State<String> setupStatesAndTransitions() {
		State<String> initial = makeState(false);
		State<String> nextating = makeState(false);
		State<String> updated = makeState(false);
		State<String> error = makeState(true);
		
		initial.addTransition(getSymbolByLabel("create"), nextating);
		initial.addTransition(getSymbolByLabel("update"), initial);
		initial.addTransition(getSymbolByLabel("next"), initial);
		nextating.addTransition(getSymbolByLabel("next"), nextating);
		nextating.addTransition(getSymbolByLabel("update"), updated);
		updated.addTransition(getSymbolByLabel("update"), updated);
		updated.addTransition(getSymbolByLabel("next"), error);
		return initial;
	}

	public void matchCompleted(IVariableBinding<String, Integer> binding) {
	}

}
