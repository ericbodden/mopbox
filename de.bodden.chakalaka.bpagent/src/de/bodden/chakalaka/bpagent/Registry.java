package de.bodden.chakalaka.bpagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bodden.chakalaka.bpagentshared.IFSM;
import de.bodden.rvlib.finitestate.AbstractFSMMonitorTemplate;

public class Registry implements IMonitorTemplateRegistry {

	Map<String,Map<Integer,Set<SymbolAndTemplateNumber>>> classNameToLineNumberToInfos = new HashMap<String, Map<Integer,Set<SymbolAndTemplateNumber>>>();
	
	List<AbstractFSMMonitorTemplate<String, String, Object>> templates = new ArrayList<AbstractFSMMonitorTemplate<String,String,Object>>();
	
	public void registerMonitor(IFSM fsm, AbstractFSMMonitorTemplate<String, String, Object> template) {
		templates.add(template);
		for(String s: fsm.symbols()) {
			String className = fsm.classNameForSymbol(s);
			assert className!=null;
			Map<Integer, Set<SymbolAndTemplateNumber>> lineToInfos = classNameToLineNumberToInfos.get(className);
			if(lineToInfos==null) {
				lineToInfos = new HashMap<Integer, Set<SymbolAndTemplateNumber>>();
				classNameToLineNumberToInfos.put(className, lineToInfos);
			}
			int line = fsm.lineNumberForSymbol(s);
			Set<SymbolAndTemplateNumber> infos = lineToInfos.get(line);
			if(infos==null) {
				infos = new HashSet<IMonitorTemplateRegistry.SymbolAndTemplateNumber>();
				lineToInfos.put(line, infos);
			}
			infos.add(new SymbolAndTemplateNumber(s, templates.indexOf(template)));
		}
		System.out.println("Registered new template with number "+(templates.size()-1));
	}

	
	@Override
	public boolean needsMonitoring(String className) {		
		return classNameToLineNumberToInfos.containsKey(className);
	}

	@Override
	public boolean needsMonitoring(String className, int lineNumber) {
		Map<Integer, Set<SymbolAndTemplateNumber>> lineToInfos = classNameToLineNumberToInfos.get(className);
		if(lineToInfos==null)
			return false;
		else
			return lineToInfos.containsKey(lineNumber);
	}

	@Override
	public Set<SymbolAndTemplateNumber> transitionInfos(String className, int lineNumber) {
		Map<Integer, Set<SymbolAndTemplateNumber>> lineToInfos = classNameToLineNumberToInfos.get(className);
		return lineToInfos.get(lineNumber);
	}
	

	
	private static Registry instance = new Registry();

	public static Registry v() {
		return instance;
	}

	private Registry() { }
}
