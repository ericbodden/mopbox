package test;


import helpers.AbstractFSMMonitorTestTemplate;
import helpers.HasNextMonitorTemplate;
import helpers.StringBasedBinding;
import junit.framework.Assert;

import org.junit.Test;

import de.bodden.rvlib.finitestate.DefaultFSMMonitor;
import de.bodden.rvlib.generic.IIndexingStrategy;
import de.bodden.rvlib.generic.def.Event;

public class HasNext extends AbstractTest {

	public HasNext(Class<IIndexingStrategy<String,String,Object>> indexingStrategyClass) {
		super(indexingStrategyClass);
	}

	@Override
	protected AbstractFSMMonitorTestTemplate<String,String,Object> makeTemplate() {
		return new HasNextMonitorTemplate();
	}

	@Test
	public void testSimple() {
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("next"),new StringBasedBinding("i=i1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("next"),new StringBasedBinding("i=i1")));
		Assert.assertEquals("{i=i1}", template.getTrace());
	}

	@Test
	public void testNoMatch() {
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("next"),new StringBasedBinding("i=i1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("hasNext"),new StringBasedBinding("i=i1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("next"),new StringBasedBinding("i=i1")));
		Assert.assertEquals("", template.getTrace());
	}

	@Test
	public void testTwoBindings() {
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("next"),new StringBasedBinding("i=i1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("next"),new StringBasedBinding("i=i2")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("hasNext"),new StringBasedBinding("i=i1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("next"),new StringBasedBinding("i=i1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("next"),new StringBasedBinding("i=i2")));
		Assert.assertEquals("{i=i2}", template.getTrace());
	}
}