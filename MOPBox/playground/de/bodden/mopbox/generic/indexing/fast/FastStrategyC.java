package de.bodden.mopbox.generic.indexing.fast;


import de.bodden.mopbox.generic.IMonitor;
import de.bodden.mopbox.generic.IMonitorTemplate;

public class FastStrategyC<M extends IMonitor<M,L>,L,K,V> extends FastStrategyCPlus<M,L,K,V> {

	public FastStrategyC(IMonitorTemplate<M,L,K,V> template) {
		super(template,template.getAlphabet().asSet());
	}
	
	
}
