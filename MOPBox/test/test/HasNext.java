package test;


import static helpers.HasNextMonitorTemplate.Var.I;
import helpers.AbstractFSMMonitorTestTemplate;
import helpers.HasNextMonitorTemplate;
import helpers.HasNextMonitorTemplate.Var;
import junit.framework.Assert;

import org.junit.Test;

import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

/**
 * Tests for the {@link HasNextMonitorTemplate}. 
 */
public class HasNext extends AbstractTest<Var> {

	IVariableBinding<Var, Object> v, v2;
	
	public HasNext() {
		v = new VariableBinding<Var, Object>();
		v.put(I, "i1");
		v2 = new VariableBinding<Var, Object>();
		v2.put(I, "i2");
	}
	
	@Override
	protected AbstractFSMMonitorTestTemplate<String,Var,Object> makeTemplate() {
		return new HasNextMonitorTemplate();
	}	
	
	@Test
	public void testSimple() {
		template.processEvent("next", v);
		template.processEvent("next", v);
		Assert.assertEquals("{I=i1}", template.getTrace());
	}

	@Test
	public void testNoMatch() {
		template.processEvent("next", v);
		template.processEvent("hasNext", v);
		template.processEvent("next", v);
		Assert.assertEquals("", template.getTrace());
	}

	@Test
	public void testTwoBindings() {
		template.processEvent("next", v);
		template.processEvent("next", v2);
		template.processEvent("hasNext", v);
		template.processEvent("next", v);
		template.processEvent("next", v2);
		Assert.assertEquals("{I=i2}", template.getTrace());
	}
}