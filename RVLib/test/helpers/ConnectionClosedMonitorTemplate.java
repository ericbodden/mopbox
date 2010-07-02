package helpers;

import de.bodden.rvlib.finitestate.DefaultFSMMonitor;
import de.bodden.rvlib.finitestate.State;
import de.bodden.rvlib.generic.IAlphabet;
import de.bodden.rvlib.generic.ISymbol;
import de.bodden.rvlib.generic.def.Alphabet;

public class ConnectionClosedMonitorTemplate extends AbstractFSMMonitorTestTemplate<String,String,Object> {

	@Override
	protected IAlphabet<String> createAlphabet() {
		Alphabet<String> alphabet = new Alphabet<String>();
		ISymbol<String> close = makeNewSymbol("close");
		alphabet.add(close);
		ISymbol<String> write = makeNewSymbol("write");
		alphabet.add(write);
		return alphabet;
	}

	protected State<String> createAndWireInitialState() {
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
