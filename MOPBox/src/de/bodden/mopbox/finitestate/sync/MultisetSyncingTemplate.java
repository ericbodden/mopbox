package de.bodden.mopbox.finitestate.sync;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;

/**
 * An {@link AbstractSyncingFSMMonitorTemplate} that models the gap of events missed as a multiset of missed events.
 */
public abstract class MultisetSyncingTemplate<L, K, V>
	extends AbstractSyncingFSMMonitorTemplate<L, K, V, MultisetSyncingTemplate<L,K,V>.AbstractionAsMultiset>{
		
	public MultisetSyncingTemplate(OpenFSMMonitorTemplate<L, K, V> delegate, int max) {
		super(delegate, max);
	}
	
	protected AbstractionAsMultiset abstraction(Multiset<ISymbol<L, K>> symbols) {
		return new AbstractionAsMultiset(symbols);
	}

	public class AbstractionAsMultiset
		extends AbstractSyncingFSMMonitorTemplate<L,K,V,AbstractionAsMultiset>.SymbolMultisetAbstraction {

		private final Multiset<ISymbol<L, K>> symbols;

		public AbstractionAsMultiset(Multiset<ISymbol<L, K>> symbols) {
			this.symbols = symbols;
		}

		public Multiset<ISymbol<L, K>> getSymbols() {
			return symbols;
		}
		
		@Override
		public String toString() {
			return symbols.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((symbols == null) ? 0 : symbols.hashCode());
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
			AbstractionAsMultiset other = (AbstractionAsMultiset) obj;
			if (symbols == null) {
				if (other.symbols != null)
					return false;
			} else if (!symbols.equals(other.symbols))
				return false;
			return true;
		}
	}	
}