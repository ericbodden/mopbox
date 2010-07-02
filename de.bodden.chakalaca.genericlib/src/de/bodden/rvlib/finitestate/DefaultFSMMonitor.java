package de.bodden.rvlib.finitestate;

public class DefaultFSMMonitor<L> extends AbstractFSMMonitor<DefaultFSMMonitor<L>,L>{

		public DefaultFSMMonitor(State<L> initialState) {
			super(initialState);
		}

}