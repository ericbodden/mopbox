package de.bodden.rvlib.finitestate;

import java.util.HashSet;
import java.util.Set;

import de.bodden.rvlib.generic.AbstractMonitorTemplate;
import de.bodden.rvlib.generic.ISymbol;

public abstract class AbstractFSMMonitorTemplate<L,K,V> extends AbstractMonitorTemplate<DefaultFSMMonitor<L>,L,K,V> {

	private int nextStateNum = 0;
	private State<L> initialState;	

	protected State<L> makeState(boolean isFinal) {
		return new State<L>(getAlphabet(),isFinal,Integer.toString(nextStateNum++));
	}
	
	@Override
	public Set<ISymbol<L>> computeCreationSymbols() {
		;
		Set<ISymbol<L>> creationSyms = new HashSet<ISymbol<L>>();
		for(ISymbol<L> s: getAlphabet()) {
			State<L> target = initialState().targetFor(s);
			if(target!=null && !target.equals(initialState())){
				creationSyms.add(s);
			}
		}
		return creationSyms;
	}
	
	@Override
	public DefaultFSMMonitor<L> createMonitorPrototype() {
		return new DefaultFSMMonitor<L>(initialState());
	}

	private State<L> initialState() {
		if(initialState==null) {
			initialState = createAndWireInitialState();
		}
		return initialState;
	}
	
	protected abstract State<L> createAndWireInitialState();

}
