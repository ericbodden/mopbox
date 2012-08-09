package de.bodden.mopbox.finitestate.sync;

import helpers.FailSafeIterMonitorTemplate;
import helpers.FailSafeIterMonitorTemplate.Var;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

public class SyncTests3 {
	
	String trace = "";
	
	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_empty = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_c = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

//	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_c2 = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_i = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_i2 = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_ci = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_ci2 = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	FailSafeIterMonitorTemplate fsiNormal;
	FailSafeIterMonitorTemplate fsiNumber;
	SymbolSetSyncingTemplate<String,Var,Object> symbolSetSync;
	
	int eventCount;
	int[] eventsToSkip = {3};

	{
		Object c = new Object() {
			public String toString() {
				return "c";
			}
		};
//		Object c2 = new Object() {
//			public String toString() {
//				return "c2";
//			}
//		};		
		Object i = new Object() {
			public String toString() {
				return "i1";
			}
		};
		Object i2 = new Object() {
			public String toString() {
				return "i2";
			}
		};
		b_c.put(Var.C, c);
//		b_c2.put(Var.C, c2);
		b_i.put(Var.I, i);
		b_i2.put(Var.I, i2);
		b_ci.put(Var.C, c);
		b_ci.put(Var.I, i);
		b_ci2.put(Var.C, c);
		b_ci2.put(Var.I, i2);
	}

	
	@Before
	public void setUp() throws Exception {
		fsiNormal = new FailSafeIterMonitorTemplate();
		symbolSetSync = new SymbolSetSyncingTemplate<String,Var,Object>(new FailSafeIterMonitorTemplate(),2) {
			protected boolean shouldMonitor(ISymbol<String, Var> symbol,IVariableBinding<Var, Object> binding,Multiset<ISymbol<String, Var>> skippedSymbols) {
				return !shouldSkip();
			}
			public void matchCompleted(IVariableBinding<Var, Object> binding) {
				trace += binding.toString();				
			}
		};
		trace = "";
		eventCount = 1;
	}

	@Test
	public void setBasedAbstractionMatch() {
		symbolSetSync.maybeProcessEvent("create", b_ci); 

		System.err.println(syncTrace());
//		Assert.assertEquals("",syncTrace());
		
		symbolSetSync.maybeProcessEvent("create", b_ci2); 

		System.err.println(syncTrace());
//		Assert.assertEquals("",syncTrace());

		symbolSetSync.maybeProcessEvent("update", b_c); 

		System.err.println(syncTrace());
//		Assert.assertEquals("",syncTrace());

		symbolSetSync.maybeProcessEvent("iter", b_i); 

		symbolSetSync.maybeProcessEvent("iter", b_i); 

		System.err.println(syncTrace());
//		Assert.assertEquals("",syncTrace());

		symbolSetSync.maybeProcessEvent("iter", b_i2); 

		System.err.println(syncTrace());
//		Assert.assertEquals("{C=c, I=i1}",syncTrace());
	}
	

	private String syncTrace() {
		return trace;
	}

	private boolean shouldSkip() {
		boolean ret = Arrays.binarySearch(eventsToSkip, eventCount)>-1;
		eventCount++;
		return ret;
	}

}
