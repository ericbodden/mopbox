package de.bodden.mopbox.finitestate.sync;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;

/**
 * An {@link AbstractSyncingFSMMonitorTemplate} that models the gap of events missed by the number
 * of missed events.
 */
public abstract class NumberSyncingTemplate<L, K, V>
	extends DefaultSyncingFSMMonitorTemplate<L, K, V, NumberSyncingTemplate<L,K,V>.AbstractionBySize>{
		
	public NumberSyncingTemplate(OpenFSMMonitorTemplate<L, K, V> delegate) {
		super(delegate);
	}
	
	protected AbstractionBySize abstraction(Multiset<ISymbol<L, K>> symbols) {
		return new AbstractionBySize(symbols.size());
	}

	public class AbstractionBySize
		extends AbstractSyncingFSMMonitorTemplate<L,K,V,AbstractionBySize>.SymbolMultisetAbstraction {

		private final int size;

		protected AbstractionBySize(int size) {
			this.size = size;
		}
		
		@Override
		public String toString() {
			return Integer.toString(size);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + size;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			AbstractionBySize other = (AbstractionBySize) obj;
			if (size != other.size)
				return false;
			return true;
		}
		
	}
		
}