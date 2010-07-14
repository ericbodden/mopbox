package de.bodden.chakalaka.bpagent;

/**
 * Provides information about the classes and lines that need monitoring
 * and which transitions to insert at these lines.
 */
public interface IMonitorTemplateRegistry {
	
	boolean needsMonitoring(String className);
	
	boolean needsMonitoring(String className, int lineNumber);
	
	SymbolAndTemplateNumber transitionInfo(String className, int lineNumber);

	static class SymbolAndTemplateNumber {
		public final String symbol;
		public final int templateNumber;
		public SymbolAndTemplateNumber(String symbol, int templateNumber) {
			this.symbol = symbol;
			this.templateNumber = templateNumber;
		}
	}
	
}
