package de.bodden.rvlib.generic;

/**
 * An indexing strategy has the single purpose of dispatching a parametric event,
 * i.e., an event parameterized by a concrete {@link IVariableBinding}, to all monitors
 * corresponding to compatible variable bindings.
 * 
 * For example, consider an event with a binding {a=a1,b=b1} where a1 and b1 are concrete objects.
 * Also assume that there are monitor instances for the bindings {}, {a=a1}, and {a=a1,b=b2}.
 * The indexing algorithm will dispatch the event to both {}, {a=a1} but not to {a=a1,b=b2} because the
 * latter binding conflicts on variable b: the event binds b to b1 while the monitor binds it to b2.
 * When the event is dispatched to {} and {a=a1}, this will progress these monitors' states and the new states will
 * be stored for the binding {a=a1,b=b1}. 
 *
 * Indexing is tricky to get both correct and efficient. Therefore there can be multiple different indexing
 * strategies. However, they all should be equivalent with respect to their externally visible behavior.
 * 
 * @param <L> The type of labels used at transitions.
 * @param <K> The type of keys used for the variable bindings.
 * @param <V> The type of values used for the variable bindings.
 */
public interface IIndexingStrategy<L,K,V> {
	
	void processEvent(IEvent<L,K,V> e);

}
