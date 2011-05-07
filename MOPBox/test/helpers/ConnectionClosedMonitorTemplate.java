package helpers;

import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;

/**
 * This is a concrete monitor template for the following regular-expression property:
 * <code>close+(c) write(c)</code> 
 */
public class ConnectionClosedMonitorTemplate extends AbstractFSMMonitorTestTemplate<String,String,Object> {

	@Override
	protected void fillAlphabet(IAlphabet<String,String> a) {
		a.makeNewSymbol("close");
		a.makeNewSymbol("write");
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
