package de.bodden.mopbox.finitestate.sync;

import helpers.FailSafeIterMonitorTemplate;
import helpers.FailSafeIterMonitorTemplate.Var;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

public class SyncTests {
	
	protected static String trace = "";
	
	static IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_empty = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	static IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_c = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	static IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_c2 = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	static IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_i = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();

	static IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_ci = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();
	
	static {
		Collection<Object> c = new ArrayList<Object>();
		Collection<Object> c2 = new ArrayList<Object>();
		Iterator<Object> i = c.iterator();
		b_c.put(Var.C, c);
		b_c2.put(Var.C, c2);
		b_i.put(Var.I, i);
		b_ci.put(Var.C, c);
		b_ci.put(Var.I, i);
	}

	public static void main(String[] args) {
		FailSafeIterMonitorTemplate fsi = new FailSafeIterMonitorTemplate();
		FailSafeIterMonitorTemplate fsi2 = new FailSafeIterMonitorTemplate();
		MultisetSyncingTemplate<String,Var,Object> sync = new MultisetSyncingTemplate<String,Var,Object>(fsi,2) {
			protected boolean shouldMonitor(ISymbol<String, Var> symbol,IVariableBinding<Var, Object> binding,Multiset<ISymbol<String, Var>> skippedSymbols) {
				//only skip a single symbol
				return skippedSymbols.size()>0;
			}
			public void matchCompleted(IVariableBinding<Var, Object> binding) {
				trace += binding.toString();				
			}
		};
		
		System.out.println(fsi);
		System.out.println("===============");
		System.out.println(sync);

		falsePositive(fsi2, sync);
	}

	/*
	 * In this test case, the sync template will raise a false positive.
	 * This is because it currently loses the binding on the third event (which is skipped), and
	 * therefore believes that collection c may have been updated. 
	 */
	protected static void falsePositive(FailSafeIterMonitorTemplate fsi2, MultisetSyncingTemplate<String, Var, Object> sync) {
		sync.maybeProcessEvent("update", b_c); //skipped
		fsi2.processEvent("update", b_c);

		sync.maybeProcessEvent("create", b_ci); //monitored
		fsi2.processEvent("create", b_ci);
		
		sync.maybeProcessEvent("update", b_c2); //skipped, binding lost
		fsi2.processEvent("update", b_c2);

		sync.maybeProcessEvent("iter", b_i); //monitored
		fsi2.processEvent("iter", b_i);

		System.err.println("Sync:   "+trace);
		System.err.println("Normal: "+fsi2.getTrace());
	}


}
