package helpers;

import de.bodden.mopbox.finitestate.DefaultFSMMonitor;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.def.Alphabet;

/**
 * This is a concrete monitor template for the following regular-expression property:
 * <code>close+(c) write(c)</code> 
 */
public class ConnectionClosedMonitorTemplate extends AbstractFSMMonitorTestTemplate<String,String,Object> {

	@Override
	protected IAlphabet<String> createAlphabet() {
		IAlphabet<String> alphabet = new Alphabet<String>();
		alphabet.makeNewSymbol("close");
		alphabet.makeNewSymbol("write");
		return alphabet;
	}

	protected State<String> doCreateAndWireInitialState() {
		State<String> initial = makeState(false);
		State<String> closed = makeState(false);
		State<String> error = makeState(true);
		
		initial.addTransition(getSymbolByLabel("close"), closed);
		closed.addTransition(getSymbolByLabel("close"),closed);
		closed.addTransition(getSymbolByLabel("write"), error);
		return initial;
	}

	@Override
	public DefaultFSMMonitor<String> createMonitorPrototype() {
		return new DefaultFSMMonitor<String>(createAndWireInitialState());
	}

}
