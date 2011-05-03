package test;


import helpers.AbstractFSMMonitorTestTemplate;
import helpers.ConnectionClosedMonitorTemplate;
import junit.framework.Assert;

import org.junit.Test;

import de.bodden.rvlib.finitestate.DefaultFSMMonitor;
import de.bodden.rvlib.generic.IVariableBinding;
import de.bodden.rvlib.generic.def.Event;
import de.bodden.rvlib.generic.def.VariableBinding;

public class ConnectionClosed extends AbstractTest {

	IVariableBinding<String,Object> v,v2;
	
	public ConnectionClosed() {
		v = new VariableBinding<String,Object>();
		v.put("c", new Object() {
			@Override
			public String toString() {
				return "c1";
			}
		});
		v2 = new VariableBinding<String,Object>();
		v2.put("c", new Object() {
			@Override
			public String toString() {
				return "c2";
			}
		});}
	
	protected AbstractFSMMonitorTestTemplate<String,String,Object> makeTemplate() {
		return new ConnectionClosedMonitorTemplate();
	}


	@Test
	public void testSimple() {
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("close"),v));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("write"),v2));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("close"),v2));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("write"),v2));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("write"),v));	
		Assert.assertEquals("{c=c1}", template.getTrace());
	}

	@Test
	public void testInterleaving() {
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("close"),v));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("write"),v2));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("close"),v2));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("write"),v2));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("write"),v));	
		Assert.assertEquals("{c=c1}", template.getTrace());
	}

	@Test
	public void testTwoMatches() {
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("close"),v));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("close"),v2));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("write"),v2));
		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("write"),v));	
		Assert.assertEquals("{c=c2}{c=c1}", template.getTrace());
	}

//	@Test
//	public void testWriteFirst() {
//		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("write"),v));
//		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("close"),v));
//		template.processEvent(new Event<DefaultFSMMonitor<String>,String,String,Object>(template.getSymbolByLabel("write"),v));	
//		Assert.assertEquals("", template.getTrace());
//	}

}
