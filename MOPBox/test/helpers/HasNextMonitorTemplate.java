package helpers;

import de.bodden.mopbox.finitestate.DefaultFSMMonitor;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.def.Alphabet;

/**
 * This is a concrete monitor template for the following regular-expression property:
 * <code>next(i) next(i)</code> over the alphabet <code>{hasNext, next}</code> 
 */
public class HasNextMonitorTemplate extends AbstractFSMMonitorTestTemplate<String,String,Object> {

	@Override
	protected IAlphabet<String> createAlphabet() {
		IAlphabet<String> alphabet = new Alphabet<String>();
		alphabet.makeNewSymbol("hasNext");
		alphabet.makeNewSymbol("next");
		return alphabet;
	}
	
	protected State<String> doCreateAndWireInitialState() {
		State<String> initial = makeState(false);
		State<String> middle = makeState(false);
		State<String> error = makeState(true);
		
		initial.addTransition(getSymbolByLabel("next"), middle);
		middle.addTransition(getSymbolByLabel("next"), error);
		error.addTransition(getSymbolByLabel("next"), error);
		return initial;
	}

	@Override
	public DefaultFSMMonitor<String> createMonitorPrototype() {
		return new DefaultFSMMonitor<String>(createAndWireInitialState());
	}

}
