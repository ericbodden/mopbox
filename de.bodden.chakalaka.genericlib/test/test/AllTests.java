package test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.bodden.rvlib.generic.IIndexingStrategy;
import de.bodden.rvlib.generic.indexing.StrategyCPlus;

 
@RunWith(Suite.class)
@Suite.SuiteClasses({
  ConnectionClosed.class,
  FailSafeIter.class,
  ASyncContainsAll.class,
  HasNext.class
})
public class AllTests {
	
	@SuppressWarnings("unchecked")
	public final static Class<? extends IIndexingStrategy> STRATEGY_CLASS = StrategyCPlus.class;
	
}