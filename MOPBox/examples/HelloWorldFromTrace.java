

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import de.bodden.mopbox.finitestate.AbstractFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.IVariableBinding;

/*
 * This is a variant of the Hello World example in which events are read from a file.
 */
public class HelloWorldFromTrace {

	public static void main(String[] args) throws IOException {
		HelloWorldMonitor template = new HelloWorldMonitor();

		/* The following will match because trace1.txt contains lines:
		 * hello
		 * world
		 */
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("examples/trace1.txt")));		
			String line;
			System.out.println("file: "+"examples/trace1.txt");
			while((line=reader.readLine())!=null) {
				System.out.println("read: "+line);
				template.processEvent(line);
			}
			template.reset();
		}

		/* The following will not match because trace2.txt contains lines:
		 * world
		 * hello
		 */
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("examples/trace2.txt")));		
			String line;
			System.out.println("file: "+"examples/trace2.txt");
			while((line=reader.readLine())!=null) {
				System.out.println("read: "+line);
				template.processEvent(line);
			}
			template.reset();
		}
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
