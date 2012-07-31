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
		
	public FullSyncingTemplate(OpenFSMMonitorTemplate<L, K, V> delegate, int max) {
		super(delegate, max);
	}
	
	protected FullAbstraction abstraction(Multiset<ISymbol<L, K>> symbols) {
		return new FullAbstraction(!symbols.isEmpty());
	}
	
	public class FullAbstraction
		extends AbstractSyncingFSMMonitorTemplate<L,K,V,FullAbstraction>.SymbolMultisetAbstraction {

		private final boolean skippedSomething;

		protected FullAbstraction(boolean skippedSomething) {
			this.skippedSomething = skippedSomething;
		}
		
		@Override
		public String toString() {
			return skippedSomething ? "*" : "{}";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (skippedSomething ? 1231 : 1237);
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FullAbstraction other = (FullAbstraction) obj;
			if (skippedSomething != other.skippedSomething)
				return false;
			return true;
		}

	}
		
}