package de.bodden.rvlib.finitestate;

/**
 * The default implementation for a finite-state runtime monitor.
 *
 * @param <L>
 */
public class DefaultFSMMonitor<L> extends AbstractFSMMonitor<DefaultFSMMonitor<L>,L>{

		public DefaultFSMMonitor(State<L> initialState) {
			super(initialState);
		}

}