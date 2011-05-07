package helpers;

import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;

/**
 * This is a concrete monitor template for the following regular-expression property:
 * <code>next(i) next(i)</code> over the alphabet <code>{hasNext, next}</code> 
 */
public class HasNextMonitorTemplate extends AbstractFSMMonitorTestTemplate<String,String,Object> {

	@Override
	protected void fillAlphabet(IAlphabet<String,String> a) {
		a.makeNewSymbol("hasNext");
		a.makeNewSymbol("next");
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
