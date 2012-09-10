package de.bodden.mopbox.finitestate.sync;

import java.util.Set;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;

/**
 * An {@link AbstractSyncingFSMMonitorTemplate} that models the gap of events missed by the set of symbols that were missed.
 */
public abstract class SymbolSetSyncingTemplate<L, K, V>
	extends DefaultSyncingFSMMonitorTemplate<L, K, V, SymbolSetSyncingTemplate<L,K,V>.AbstractionBySymbolSet>{
		
	public SymbolSetSyncingTemplate(OpenFSMMonitorTemplate<L, K, V> delegate, int max) {
		super(delegate, max);
	}
	
	protected AbstractionBySymbolSet abstraction(Multiset<ISymbol<L, K>> symbols) {
		return new AbstractionBySymbolSet(symbols.elementSet());
	}

	public class AbstractionBySymbolSet
		extends AbstractSyncingFSMMonitorTemplate<L,K,V,AbstractionBySymbolSet>.SymbolMultisetAbstraction {

		private final Set<ISymbol<L, K>> symbols;

		protected AbstractionBySymbolSet(Set<ISymbol<L, K>> symbols) {
			this.symbols = symbols;
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
			AbstractionBySymbolSet other = (AbstractionBySymbolSet) obj;
			if (symbols == null) {
				if (other.symbols != null)
					return false;
			} else if (!symbols.equals(other.symbols))
				return false;
			return true;
		}
	}	
}