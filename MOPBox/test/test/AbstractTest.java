package test;
import helpers.AbstractFSMMonitorTestTemplate;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.runners.Parameterized.Parameters;

import de.bodden.mopbox.generic.IIndexingStrategy;
import de.bodden.mopbox.generic.IMonitorTemplate;
import de.bodden.mopbox.generic.indexing.simple.IndexingStrategyFactory;
import de.bodden.mopbox.generic.indexing.simple.StrategyB;

/**
 * An abstract super class for JUnit tests of this library.
 * It instantiates {@link IMonitorTemplate}s and {@link IIndexingStrategy}.
 * 
 * Also provides methods to record and report timing information.
 */
public abstract class AbstractTest<V> {
	
	@SuppressWarnings("rawtypes")
	protected final static IndexingStrategyFactory factory = new IndexingStrategyFactory();
	protected AbstractFSMMonitorTestTemplate<String,V,Object> template;
	
	private long before;
	
	@Before
	public void setUp() {
		template = makeTemplate();
	}
	
	/**
	 * Subclasses must implement this method to return the concrete monitor template that is to be used.
	 */
	protected abstract AbstractFSMMonitorTestTemplate<String,V,Object> makeTemplate();
	
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
