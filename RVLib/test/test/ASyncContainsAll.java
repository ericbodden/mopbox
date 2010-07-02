package test;


import helpers.ASyncContainsAllMonitorTemplate;
import helpers.AbstractFSMMonitorTestTemplate;
import helpers.StringBasedBinding;
import junit.framework.Assert;

import org.junit.Test;

import de.bodden.rvlib.finitestate.DefaultFSMMonitor;
import de.bodden.rvlib.generic.IIndexingStrategy;
import de.bodden.rvlib.generic.def.Event;

public class ASyncContainsAll extends AbstractTest {

	public ASyncContainsAll(Class<IIndexingStrategy<String,String,Object>> indexingStrategyClass) {
		super(indexingStrategyClass);
	}

	@Override
	protected AbstractFSMMonitorTestTemplate<String,String,Object> makeTemplate() {
		return new ASyncContainsAllMonitorTemplate();
	}

	@Test
	public void testSimple() {
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("sync"),new StringBasedBinding("c=c1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("sync"),new StringBasedBinding("d=c2")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("containsAll"),new StringBasedBinding("c=c1,d=c2")));
		Assert.assertEquals("{d=c2, c=c1}", template.getTrace());
	}

}