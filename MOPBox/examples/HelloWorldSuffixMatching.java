

import de.bodden.mopbox.finitestate.AbstractFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.IVariableBinding;

/*
 * This is a variant of the Hello World example with suffix matching.
 * Suffix matching means that any prefix of the trace may be ignored during
 * matching. This is often useful and can be achieved by adding loops to the
 * automaton's initial state for all event kinds.
 */
public class HelloWorldSuffixMatching {

	public static void main(String[] args) {
		HelloWorldSuffixMonitor template = new HelloWorldSuffixMonitor();

		/* The following won't match because we are looking
		 * for "'hello' 'world'" and not "'world' 'hello'"  
		 */
		System.out.println("Trace 1...");
		template.processEvent("world");
		template.processEvent("hello");
		template.reset();
		
		/* The following WILL match because the suffix of the
		 * trace is "hello world". 
		 */
		System.out.println("Trace 2...");
		template.processEvent("world");
		template.processEvent("hello");
		template.processEvent("world");
		template.reset();
		
		/* The following will also match. It is exactly what we are looking for. 
		 */
		System.out.println("Trace 3...");
		template.processEvent("hello");
		template.processEvent("world");
		template.reset();	

		/* This will even match twice. 
		 */
		System.out.println("Trace 4...");
		template.processEvent("hello");
		template.processEvent("world"); //once here
		template.processEvent("hello");
		template.processEvent("world"); //once here
		template.reset();	
	}	

	static class HelloWorldSuffixMonitor extends AbstractFSMMonitorTemplate<String, String, Object> {
		
		public HelloWorldSuffixMonitor() {
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
			
			//the following transitions are for suffix matching;
			//they "eat up" all "world" prefixes of the trace, thus ignoring them
			initial.addTransition(getSymbolByLabel("world"), initial);
			
			//upon the first "hello" we move to "middle"
			initial.addTransition(getSymbolByLabel("hello"), middle);
			
			//for any further "hello" we stay in "middle" (thereby ignoring all other
			//"hello" events already read
			middle.addTransition(getSymbolByLabel("hello"), middle);
			
			//hello->world, now we reach an error state
			middle.addTransition(getSymbolByLabel("world"), error);
			
			//this is needed to be notified again when there are additional matches
			error.addTransition(getSymbolByLabel("hello"), middle);
			error.addTransition(getSymbolByLabel("world"), initial);
			
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
