

import de.bodden.mopbox.finitestate.AbstractFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

/*
 * This is a variant of the Hello World example that uses variable bindings. Such
 * bindings allow events to be parameterized by objects. For instance, imagine that
 * the events "hello" and "world" can be sent both by Alice and Bob and we want to match
 * if any of them sent the events in the right order, without regarding what the other one
 * may have sent at the same time.
 */
public class HelloAliceHelloBob {

	public static void main(String[] args) {
		HelloPersonMonitor template = new HelloPersonMonitor();
	
		//first we create two variable bindings, one mapping "person" to "Alice"...
		IVariableBinding<String, String> alice = new VariableBinding<String, String>();
		alice.put("person", "Alice");
		//... and one mapping "person" to "Bob"
		IVariableBinding<String, String> bob = new VariableBinding<String, String>();
		bob.put("person", "Bob");
		
		/* The following will match because Alice said both "hello" and "world" in the right order. 
		 */
		System.out.println("Trace 1...");
		template.processEvent("hello", alice);
		template.processEvent("world", alice);
		template.reset();	

		/* The following will match also, even twice, once for Alice and once for Bob. This is because
		 * both said "hello" and "world" in the right order. Notice that it is not a problem that
		 * the "hello" event for Bob comes between the two events for Alice; MOPBox automatically projects
		 * events onto Alice and Bob separately, keeping them apart.
		 */
		System.out.println("Trace 2...");
		template.processEvent("hello", alice);
		template.processEvent("hello", bob);
		template.processEvent("world", alice);
		template.processEvent("world", bob);
		template.reset();	

		/*
		 * Here we match for Alice but not for Bob because Bob said "world hello" instead of "hello world".
		 */
		System.out.println("Trace 3...");
		template.processEvent("hello", alice);
		template.processEvent("world", bob);
		template.processEvent("hello", bob);
		template.processEvent("world", alice);
		template.reset();	
	}	

	static class HelloPersonMonitor extends AbstractFSMMonitorTemplate<String, String, String> {
		
		public HelloPersonMonitor() {
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
			alphabet.makeNewSymbol("hello","person"); //Here, "person" is a free variable that events of type "hello" will bind.
			alphabet.makeNewSymbol("world","person"); //Here, "person" is a free variable that events of type "world" will bind.
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
		 * was reached. 
		 */
		public void matchCompleted(IVariableBinding<String, String> binding) {
			//Note that we can access the value of "person" from the binding!
			System.out.println("<MOPBox>Hello World!, said "+binding.get("person")+"</MOPBox>");
		}
		
	}
}
