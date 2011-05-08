package de.bodden.mopbox.util;

import java.util.Map;

import org.apache.commons.collections15.map.AbstractReferenceMap;
import org.apache.commons.collections15.map.ReferenceIdentityMap;

import de.bodden.mopbox.generic.indexing.fast.WeakIdentityMultiKey;

@SuppressWarnings({"rawtypes","unchecked"})
public class PerfTest {
	
	private static final int MAX = 100000;
	static Map map = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.HARD);
	
	public static void main(String[] args) {
		{
			map.clear();
			long before = System.currentTimeMillis();
			for(int i=0;i<MAX;i++) {
				Object o1 = new Object();
				Object o2 = new Object();
				Object o3 = new Object();
				associate(o1,o2,o3);
			}
			System.err.println("Nested: "+(System.currentTimeMillis()-before));
		}
		
		{
			map.clear();
			long before = System.currentTimeMillis();
			for(int i=0;i<MAX;i++) {
				Object o1 = new Object();
				Object o2 = new Object();
				Object o3 = new Object();
				associateMultiKey(o1,o2,o3);
			}
			System.err.println("Multi-Key: "+(System.currentTimeMillis()-before));
		}
	}

	private static void associate(Object o, Object o2, Object o3) {
		Map map2 = (Map) map.get(o);
		if(map2==null) {
			map2 = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.HARD);
			map.put(o, map2);
		}
		map2.put(o2, o3);
	}

	private static void associateMultiKey(Object o, Object o2, Object o3) {
		WeakIdentityMultiKey<Object> key = new WeakIdentityMultiKey<Object>(o,o2);
		map.put(key, o3);
	}
}
