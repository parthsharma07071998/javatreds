package com.xlx.treds.hostapi.bo;

import java.util.HashMap;

public class Test {

	public static void main() {
		// Create a HashMap
		HashMap<Integer, String> hmap = new HashMap<Integer, String>();

		// add elements to HashMap
		hmap.put(1, "AA");
		hmap.put(2, "BB");
		hmap.put(3, "CC");
		hmap.put(4, "DD");

		// Create another HashMap
		HashMap<Integer, String> hmap2 = new HashMap<Integer, String>();

		// Adding elements to the recently created HashMap
		hmap2.put(11, "Hello");
		hmap2.put(22, "Hi");

		// Copying one HashMap "hmap" to another HashMap "hmap2"
		hmap2.putAll(hmap);

		// Displaying HashMap "hmap2" content
		System.out.println("HashMap 2 contains: " + hmap2);
	}

}
