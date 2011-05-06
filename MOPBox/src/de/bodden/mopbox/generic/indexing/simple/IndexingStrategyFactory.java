package de.bodden.mopbox.generic.indexing.simple;

import java.lang.reflect.Constructor;

import de.bodden.mopbox.generic.IIndexingStrategy;
import de.bodden.mopbox.generic.IMonitorTemplate;

/**
 * This factory instantiates an {@link IIndexingStrategy} using reflection.
 */
public class IndexingStrategyFactory<L,K,V> {
	
	@SuppressWarnings("unchecked")
	public IIndexingStrategy<L,K,V> makeStrategyForTemplate(
			Class<?> strategyClass, IMonitorTemplate<?,L,K,V> template) {
		Constructor<?> constructor = strategyClass.getConstructors()[0];
		try {
			IIndexingStrategy<L,K,V> strategy = (IIndexingStrategy<L,K,V>) constructor.newInstance(template);
			return strategy;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
