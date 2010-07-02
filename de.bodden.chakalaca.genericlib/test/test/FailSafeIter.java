package test;


import helpers.AbstractFSMMonitorTestTemplate;
import helpers.FailSafeIterMonitorTemplate;
import helpers.StringBasedBinding;
import junit.framework.Assert;

import org.junit.Test;

import de.bodden.rvlib.finitestate.DefaultFSMMonitor;
import de.bodden.rvlib.generic.IIndexingStrategy;
import de.bodden.rvlib.generic.def.Event;

public class FailSafeIter extends AbstractTest {

	public FailSafeIter(Class<IIndexingStrategy<String,String,Object>> indexingStrategyClass) {
		super(indexingStrategyClass);
	}

	@Override
	protected AbstractFSMMonitorTestTemplate<String,String,Object> makeTemplate() {
		return new FailSafeIterMonitorTemplate();
	}

	@Test
	public void testSimple() {
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("create"),new StringBasedBinding("c=c1,i=i1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("update"),new StringBasedBinding("c=c1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("iter"),new StringBasedBinding("i=i1")));
		Assert.assertEquals("{c=c1, i=i1}", template.getTrace());
	}

	@Test
	public void testMultipleIterators() {
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("create"),new StringBasedBinding("c=c1,i=i1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("create"),new StringBasedBinding("c=c1,i=i2")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("update"),new StringBasedBinding("c=c1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("iter"),new StringBasedBinding("i=i1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("update"),new StringBasedBinding("c=c1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("iter"),new StringBasedBinding("i=i2")));
		Assert.assertEquals("{c=c1, i=i1}{c=c1, i=i2}", template.getTrace());
	}
}