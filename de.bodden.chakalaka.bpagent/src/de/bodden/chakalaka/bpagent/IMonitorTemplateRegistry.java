package de.bodden.chakalaka.bpagent;

import java.util.Set;

/**
 * Provides information about the classes and lines that need monitoring
 * and which transitions to insert at these lines.
 */
public interface IMonitorTemplateRegistry {
	
	boolean needsMonitoring(String className);
	
	boolean needsMonitoring(String className, int lineNumber);
	
	Set<SymbolAndTemplateNumber> transitionInfos(String className, int lineNumber);

	public static class SymbolAndTemplateNumber {
		final String sym;
		final int templateNumber;
		public SymbolAndTemplateNumber(String sym, int templateNumber) {
			this.sym = sym;
			this.templateNumber = templateNumber;
		}
	}
	
}
