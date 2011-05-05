package test;


import helpers.AbstractFSMMonitorTestTemplate;
import helpers.HasNextMonitorTemplate;
import helpers.StringBasedBinding;
import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests for the {@link HasNextMonitorTemplate}. 
 */
public class HasNext extends AbstractTest {

	@Override
	protected AbstractFSMMonitorTestTemplate<String,String,Object> makeTemplate() {
		return new HasNextMonitorTemplate();
	}

	@Test
	public void testSimple() {
		template.processEvent("next",new StringBasedBinding("i=i1"));
		template.processEvent("next",new StringBasedBinding("i=i1"));
		Assert.assertEquals("{i=i1}", template.getTrace());
	}

	@Test
	public void testNoMatch() {
		template.processEvent("next",new StringBasedBinding("i=i1"));
		template.processEvent("hasNext",new StringBasedBinding("i=i1"));
		template.processEvent("next",new StringBasedBinding("i=i1"));
		Assert.assertEquals("", template.getTrace());
	}

	@Test
	public void testTwoBindings() {
		template.processEvent("next",new StringBasedBinding("i=i1"));
		template.processEvent("next",new StringBasedBinding("i=i2"));
		template.processEvent("hasNext",new StringBasedBinding("i=i1"));
		template.processEvent("next",new StringBasedBinding("i=i1"));
		template.processEvent("next",new StringBasedBinding("i=i2"));
		Assert.assertEquals("{i=i2}", template.getTrace());
	}
}