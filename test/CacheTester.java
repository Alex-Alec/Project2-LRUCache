import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class CacheTester {

	/**
	 * Verifies that the least recently used value in the cache is
	 * correct and that the number of hits and misses is correct
	 */
	@Test
	public void leastRecentlyUsedIsCorrect () {
		StringIntProvider provider = new StringIntProvider();
		provider.populate (100);
		int capacity = 2;
		LRUCache<String, Integer> cache = new LRUCache<String, Integer> (provider, capacity);
		cache.get("1"); //miss, add 1 to cache
		cache.get("2"); //miss, add 2 to cache
		assertTrue(cache.getNumMisses() == 2);
		cache.get("1"); //hit
		cache.get("3"); //miss, add 3 to cache, evict 2
		assertTrue(cache.getNumMisses() == 3);
		cache.get("3"); //hit
		cache.get("2"); //miss, add 2 to cache, evict 1
		assertTrue(cache.getNumMisses() == 4);
	}

	/**
	 * Verifies that the get function is constant time on average
	 */
	@Test
	public void checkConstantTime () {
		StringIntProvider provider = new StringIntProvider();
		provider.populate(200000);
		int bound = 100;
		long [] timeCosts = new long[bound];
		for (int i = 1; i < bound; i++) {
			int capacity = i*1000;
			Cache<String, Integer> cache = new LRUCache<String, Integer> (provider, capacity);
			for (int j = 0; j < capacity; j++) { // Populate cache
				cache.get(String.valueOf(j));
			}
			Random rand = new Random();
			int val = rand.nextInt(2*capacity);
			long start = System.currentTimeMillis();
			cache.get(String.valueOf(val));
			long end = System.currentTimeMillis();
			timeCosts[i] = end-start;
		}
		int counter = 0;
		for (int k = 0; k < bound; k++) {
			for (int l = k++; l < bound; l++) {
				if (timeCosts[l] > timeCosts[k]) {
					counter++;
				}
			}
		}
		double time = counter/4950;
		assertTrue(((0.4 < time) && (time < 0.6)));
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

	//How?
	/**
	 * Verifies that the cache is instantiated at the correct capacity
	 */
	@Test
	public void checkSize () {
		StringIntProvider provider = new StringIntProvider();
		provider.populate (100);
		Cache<String, Integer> cache = new LRUCache<String, Integer> (provider, 20);
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
	public void checkOnlyMisses(){
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
		for(int i = capacity; i < 20;i++){
			retrieved[i-10] = cache.get(String.valueOf(i)); //misses
		}
		assertTrue(cache.getNumMisses() == 20);
		for(int i = 0; i < actual.length; i++){
			assertNotEquals(actual[i], retrieved[i]);
		}
	}

	/**
	 * ???
	 */
	@Test
	public void checkProperGet(){
		StringIntProvider provider = new StringIntProvider();
		provider.populate(20);
		int capacity = 10;
		Cache<String, Integer> cache = new LRUCache<String, Integer> (provider, capacity);

	}

	/**
	 * Verifies that the get method evicts the correct policy correctly
	 */
	@Test
	public void checkEviction() {

	}
}
