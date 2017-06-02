package com.common;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MapTest {

	@Test
	public void testMap(){
		Map<String, String> map1 = new HashMap<>();
		Map<String, String> map2 = new HashMap<>();
		
		map1.put("1", "a");
		map1.put("2", "b");
		map1.put("3", "c");
		map1.put("4", "d");
		
		map2.put("4", "e");
		map2.put("6", "f");
		map2.put("7", "g");
		map2.put("8", "h");
		
		map2.putAll(map1);
		
		System.out.println(map2);
	}
}
