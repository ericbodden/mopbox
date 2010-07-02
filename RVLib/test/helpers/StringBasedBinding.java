package helpers;

import de.bodden.rvlib.impl.VariableBinding;

@SuppressWarnings("serial")
public class StringBasedBinding extends VariableBinding<String,Object> {
	
	public StringBasedBinding(String s) {
		String[] split = s.split(",");
		for (String keyValue : split) {
			String[] spl = keyValue.split("=");
			put(spl[0].intern(), spl[1].intern());
		}		
	}

}
