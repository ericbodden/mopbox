package de.bodden.mopbox.finitestate.sync;

import junit.framework.Assert;
import helpers.FailSafeIterMonitorTemplate;
import helpers.FailSafeIterMonitorTemplate.Var;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

public class SyncTests {
	
	protected String trace = "";
	
	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_empty = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_c = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_c2 = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_i = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_ci = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();
	
	FailSafeIterMonitorTemplate fsiNormal;
	FailSafeIterMonitorTemplate fsiNumber;
	MultisetSyncingTemplate<String,Var,Object> multisetSync;
	NumberSyncingTemplate<String,Var,Object> numberSync;

	{
		Object c = new Object() {
			public String toString() {
				return "c";
			}
		};
		Object c2 = new Object() {
			public String toString() {
				return "c2";
			}
		};		
		Object i = new Object() {
			public String toString() {
				return "i1";
			}
		};
		b_c.put(Var.C, c);
		b_c2.put(Var.C, c2);
		b_i.put(Var.I, i);
		b_ci.put(Var.C, c);
		b_ci.put(Var.I, i);
	}

	
	@Before
	public void setUp() throws Exception {
		fsiNormal = new FailSafeIterMonitorTemplate();
		multisetSync = new MultisetSyncingTemplate<String,Var,Object>(new FailSafeIterMonitorTemplate()) {
			protected boolean shouldMonitor(ISymbol<String, Var> symbol,IVariableBinding<Var, Object> binding,Multiset<ISymbol<String, Var>> skippedSymbols) {
				//only skip a single symbol
				return skippedSymbols.size()>0;
			}
			public void matchCompleted(IVariableBinding<Var, Object> binding) {
				trace += binding.toString();				
			}
		};
		numberSync = new NumberSyncingTemplate<String,Var,Object>(new FailSafeIterMonitorTemplate()) {
			protected boolean shouldMonitor(ISymbol<String, Var> symbol,IVariableBinding<Var, Object> binding,Multiset<ISymbol<String, Var>> skippedSymbols) {
				//only skip a single symbol
				return skippedSymbols.size()>0;
			}
			public void matchCompleted(IVariableBinding<Var, Object> binding) {
				trace += binding.toString();				
			}
		};
		trace = "";
	}
	
	
	/*
	 * Here we test that the number-syncing monitor does not match.
	 * This is because we skip the update-event below, and therefore,
	 * because we are only counting the skipped events, do not know whether
	 * the skipped event is actually a an update or iter event.
	 * Since the monitor is a must-monitor, it assumes that
	 * it was not an update, and therefore does not report the violation.  
	 */	
	@Test
	public void numberSimpleCase() {
		numberSync.maybeProcessEvent("update", b_c); //skipped
		fsiNormal.processEvent("update", b_c);

		numberSync.maybeProcessEvent("create", b_ci); //monitored
		fsiNormal.processEvent("create", b_ci);
		
		numberSync.maybeProcessEvent("update", b_c); //skipped
		fsiNormal.processEvent("update", b_c);

		numberSync.maybeProcessEvent("iter", b_i); //monitored
		fsiNormal.processEvent("iter", b_i);

		Assert.assertEquals("",syncTrace());
		Assert.assertEquals("{C=c, I=i1}",normalTrace());
	}
	
	/*
	 * This is a simple test case that validates that the multiset-based monitor matches.  
	 */	
	@Test
	public void multisetSimpleCase() {
		multisetSync.maybeProcessEvent("update", b_c); //skipped
		fsiNormal.processEvent("update", b_c);

		multisetSync.maybeProcessEvent("create", b_ci); //monitored
		fsiNormal.processEvent("create", b_ci);
		
		multisetSync.maybeProcessEvent("update", b_c); //skipped
		fsiNormal.processEvent("update", b_c);

		multisetSync.maybeProcessEvent("iter", b_i); //monitored
		fsiNormal.processEvent("iter", b_i);

		Assert.assertEquals("{C=c, I=i1}",syncTrace());
		Assert.assertEquals("{C=c, I=i1}",normalTrace());
	}

	
	/*
	 * In this test case, the sync template must track bindings in order to not
	 * raise a false positive.
	 * This is because if the binding on the third event (which is skipped) is lost,
	 * the sync monitor template will mistakenly think that c may have been updated. 
	 */
	@Test
	public void avoidFalsePositivesWithMultipleBindings() {
		multisetSync.maybeProcessEvent("update", b_c); //skipped
		fsiNormal.processEvent("update", b_c);

		multisetSync.maybeProcessEvent("create", b_ci); //monitored
		fsiNormal.processEvent("create", b_ci);
		
		multisetSync.maybeProcessEvent("update", b_c2); //skipped, but binding must be kept!
		fsiNormal.processEvent("update", b_c2);

		multisetSync.maybeProcessEvent("iter", b_i); //monitored
		fsiNormal.processEvent("iter", b_i);

		Assert.assertEquals("",syncTrace());
		Assert.assertEquals("",normalTrace());
	}
	
	private String normalTrace() {
		return fsiNormal.getTrace();
	}

	private String syncTrace() {
		return trace;
	}

}
