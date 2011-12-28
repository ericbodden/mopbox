package de.bodden.mopbox.finitestate.sync;

import java.util.HashSet;
import java.util.Set;

import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.ISymbol;

/**
 * Idea: can use this technique to buffer event and then process them in bursts.
 */
public abstract class NumberAndSymbolSetSyncingTemplate<L, K, V>
	extends AbstractSyncingFSMMonitorTemplate<L, K, V, NumberAndSymbolSetSyncingTemplate<L,K,V>.NumberSymSetAndSymbol>{
		
	public NumberAndSymbolSetSyncingTemplate(OpenFSMMonitorTemplate<L, K, V> delegate) {
		super(delegate);
	}
	
	protected Set<State<L>> nextFrontier(Set<State<L>> frontier) {
		Set<State<L>> nextFrontier;
		nextFrontier = new HashSet<State<L>>();
		for(State<L> curr : frontier) {
			for (ISymbol<L,K> sym : delegate.getAlphabet()) {
				State<L> succ = curr.successor(sym);
				if(succ!=null)
					nextFrontier.add(succ);
			}
		}
		return nextFrontier;
	}

	protected NumberSymSetAndSymbol makeTransition(int delta, ISymbol<L, K> sym) {
		//TODO FIXME
		return new NumberSymSetAndSymbol(delta, null, sym);
	}
	
	
	public class NumberSymSetAndSymbol
		extends AbstractSyncingFSMMonitorTemplate<L,K,V,NumberSymSetAndSymbol>.ObservationGapInfo {

		private final int number;
		private final Set<ISymbol<L, K>> skippedTransitionSymbols;
		private final ISymbol<L, K> sym;

		protected NumberSymSetAndSymbol(int number, Set<ISymbol<L, K>> skippedTransitionSymbols, ISymbol<L, K> sym) {
			super();
			this.number = number;
			this.skippedTransitionSymbols = skippedTransitionSymbols;
			this.sym = sym;
		}
		
		public int getNumber() {
			return number;
		}
		
		public Set<ISymbol<L, K>> getSkippedTransitionSymbols() {
			return skippedTransitionSymbols;
		}

		public ISymbol<L,K> getCurrentSymbol() {
			return sym;
		}		
		
		@Override
		public String toString() {
			return "("+number+":"+skippedTransitionSymbols+":"+sym+")";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + number;
			result = prime
					* result
					+ ((skippedTransitionSymbols == null) ? 0
							: skippedTransitionSymbols.hashCode());
			result = prime * result + ((sym == null) ? 0 : sym.hashCode());
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
			NumberSymSetAndSymbol other = (NumberSymSetAndSymbol) obj;
			if (number != other.number)
				return false;
			if (skippedTransitionSymbols == null) {
				if (other.skippedTransitionSymbols != null)
					return false;
			} else if (!skippedTransitionSymbols
					.equals(other.skippedTransitionSymbols))
				return false;
			if (sym == null) {
				if (other.sym != null)
					return false;
			} else if (!sym.equals(other.sym))
				return false;
			return true;
		}

	}
}