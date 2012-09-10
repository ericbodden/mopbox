package de.bodden.mopbox.finitestate.sync;

import java.util.Collections;
import java.util.Set;

import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;

public abstract class DefaultSyncingFSMMonitorTemplate<L, K, V, A extends AbstractSyncingFSMMonitorTemplate<L,K,V,A>.SymbolMultisetAbstraction> extends AbstractSyncingFSMMonitorTemplate<L, K, V, A> {

	public DefaultSyncingFSMMonitorTemplate(OpenFSMMonitorTemplate<L, K, V> delegate) {
		super(delegate, 5);
	}

	@Override
	protected int samplingPeriod() {
		return delegate.getStates().size()+2;
	}

	@Override
	protected double samplingRate() {
		return 0.2d;
	}

	@Override
	protected Set<ISymbol<L, K>> criticalSymbols() {
		return Collections.emptySet();
	}
}
