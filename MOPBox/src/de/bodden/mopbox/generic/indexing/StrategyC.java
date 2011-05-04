package de.bodden.mopbox.generic.indexing;


import java.util.Set;

import de.bodden.mopbox.generic.IMonitor;
import de.bodden.mopbox.generic.IMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;

/**
 * This strategy is known as Algorithm C in the TACAS paper of Feng Chen at al.
 * This algorithm is in O(n*k) where n is the length of the trace and k is
 * bounded by 2^|vars|. Therefore this strategy will be better than {@link StrategyB}
 * for cases where we get many bindings.
 * As mentioned in the ASE paper, Algorithm C is just a special case of Algorithm C+,
 * where the set of creation symbols is selected as the whole alphabet.
 * We follow this implememntation here.
 * @see StrategyCPlus
 * @see #computeCreationSymbols(IMonitorTemplate)
 */
public class StrategyC<M extends IMonitor<M,L>,L,K,V> extends StrategyCPlus<M,L,K,V> {

	public StrategyC(IMonitorTemplate<M,L,K,V> template) {
		super(template);
	}
	
	@Override
	protected Set<ISymbol<L>> computeCreationSymbols(IMonitorTemplate<M,L,K,V> template) {
		//use whole alphabet
		return template.getAlphabet();
	}
	

}
