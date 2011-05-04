package helpers;

import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;

/**
 * This is a concrete monitor template for the following regular-expression property:
 * <code>create(c,i) update(c)* iter(i)+ update(c)+ iter(i)</code> 
 */
public class FailSafeIterMonitorTemplate extends AbstractFSMMonitorTestTemplate<String,String,Object> {

	@Override
	protected void fillAlphabet(IAlphabet<String> a) {
		a.makeNewSymbol("create");
		a.makeNewSymbol("update");
		a.makeNewSymbol("iter");
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
