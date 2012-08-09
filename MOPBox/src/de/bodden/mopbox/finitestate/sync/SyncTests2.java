package de.bodden.mopbox.finitestate.sync;

import java.util.Arrays;

import junit.framework.Assert;
import helpers.FailSafeIterMonitorTemplate;
import helpers.FailSafeIterMonitorTemplate.Var;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

public class SyncTests2 {
	
	String trace = "";
	
	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_empty = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_c = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_c2 = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_i = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_ci = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();
	
	FailSafeIterMonitorTemplate fsiNormal;
	FailSafeIterMonitorTemplate fsiNumber;
	MultisetSyncingTemplate<String,Var,Object> multisetSync;
	SymbolSetSyncingTemplate<String,Var,Object> symbolSetSync;
	FullSyncingTemplate<String,Var,Object> fullSync;
	
	int eventCount;
	int[] eventsToSkip = {2,5};

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
//		multisetSync = new MultisetSyncingTemplate<String,Var,Object>(new FailSafeIterMonitorTemplate(),2) {
//			protected boolean shouldMonitor(ISymbol<String, Var> symbol,IVariableBinding<Var, Object> binding,Multiset<ISymbol<String, Var>> skippedSymbols) {				
//				return !shouldSkip();
//			}
//			public void matchCompleted(IVariableBinding<Var, Object> binding) {
//				trace += binding.toString();				
//			}
//		};
//		symbolSetSync = new SymbolSetSyncingTemplate<String,Var,Object>(new FailSafeIterMonitorTemplate(),2) {
//			protected boolean shouldMonitor(ISymbol<String, Var> symbol,IVariableBinding<Var, Object> binding,Multiset<ISymbol<String, Var>> skippedSymbols) {
//				return !shouldSkip();
//			}
//			public void matchCompleted(IVariableBinding<Var, Object> binding) {
//				trace += binding.toString();				
//			}
//		};
		fullSync = new FullSyncingTemplate<String,Var,Object>(new FailSafeIterMonitorTemplate(),2) {
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

//	@Test
//	public void setBasedAbstractionMatch() {
//		symbolSetSync.maybeProcessEvent("create", b_ci); 
//
//		Assert.assertEquals("",syncTrace());
//		
//		symbolSetSync.maybeProcessEvent("update", b_c); 
//
//		Assert.assertEquals("",syncTrace());
//
//		symbolSetSync.maybeProcessEvent("iter", b_i); 
//
//		Assert.assertEquals("",syncTrace());
//
//		symbolSetSync.maybeProcessEvent("update", b_c); 
//
//		Assert.assertEquals("",syncTrace());
//
//		symbolSetSync.maybeProcessEvent("iter", b_i); 
//
//		Assert.assertEquals("",syncTrace());
//
//		symbolSetSync.maybeProcessEvent("iter", b_i); 
//
//		Assert.assertEquals("{C=c, I=i1}",syncTrace());
//	}
	
	@Test
	public void fullAbstractionMatch() {
		fullSync.maybeProcessEvent("create", b_ci); 

		Assert.assertEquals("",syncTrace());
		
		fullSync.maybeProcessEvent("update", b_c); 

		Assert.assertEquals("",syncTrace());

		fullSync.maybeProcessEvent("iter", b_i); 

		Assert.assertEquals("",syncTrace());

		fullSync.maybeProcessEvent("update", b_c); 

		Assert.assertEquals("",syncTrace());

		fullSync.maybeProcessEvent("iter", b_i); 

		Assert.assertEquals("",syncTrace());

		fullSync.maybeProcessEvent("iter", b_i); 

		Assert.assertEquals("{C=c, I=i1}",syncTrace());
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
