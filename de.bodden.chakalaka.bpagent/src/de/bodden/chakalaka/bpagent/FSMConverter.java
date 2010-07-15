package de.bodden.chakalaka.bpagent;

import de.bodden.chakalaka.bpagentshared.IFSM;
import de.bodden.rvlib.finitestate.AbstractFSMMonitorTemplate;
import de.bodden.rvlib.finitestate.DefaultFSMMonitor;
import de.bodden.rvlib.finitestate.State;
import de.bodden.rvlib.generic.IAlphabet;
import de.bodden.rvlib.generic.IIndexingStrategy;
import de.bodden.rvlib.generic.ISymbol;
import de.bodden.rvlib.generic.IVariableBinding;
import de.bodden.rvlib.generic.def.Alphabet;
import de.bodden.rvlib.generic.indexing.StrategyC;

public class FSMConverter {

	public static void installFSM(final IFSM fsm) {		
		AbstractFSMMonitorTemplate<String, String, Object> template = new AbstractFSMMonitorTemplate<String, String, Object>() {

			@Override
			public void matchCompleted(IVariableBinding<String, Object> binding) {
				System.err.println("MATCHED!");
			}

			@Override
			protected State<String> doCreateAndWireInitialState() {
				int MAX = fsm.numberOfStates();
				@SuppressWarnings("unchecked")
				State<String>[] states = new State[MAX];
				for(int i=0; i<MAX; i++) {
					//last state is final state
					boolean finalState = i==MAX-1;
					State<String> state = super.makeState(finalState);
					states[i] = state;
				}
				for (int i = 0; i < states.length; i++) {
					State<String> state = states[i];
					for(ISymbol<String> sym: getAlphabet()) {
						int succ = fsm.succ(i, sym.getLabel());
						if(succ>-1) {	//if there is a successor
							state.addTransition(sym, states[succ]);
						}
					}
				}
				//first state is initial state
				return states[0];
			}

			@Override
			protected IIndexingStrategy<String, String, Object> createIndexingStrategy() {
				return new StrategyC<DefaultFSMMonitor<String>, String, String, Object>(this);
			}

			@Override
			protected IAlphabet<String> createAlphabet() {
				Alphabet<String>  a = new Alphabet<String>();
				for(String s: fsm.symbols()) {
					a.add(super.makeNewSymbol(s));
				}
				return a;
			}
		};
		
		Registry.v().registerMonitor(fsm, template);
		
		System.out.println("New monitor template installed.");
	}

}
