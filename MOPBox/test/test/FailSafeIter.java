package test;


import helpers.AbstractFSMMonitorTestTemplate;
import helpers.FailSafeIterMonitorTemplate;
import helpers.StringBasedBinding;
import junit.framework.Assert;

import org.junit.Test;

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
		template.processEvent("create",new StringBasedBinding("c=c1,i=i1"));
		template.processEvent("update",new StringBasedBinding("c=c1"));
		template.processEvent("iter",new StringBasedBinding("i=i1"));
		Assert.assertEquals("{c=c1, i=i1}", template.getTrace());
	}	
	
	@Test
	public void testSimple() {
		template.processEvent("create",new StringBasedBinding("c=c1,i=i1"));
		template.processEvent("update",new StringBasedBinding("c=c1"));
		template.processEvent("iter",new StringBasedBinding("i=i1"));
		Assert.assertEquals("{c=c1, i=i1}", template.getTrace());
	}

	@Test
	public void testMultipleIterators() {
		template.processEvent("create",new StringBasedBinding("c=c1,i=i1"));
		template.processEvent("create",new StringBasedBinding("c=c1,i=i2"));
		template.processEvent("update",new StringBasedBinding("c=c1"));
		template.processEvent("iter",new StringBasedBinding("i=i1"));
		template.processEvent("update",new StringBasedBinding("c=c1"));
		template.processEvent("iter",new StringBasedBinding("i=i2"));
		Assert.assertEquals("{c=c1, i=i1}{c=c1, i=i2}", template.getTrace());
	}
	
	@Test
	public void perfSingleIterManyEvents() {
		begin();
		template.processEvent("create",new StringBasedBinding("c=c1,i=i1"));
		StringBasedBinding binding = new StringBasedBinding("i=i1");
		for(int i=0;i<10000;i++) {
			template.processEvent("iter",binding);
		}
		end();
		Assert.assertEquals("", template.getTrace());
	}

	@Test
	public void perfManyItersManyEvents() {
		begin();
		for(int i=0;i<1000;i++) {
			template.processEvent("create",new StringBasedBinding("c=c1,i=i"+i));
			StringBasedBinding binding = new StringBasedBinding("i=i"+i);
			for(int j=0;j<1000;j++) {			
				template.processEvent("iter",binding);
			}
		}
		end();
		template.processEvent("update",new StringBasedBinding("c=c1"));
		template.processEvent("iter",new StringBasedBinding("i=i23"));
		Assert.assertEquals("{c=c1, i=i23}", template.getTrace());
	}
}