package de.bodden.mopbox.finitestate.sync;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;

/**
 * An {@link AbstractSyncingFSMMonitorTemplate} that models the gap of events missed by the number
 * and kind of missed events.
 */
public abstract class NumberAndSymbolSetSyncingTemplate<L, K, V>
	extends DefaultSyncingFSMMonitorTemplate<L, K, V, NumberAndSymbolSetSyncingTemplate<L,K,V>.AbstractionBySizeAndSymbols>{
		
	public NumberAndSymbolSetSyncingTemplate(OpenFSMMonitorTemplate<L, K, V> delegate) {
		super(delegate);
	}
	
	protected AbstractionBySizeAndSymbols abstraction(Multiset<ISymbol<L, K>> symbols) {
		return new AbstractionBySizeAndSymbols(symbols.size(),symbols.elementSet());
	}

	public class AbstractionBySizeAndSymbols
		extends AbstractSyncingFSMMonitorTemplate<L,K,V,AbstractionBySizeAndSymbols>.SymbolMultisetAbstraction {

		private final int size;
		private final Set<ISymbol<L, K>> symbols;

		protected AbstractionBySizeAndSymbols(int size, Set<ISymbol<L, K>> symbols) {
			this.size = size;
			this.symbols = symbols;
		}

		@Override
		public String toString() {
			return "("+size+","+symbols+")";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + size;
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
			AbstractionBySizeAndSymbols other = (AbstractionBySizeAndSymbols) obj;
			if (size != other.size)
				return false;
			if (symbols == null) {
				if (other.symbols != null)
					return false;
			} else if (!symbols.equals(other.symbols))
				return false;
			return true;
		}

		@Override
		protected AbstractionBySizeAndSymbols add(ISymbol<L, K> sym) {
			HashSet<ISymbol<L, K>> copy = new HashSet<ISymbol<L, K>>(symbols);
			copy.add(sym);
			return new AbstractionBySizeAndSymbols(size+1, copy);
		}

		@Override
		protected boolean isSmallerOrEqualThan(AbstractionBySizeAndSymbols other) {
			return other.symbols.containsAll(symbols) && other.size>=size;
		}
	}	
}