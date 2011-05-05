package test;


import helpers.ASyncContainsAllMonitorTemplate;
import helpers.AbstractFSMMonitorTestTemplate;
import helpers.StringBasedBinding;
import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests for the {@link ASyncContainsAllMonitorTemplate}. 
 */
public class ASyncContainsAll extends AbstractTest {

	@Override
	protected AbstractFSMMonitorTestTemplate<String,String,Object> makeTemplate() {
		return new ASyncContainsAllMonitorTemplate();
	}

	@Test
	public void testSimple() {
		template.processEvent("sync",new StringBasedBinding("c=c1"));
		template.processEvent("sync",new StringBasedBinding("d=c2"));
		template.processEvent("containsAll",new StringBasedBinding("c=c1,d=c2"));
		Assert.assertEquals("{c=c1, d=c2}", template.getTrace());
	}

}