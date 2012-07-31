package de.bodden.mopbox.finitestate.sync;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;

/**
 * An {@link AbstractSyncingFSMMonitorTemplate} that models the gap of events missed by the number
 * of missed events.
 */
public abstract class FullSyncingTemplate<L, K, V>
	extends AbstractSyncingFSMMonitorTemplate<L, K, V, FullSyncingTemplate<L,K,V>.FullAbstraction>{
		
	private final FullAbstraction FULL_ABSTRACTION = new FullAbstraction();

	public FullSyncingTemplate(OpenFSMMonitorTemplate<L, K, V> delegate, int max) {
		super(delegate, max);
	}
	
	protected FullAbstraction abstraction(Multiset<ISymbol<L, K>> symbols) {
		return FULL_ABSTRACTION;
	}

	public class FullAbstraction
		extends AbstractSyncingFSMMonitorTemplate<L,K,V,FullAbstraction>.SymbolMultisetAbstraction {

		protected FullAbstraction() {
		}
		
		@Override
		public String toString() {
			return "FullAbstraction";
		}

		@Override
		public int hashCode() {
			return 37;
		}

		@Override
		public boolean equals(Object obj) {
			return obj==this;
		}

	}
		
}