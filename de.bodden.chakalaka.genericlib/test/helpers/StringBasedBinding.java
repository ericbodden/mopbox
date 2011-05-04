package helpers;

import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

/**
 * This class represents a simple variable binding that maps
 * from {@link String}s to {@link Object}s.
 */
@SuppressWarnings("serial")
public class StringBasedBinding extends VariableBinding<String,Object> {
	
	/**
	 * Creates a new binding using an input string of the form a=a1,b=b1.
	 * The strings a1 and b1 are automatically interned using {@link String#intern()},
	 * to allow comparison of the bindings via identity, as required by the
	 * {@link IVariableBinding} interface.
	 */
	public StringBasedBinding(String s) {
		String[] split = s.split(",");
		for (String keyValue : split) {
			String[] spl = keyValue.split("=");
			//FIXME why do the tests break if we remove the intern() from spl[1] ?
			put(spl[0].intern(), spl[1].intern());
		}		
	}
	
	/**
	 * Two bindings are equal when they are identical. 
	 */
	@Override
	protected boolean equalBindings(Object binding, Object otherBinding) {
		return binding==otherBinding;
	}

}
