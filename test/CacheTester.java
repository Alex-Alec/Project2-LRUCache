import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class CacheTester {

	/**
	 * Verifies that the eviction protocol works properly
	 */
	@Test
	public void leastRecentlyUsedIsCorrect () {
		StringIntProvider provider = new StringIntProvider();
		provider.populate (100);
		int capacity = 2;
		Cache<String, Integer> cache = new LRUCache<String, Integer> (provider, capacity);
		cache.get("1"); //miss, add 1 to cache
		cache.get("2"); //miss, add 2 to cache
		assertTrue(cache.getNumMisses() == 2);
		cache.get("1"); //hit
		cache.get("3"); //miss, add 3 to cache, evict 2
		assertTrue(cache.getNumMisses() == 3);
		cache.get("3"); //hit
		cache.get("2"); //miss, add 2 to cache, evict 1
		assertTrue(cache.getNumMisses() == 4);
		int capacity2 = 5;
		Cache<String, Integer> cache2 = new LRUCache<String, Integer> (provider, capacity2);
		cache2.get("5"); //miss, add 5 to cache2
		cache2.get("26"); //miss, add 26 to cache2
		cache2.get("45"); //miss, add 45 to cache2
		cache2.get("1"); //miss, add 1 to cache2
		cache2.get("53"); //miss, add 53 to cache2
		cache2.get("19"); //miss, add 19 to cache2, evict 5
		cache2.get("45"); //hit
		assertTrue(cache2.getNumMisses() == 6);
		cache2.get("5"); //miss, add 5 to cache2, evict 26
		assertTrue(cache2.getNumMisses() == 7);
	}

	/**
	 * Verifies that the get function is constant time on average
	 */

	@Test
	public void checkConstantTime () {
		checkConstantTimeHits();
		checkConstantTimeMisses();
	}

	/**
	  Verifies that the get function is constant time if it always hits
	 */
	@Test
	public void checkConstantTimeHits () {
		StringIntProvider provider = new StringIntProvider();
		provider.populate(20000000);
		int bound = 25;
		int cap = 20;
		int amountGets = 100000;
		long [] timeCosts = new long[bound];
		for (int i = 1; i < bound; i++) { //get timing values for different cache capacities
			int capacity = i * cap;
			Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, capacity);
			populateCache(cache, 0, capacity); //populate all elements of the cache
			long start = System.currentTimeMillis(); //start timer
			for (int m = 0; m < amountGets; m++) {
				int temp = cache.get(String.valueOf(m)); //get elements from cache
			}
			long end = System.currentTimeMillis(); //end timer
			timeCosts[i] = end - start; //store time it took to get elements
		}
		double counter = 0;
		for (int k = 1; k < bound; k++) {
			for (int l = k+1; l < bound; l++) {
				if (timeCosts[l] > timeCosts[k]) { //compare values of each timing pair
					counter++;
				}
			}
		}
		double time = counter/((bound-1)*((bound-2)/2)); //calculate ratio of time l < k
		assertTrue(((0.3 < time) && (time < 0.7))); // check if constant time (~0.5)
	}

	/**
	 Verifies that the get function is constant time if it always misses
	 */
	@Test
	public void checkConstantTimeMisses () {
		StringIntProvider provider = new StringIntProvider();
		provider.populate(20000000);
		int bound = 25;
		int cap = 20;
		int amountGets = 100000;
		long [] timeCosts = new long[bound];
		for (int i = 1; i < bound; i++) { //get timing values for different cache capacities
			int capacity = i * amountGets;
			Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, capacity);
			populateCache(cache, amountGets, capacity); //populate elements of the cache beyond amountGets to avoid hits
			long start = System.currentTimeMillis(); //start timer
			for (int m = 0; m < amountGets; m++) {
				int temp = cache.get(String.valueOf(m)); //get elements from cache
			}
			long end = System.currentTimeMillis(); //end timer
			timeCosts[i] = end - start; //store time it took to get elements
		}
		double counter = 0;
		for (int k = 1; k < bound; k++) {
			for (int l = k+1; l < bound; l++) {
				if (timeCosts[l] > timeCosts[k]) { //compare values of each timing pair
					counter++;
				}
			}
		}
		double time = counter/((bound-1)*((bound-2)/2)); //calculate ratio of time l < k
		assertTrue(((0.3 < time) && (time < 0.7))); //check if constant time (~0.5)
	}

	/**
	 * Helper method to automatically populate the cache
	 * @param c
	 * @param start
	 * @param capacity
	 */
	public void populateCache (Cache c, int start, int capacity) {
		for (int j = start; j < capacity + start; j++) { // Populate cache
			c.get(String.valueOf(j));
		}
	}

	/**
	 * Verifies that the cache returns null if a cache is instantiated with
	 * a capacity of 0
	 */
	@Test
	public void checkSize0 () {
		StringIntProvider provider = new StringIntProvider();
		provider.populate (100);
		Cache<String, Integer> cache = new LRUCache<String, Integer> (provider, 0);
		assertNull(cache.get("12"));
	}

	/**
	 * Verifies that the get method returns the correct value for the given key
	 */
	@Test
	public void checkCacheGet () {
		StringIntProvider provider = new StringIntProvider();
		provider.populate (100);
		Cache<String, Integer> cache = new LRUCache<String, Integer> (provider, 20);
		assertTrue (cache.get("20") == 20);
	}

	/**
	 * Verifies that when a value is gotten from a cache where it is already
	 * stored it doesn't increment the number of misses
	 */
	@Test
	public void checkOnlyHits(){
		StringIntProvider provider = new StringIntProvider();
		provider.populate(20);
		int capacity = 10;
		Cache<String, Integer> cache = new LRUCache<String, Integer> (provider, capacity);
		int[] actual = new int[10];
		for(int i = 0; i < capacity; i++){ //caches first ten pairs
			actual[i] = i;
			cache.get(String.valueOf(i));
		}

		assertTrue(cache.getNumMisses() == 10);

		int[] retrieved = new int[capacity];
		for(int i = 0; i < capacity; i++){ //retrieves first ten pairs
			retrieved[i] = cache.get(String.valueOf(i)); // hits
		}

		assertTrue(cache.getNumMisses() == 10);
		assertArrayEquals(actual, retrieved);
	}

	/**
	 * Verifies that when a value is gotten from a cache where it is not
	 * already stored it always increments the number of misses
	 */
	@Test
	public void checkOnlyMisses() {
		StringIntProvider provider = new StringIntProvider();
		provider.populate(20);
		int capacity = 10;
		Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, capacity);
		int[] actual = new int[10];
		for (int i = 0; i < capacity; i++) { //caches first ten pairs
			actual[i] = i;
			cache.get(String.valueOf(i));
		}
		assertTrue(cache.getNumMisses() == 10);
		int[] retrieved = new int[capacity];
		for (int i = capacity; i < 20; i++) {
			retrieved[i - 10] = cache.get(String.valueOf(i)); //misses
		}
		assertTrue(cache.getNumMisses() == 20);
		for (int i = 0; i < actual.length; i++) {
			assertNotEquals(actual[i], retrieved[i]);
		}
	}
}
