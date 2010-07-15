package de.bodden.chakalaka.bpagent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassRegistry {
	
	private Map<String,Set<Class<?>>> nameToClasses = new HashMap<String, Set<Class<?>>>();
	
	public void registerClass(Class<?> c) {
		Set<Class<?>> classes = nameToClasses.get(c.getName());
		if(classes==null) {
			classes = new HashSet<Class<?>>();
			nameToClasses.put(c.getName(), classes);			
		}
		classes.add(c);
	}
	
	public Set<Class<?>> classesWithName(String className) {
		if(!nameToClasses.containsKey(className)) {
			throw new IllegalArgumentException("No such class "+className);
		}
		return nameToClasses.get(className);
	}
	
	private static ClassRegistry instance = new ClassRegistry();
	
	private ClassRegistry() {}
	
	public static ClassRegistry v() {
		return instance;
	}
}
