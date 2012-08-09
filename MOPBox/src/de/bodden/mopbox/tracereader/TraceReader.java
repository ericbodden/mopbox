package de.bodden.mopbox.tracereader;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Multiset;

import de.bodden.mopbox.finitestate.AbstractFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.sync.AbstractSyncingFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.sync.SymbolSetSyncingTemplate;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

/*
 * This is a variant of the Hello World example in which events are read from a file.
 */
public class TraceReader {

	public static void main(String[] args) throws IOException {
		if(args.length!=2) {
			System.err.println("USAGE: <pathToTraceFile> <propertyName>");
		}
		
		String filePath = args[0];
		String propName = args[1];
		
		AbstractSyncingFSMMonitorTemplate<String, String, Integer, ?> syncingTemplate = null;
		AbstractFSMMonitorTemplate<String, String, Integer> innerTemplate = null;
		if(propName.equals("fsi")) {
			FailSafeIterMonitorTemplate failSafeIterMonitorTemplate = new FailSafeIterMonitorTemplate() {
				@Override
				public IVariableBinding<String, Integer> createEmptyBinding() {
					return new EqualsBinding();
				}
			};
			innerTemplate = failSafeIterMonitorTemplate;
			syncingTemplate = new SymbolSetSyncingTemplate<String, String, Integer>(failSafeIterMonitorTemplate, 5) {

				@Override
				public void matchCompleted(IVariableBinding<String, Integer> binding) {
					System.err.println("MATCH!");
				}

				@Override
				protected boolean shouldMonitor(ISymbol<String, String> symbol,
						IVariableBinding<String, Integer> binding,
						Multiset<ISymbol<String, String>> skippedSymbols) {
					//TODO fill in logic
					return true;
				}
			};

		}
		if(innerTemplate==null)
			System.exit(1);

		Set<String> symbols = new HashSet<String>();		
		for (ISymbol<String, String> sym: innerTemplate.getAlphabet().asSet()) {
			symbols.add(sym.getLabel());
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));		
		String line;
		System.out.println("file: "+filePath);
		while((line=reader.readLine())!=null) {
			//skip non-symbol lines
			boolean foundSym = false;
			for(String sym: symbols) {
				if(line.startsWith(sym+" ")) {
					foundSym = true;
					break;
				}
			}
			if(!foundSym) continue;
			//
			
			String[] split = line.split(" ");
			
			String symbolName = split[0];
			ISymbol<String, String> symbol = innerTemplate.getAlphabet().getSymbolByLabel(symbolName);
			String[] variableOrder = symbol.getVariables();

			IVariableBinding<String, Integer> binding = new EqualsBinding();
			
			for(int i=1; i<split.length; i++) {
				Integer objectID = Integer.parseInt(split[i]);				
				binding.put(variableOrder[i-1], objectID);
			}
			
			syncingTemplate.maybeProcessEvent(symbolName, binding);
		}
	
	}
	
	@SuppressWarnings("serial")
	static class EqualsBinding extends VariableBinding<String, Integer>  {
		protected boolean equalBindings(Integer binding,
				Integer otherBinding) {
			return binding.equals(otherBinding);
		}
	};
	
}