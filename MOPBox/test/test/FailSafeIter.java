package test;


import static helpers.FailSafeIterMonitorTemplate.Var.C;
import static helpers.FailSafeIterMonitorTemplate.Var.I;
import helpers.AbstractFSMMonitorTestTemplate;
import helpers.FailSafeIterMonitorTemplate;
import helpers.FailSafeIterMonitorTemplate.Var;
import junit.framework.Assert;

import org.junit.Test;

import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

/**
 * Tests for the {@link FailSafeIterMonitorTemplate}. 
 */
public class FailSafeIter extends AbstractTest<Var> {

	IVariableBinding<Var, Object> c1i1, c1i2, c1, i1, i2;
	
	public FailSafeIter() {
		c1i1 = new VariableBinding<Var, Object>();
		c1i1.put(C, "c1");
		c1i1.put(I, "i1");
		c1i2 = new VariableBinding<Var, Object>();
		c1i2.put(C, "c1");
		c1i2.put(I, "i2");
		c1 = new VariableBinding<Var, Object>();
		c1.put(C, "c1");
		i1 = new VariableBinding<Var, Object>();
		i1.put(I, "i1");
		i2 = new VariableBinding<Var, Object>();
		i2.put(I, "i2");
	}
	
	@Override
	protected AbstractFSMMonitorTestTemplate<String,Var,Object> makeTemplate() {
		return new FailSafeIterMonitorTemplate();
	}

	@Test
	public void testBlub() {
		template.processEvent("create",c1i1);
		template.processEvent("update",c1);
		template.processEvent("iter",i1);
		Assert.assertEquals("{C=c1, I=i1}", template.getTrace());
	}	
	
	@Test
	public void testSimple() {
		template.processEvent("create",c1i1);
		template.processEvent("update",c1);
		template.processEvent("iter",i1);
		Assert.assertEquals("{C=c1, I=i1}", template.getTrace());
	}

	@Test
	public void testMultipleIterators() {
		template.processEvent("create",c1i1);
		template.processEvent("create",c1i2);
		template.processEvent("update",c1);
		template.processEvent("iter",i1);
		template.processEvent("update",c1);
		template.processEvent("iter",i2);
		Assert.assertEquals("{C=c1, I=i1}{C=c1, I=i2}", template.getTrace());
	}
	
	@Test
	public void perfSingleIterManyEvents() {
		begin();
		template.processEvent("create",c1i1);
		for(int i=0;i<10000;i++) {
			template.processEvent("iter",i1);
		}
		end();
		Assert.assertEquals("", template.getTrace());
	}

	@Test
	public void perfManyItersManyEvents() {
		begin();
		for(int i=0;i<1000;i++) {
			IVariableBinding<Var, Object> b = new VariableBinding<Var, Object>();
			b.put(C, "c1");
			b.put(I, ("i"+i).intern());
			template.processEvent("create",b);
			IVariableBinding<Var, Object> b2 = new VariableBinding<Var, Object>();
			b2.put(I, ("i"+i).intern());
			for(int j=0;j<1000;j++) {			
				template.processEvent("iter",b2);
			}
		}
		end();
		template.processEvent("update",c1);
		IVariableBinding<Var, Object> i23 = new VariableBinding<Var, Object>();
		i23.put(I, "i23");
		template.processEvent("iter", i23);
		Assert.assertEquals("{C=c1, I=i23}", template.getTrace());
	}
}