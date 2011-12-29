package de.bodden.mopbox.finitestate.sync;

import helpers.FailSafeIterMonitorTemplate;
import helpers.FailSafeIterMonitorTemplate.Var;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

/**
 * Idea: can use this technique to buffer event and then process them in bursts.
 */
public abstract class NumberSyncingTemplate<L, K, V>
	extends AbstractSyncingFSMMonitorTemplate<L, K, V, NumberSyncingTemplate<L,K,V>.AbstractionBySize>{
		
	public NumberSyncingTemplate(OpenFSMMonitorTemplate<L, K, V> delegate, int max) {
		super(delegate, max);
	}
	
	protected AbstractionBySize abstraction(Multiset<ISymbol<L, K>> symbols) {
		return new AbstractionBySize(symbols.size());
	}

	public class AbstractionBySize
		extends AbstractSyncingFSMMonitorTemplate<L,K,V,AbstractionBySize>.SymbolMultisetAbstraction {

		private final int size;

		protected AbstractionBySize(int size) {
			this.size = size;
		}
		
		public int getSize() {
			return size;
		}
		
		@Override
		public String toString() {
			return Integer.toString(size);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + size;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			AbstractionBySize other = (AbstractionBySize) obj;
			if (size != other.size)
				return false;
			return true;
		}
		
	}
	
	protected static String trace = "";

	public static void main(String[] args) {
		FailSafeIterMonitorTemplate fsi = new FailSafeIterMonitorTemplate();
		FailSafeIterMonitorTemplate fsi2 = new FailSafeIterMonitorTemplate();
		NumberSyncingTemplate<String,Var,Object> sync = new NumberSyncingTemplate<String,Var,Object>(fsi2,5) {
			public void matchCompleted(IVariableBinding<Var, Object> binding) {
				trace += binding.toString();				
			}
		};
		
		System.out.println(fsi);
		System.out.println(sync);

		Collection<Object> c = new ArrayList<Object>();
		Iterator<Object> i = c.iterator();
		
		final int MAX = 100000;
		
		IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_c = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();
		b_c.put(Var.C, c);

		IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_i = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();
		b_i.put(Var.I, i);

		IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_ci = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();
		b_ci.put(Var.C, c);
		b_ci.put(Var.I, i);
		
		
		fsi.processEvent("create", b_ci);
		//sync.processEvent(sync.new NumberAndSymbol(0, fsi2.getSymbolByLabel("create")), b_ci);
		fsi.processEvent("update", b_c);
		sync.processEvent(sync.new AbstractionAndSymbol(sync.new AbstractionBySize(1),fsi2.getSymbolByLabel("update")), b_c);					
		fsi.processEvent("iter", b_i);
		sync.processEvent(sync.new AbstractionAndSymbol(sync.new AbstractionBySize(0),fsi2.getSymbolByLabel("iter")), b_i);					

		System.out.println("1: "+fsi.getTrace());
		System.out.println("2: "+trace);
	
		
		System.exit(0);
		
		//RANDOMIZED TEST

		Random random = new Random();
		
		int missed = 0;
		for(int n=0; n<MAX; n++) {
			double rand = Math.random();
			boolean miss = random.nextBoolean();
			if(missed>3) miss = false; // TODO how to bound in the general case?
			if(rand<1/3.0) {
				fsi.processEvent("create", b_ci);
				if(miss) {
					missed++;
				} else {
					sync.processEvent(sync.new AbstractionAndSymbol(sync.new AbstractionBySize(missed),fsi2.getSymbolByLabel("create")), b_ci);					
				}
			} else if(rand>=1/3.0 && rand<2/3.0) {
				fsi.processEvent("iter", b_i);
				if(miss) {
					missed++;
				} else {
					sync.processEvent(sync.new AbstractionAndSymbol(sync.new AbstractionBySize(missed),fsi2.getSymbolByLabel("iter")), b_i);					
				}
			} else {
				fsi.processEvent("update", b_c);
				if(miss) {
					missed++;
				} else {
					sync.processEvent(sync.new AbstractionAndSymbol(sync.new AbstractionBySize(missed),fsi2.getSymbolByLabel("update")), b_c);					
				}
			}
			if(!miss) missed = 0;
		}
		System.out.println(fsi.getTrace());
		System.out.println(fsi2.getTrace());
	}

}