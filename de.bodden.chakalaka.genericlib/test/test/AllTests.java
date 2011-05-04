package test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.bodden.mopbox.generic.IIndexingStrategy;
import de.bodden.mopbox.generic.indexing.StrategyC;

 
/**
 * A test suite encapsulating all current unit tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  ConnectionClosed.class,
  FailSafeIter.class,
  ASyncContainsAll.class,
  HasNext.class
})
public class AllTests {
	
	@SuppressWarnings("rawtypes")
	public final static Class<? extends IIndexingStrategy> STRATEGY_CLASS = StrategyC.class;
	
}