package helpers;

import test.AllTests;
import de.bodden.rvlib.finitestate.AbstractFSMMonitorTemplate;
import de.bodden.rvlib.generic.IIndexingStrategy;
import de.bodden.rvlib.generic.IVariableBinding;
import de.bodden.rvlib.generic.indexing.IndexingStrategyFactory;

public abstract class AbstractFSMMonitorTestTemplate<L,K,V> extends AbstractFSMMonitorTemplate<L,K,V> {
	
	@SuppressWarnings("unchecked")
	protected final static IndexingStrategyFactory factory = new IndexingStrategyFactory();
	protected String trace = "";
	
	@Override
	public void matchCompleted(IVariableBinding<K,V> binding) {
		trace += binding.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected IIndexingStrategy<L,K,V> createIndexingStrategy() {
		return factory.makeStrategyForTemplate(AllTests.STRATEGY_CLASS, this);
	}
	
	public String getTrace() {
		return trace;
	}
	
}
