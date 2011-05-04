package test;


import helpers.AbstractFSMMonitorTestTemplate;
import helpers.FailSafeIterMonitorTemplate;
import helpers.StringBasedBinding;
import junit.framework.Assert;

import org.junit.Test;

import de.bodden.mopbox.finitestate.DefaultFSMMonitor;
import de.bodden.mopbox.generic.def.Event;

/**
 * Tests for the {@link FailSafeIterMonitorTemplate}. 
 */
public class FailSafeIter extends AbstractTest {

	@Override
	protected AbstractFSMMonitorTestTemplate<String,String,Object> makeTemplate() {
		return new FailSafeIterMonitorTemplate();
	}

	@Test
	public void testBlub() {
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("create"),new StringBasedBinding("c=c1,i=i1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("update"),new StringBasedBinding("c=c1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("iter"),new StringBasedBinding("i=i1")));
		Assert.assertEquals("{c=c1, i=i1}", template.getTrace());
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
	
	@Test
	public void perfSingleIterManyEvents() {
		begin();
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("create"),new StringBasedBinding("c=c1,i=i1")));
		Event<DefaultFSMMonitor<String>,String,String,Object> event = new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("iter"),new StringBasedBinding("i=i1"));
		for(int i=0;i<10000;i++) {
			template.processEvent(event);
		}
		end();
		Assert.assertEquals("", template.getTrace());
	}

	@Test
	public void perfManyItersManyEvents() {
		begin();
		for(int i=0;i<1000;i++) {
			template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("create"),new StringBasedBinding("c=c1,i=i"+i)));
			Event<DefaultFSMMonitor<String>,String,String,Object> e = new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("iter"),new StringBasedBinding("i=i"+i));
			for(int j=0;j<1000;j++) {			
				template.processEvent(e);
			}
		}
		end();
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("update"),new StringBasedBinding("c=c1")));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("iter"),new StringBasedBinding("i=i23")));
		Assert.assertEquals("{c=c1, i=i23}", template.getTrace());
	}
}