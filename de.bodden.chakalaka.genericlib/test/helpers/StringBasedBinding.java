package helpers;

import de.bodden.rvlib.impl.VariableBinding;

@SuppressWarnings("serial")
public class StringBasedBinding extends VariableBinding<String,Object> {
	
	public StringBasedBinding(String s) {
		String[] split = s.split(",");
		for (String keyValue : split) {
			String[] spl = keyValue.split("=");
			//FIXME why do the tests break if we remove the intern() from spl[1] ?
			put(spl[0].intern(), spl[1].intern());
		}		
	}
	
	@Override
	protected boolean equalBindings(Object binding, Object otherBinding) {
		return binding.equals(otherBinding);
	}

}
