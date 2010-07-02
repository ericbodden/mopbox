package test;
import helpers.AbstractFSMMonitorTestTemplate;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.bodden.rvlib.generic.IIndexingStrategy;
import de.bodden.rvlib.generic.indexing.IndexingStrategyFactory;
import de.bodden.rvlib.generic.indexing.StrategyB;

@RunWith(Parameterized.class)
public abstract class AbstractTest {
	
	protected final Class<IIndexingStrategy<String,String,Object>> indexingStrategyClass;
	protected final static IndexingStrategyFactory<String,String,Object> factory = new IndexingStrategyFactory<String,String,Object>();
	protected AbstractFSMMonitorTestTemplate<String,String,Object> template;

	public AbstractTest(Class<IIndexingStrategy<String,String,Object>> indexingStrategyClass) {
		this.indexingStrategyClass = indexingStrategyClass;
	}	
	
	@Before
	public void setUp() {
		template = makeTemplate();
		factory.makeStrategyForTemplate(indexingStrategyClass, template);
	}
	
	protected abstract AbstractFSMMonitorTestTemplate<String,String,Object> makeTemplate();
	
	@SuppressWarnings("unchecked")
	@Parameters
	public static Collection<Class<? extends IIndexingStrategy>[]> makeStrategies() {
		Collection<Class<? extends IIndexingStrategy>[]> res =
			new LinkedList<Class<? extends IIndexingStrategy>[]>();
		res.add(new Class[]{StrategyB.class});
		return res;
	}
}
