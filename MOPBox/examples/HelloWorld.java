

import de.bodden.mopbox.finitestate.AbstractFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.IVariableBinding;

/*
 * This is a simple Hello World example for a runtime monitor.
 * We are interested in events "hello" and "world" and want
 * to be notified whenever those events happen in the order
 * "hello world".
 */
public class HelloWorld {

	public static void main(String[] args) {
		HelloWorldMonitor template = new HelloWorldMonitor();

		/* The following won't match because we are looking
		 * for "'hello' 'world'" and not "'world' 'hello'"  
		 */
		System.out.println("Trace 1...");
		template.processEvent("world");
		template.processEvent("hello");
		template.reset();
		
		/* The following also won't match, although the trace ends
		 * with "'hello' 'world'". This is because the automaton describes
		 * the regular language "'hello' 'world'", which does not allow
		 * for the prefix 'world'; in other words "'world' 'hello' 'world'"
		 * is not a word in this language. If we wanted to match also in
		 * such situations, then we would have to add appropriate loops
		 * to the initial state.
		 */
		System.out.println("Trace 2...");
		template.processEvent("world");
		template.processEvent("hello");
		template.processEvent("world");
		template.reset();
		
		/* The following will match. It is exactly what we are looking for. 
		 */
		System.out.println("Trace 3...");
		template.processEvent("hello");
		template.processEvent("world");
		template.reset();	
	}	

	static class HelloWorldMonitor extends AbstractFSMMonitorTemplate<String, String, Object> {
		
		public HelloWorldMonitor() {
			//this is necessary to initialize this monitor template;
			//we don't do this automatically because often the template may want to compute
			//things before initialization
			initialize();
		}
	
		/*
		 * We define that this monitor is interested in two kinds of events, labeled
		 * by "hello" and "world". We have to use Strings here because we used "String"
		 * as the first type parameter to AbstractFSMMonitorTemplate. Had we chosen another
		 * parameter there, then we could have used other kinds of labels here.
		 */
		protected void fillAlphabet(IAlphabet<String, String> alphabet) {
			alphabet.makeNewSymbol("hello");
			alphabet.makeNewSymbol("world");		
		}
	
		/*
		 * Here we create the monitor's states and transitions. We want to be called back
		 * when having monitored a "hello" event followed by a "world" event. Hence we need
		 * three states connected by a "hello" and "world" transition. 
		 */
		protected State<String> setupStatesAndTransitions() {
			State<String> initial = makeState(false);
			State<String> middle = makeState(false);
			State<String> error = makeState(true /*is an error state*/);
			
			initial.addTransition(getSymbolByLabel("hello"), middle);
			middle.addTransition(getSymbolByLabel("world"), error);
			
			return initial; //by convention, we return the initial state
		}
	
		/*
		 * This is a callback method. MOPBox will call this method when an error state
		 * was reached. We ignore the parameter "binding" for now.
		 */
		public void matchCompleted(IVariableBinding<String, Object> binding) {
			System.out.println("<MOPBox>Hello World!</MOPBox>");
		}
		
	}
}
