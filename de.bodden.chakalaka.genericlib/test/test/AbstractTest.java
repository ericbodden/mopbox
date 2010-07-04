package test;
import helpers.AbstractFSMMonitorTestTemplate;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.runners.Parameterized.Parameters;

import de.bodden.rvlib.generic.IIndexingStrategy;
import de.bodden.rvlib.generic.indexing.IndexingStrategyFactory;
import de.bodden.rvlib.generic.indexing.StrategyB;

public abstract class AbstractTest {
	
	protected final static IndexingStrategyFactory<String,String,Object> factory = new IndexingStrategyFactory<String,String,Object>();
	protected AbstractFSMMonitorTestTemplate<String,String,Object> template;
	
	private long before;
	
	@Before
	public void setUp() {
		template = makeTemplate();
	}
	
	protected abstract AbstractFSMMonitorTestTemplate<String,String,Object> makeTemplate();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Parameters
	public static Collection<Class<? extends IIndexingStrategy>[]> makeStrategies() {
		Collection<Class<? extends IIndexingStrategy>[]> res =
			new LinkedList<Class<? extends IIndexingStrategy>[]>();
		res.add(new Class[]{StrategyB.class});
		return res;
	}
	
	protected void begin() {
		before = System.currentTimeMillis();		
	}

	protected void end() {
		long after = System.currentTimeMillis();
		StackTraceElement[] stackTrace = new Exception().getStackTrace();
		System.out.println(stackTrace[1].getMethodName()+" took "+(after-before));
	}
}
