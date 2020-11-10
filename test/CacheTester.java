import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class CacheTester {

	/**
	 * Verifies that the cache is properly evicting the LRU Nodes
	 */
	@Test
	public void leastRecentlyUsedIsCorrect () {

		// Test proper eviction of 2
		StringIntProvider provider = new StringIntProvider(100);
		int capacity = 2;
		Cache<String, Integer> cache = new LRUCache<String, Integer> (provider, capacity);
		cache.get("1"); // Miss, add 1 to cache
		cache.get("2"); // Miss, add 2 to cache
		assertTrue(cache.getNumMisses() == 2);
		cache.get("1"); // Hit
		cache.get("3"); // Miss, add 3 to cache, evict 2
		assertTrue(cache.getNumMisses() == 3);
		cache.get("3"); // Hit
		cache.get("2"); // Miss, add 2 to cache, evict 1
		assertTrue(cache.getNumMisses() == 4);

		// Test proper eviction of 5
		int capacity2 = 5;
		Cache<String, Integer> cache2 = new LRUCache<String, Integer> (provider, capacity2);
		cache2.get("5"); // Miss, add 5 to cache2
		cache2.get("26"); // Miss, add 26 to cache2
		cache2.get("45"); // Miss, add 45 to cache2
		cache2.get("1"); // Miss, add 1 to cache2
		cache2.get("53"); // Miss, add 53 to cache2
		cache2.get("19"); // Miss, add 19 to cache2, evict 5
		cache2.get("45"); // Hit
		assertTrue(cache2.getNumMisses() == 6);
		cache2.get("5"); // Miss, add 5 to cache2, evict 26
		assertTrue(cache2.getNumMisses() == 7);
	}

	/**
	 * Helper method to automatically populate the cache
	 * @param c
	 * @param start
	 * @param capacity
	 */
	private void populateCache (Cache c, int start, int capacity) {
		for (int j = start; j < capacity + start; j++) { // Populate cache
			c.get(String.valueOf(j));
		}
	}

	/**
	 * Verifies that the cache returns a value from the data provider
	 */
	@Test
	public void testSize0 () {
		StringIntProvider provider = new StringIntProvider(100);
		Cache<String, Integer> cache = new LRUCache<String, Integer> (provider, 0);

		assertTrue(cache.get("1") == 1);
		assertTrue(provider.getNumFetches() == 1);
	}

	/**
	 * Verifies that the get method returns the correct value for the given key, when both hits & misses occur
	 */
	@Test
	public void testGetReturn () {

		int providerCapacity = 20;
		StringIntProvider provider = new StringIntProvider(providerCapacity);
		int cacheCapacity = providerCapacity / 2;
		Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, cacheCapacity);

		populateCache(cache, 0, cacheCapacity);

		// Verify getting hit values returns proper values
		for(int i = 0; i < cacheCapacity / 2; i++){
			assertTrue (cache.get(String.valueOf(i)) == i);
		}

		// Verify getting miss values after the cache has hit values returns proper values
		for(int i = 0; i < cacheCapacity / 2; i++){
			assertTrue (cache.get(String.valueOf(i + cacheCapacity)) == i + cacheCapacity);
		}

		// Verify getting hit values after the cache has missed values returns proper values
		for(int i = 0; i < cacheCapacity/ 2; i++){
			assertTrue (cache.get(String.valueOf(i + cacheCapacity)) == i + cacheCapacity);
		}
	}

	/**
	 * Verifies that the get method returns the correct value for the given key, for repeated hits on the cache
	 */
	@Test
	public void testGetHitReturn () {
		int providerCapacity = 20;
		StringIntProvider provider = new StringIntProvider(providerCapacity);
		int cacheCapacity = providerCapacity / 2;
		Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, cacheCapacity);

		populateCache(cache, 0, cacheCapacity);

		// Verify getting hit values returns proper values
		for(int i = 0; i < cacheCapacity; i++){
			assertTrue (cache.get(String.valueOf(i)) == i);
		}
	}

	/**
	 * Verifies that the get method returns the correct value for the given key, for repeated misses on the cache
	 */
	@Test
	public void testGetMissReturn() {
		int providerCapacity = 20;
		StringIntProvider provider = new StringIntProvider(providerCapacity);
		int cacheCapacity = providerCapacity / 2;
		Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, cacheCapacity);

		// Verify getting miss values returns proper values
		for(int i = 0; i < cacheCapacity; i++){
			assertTrue (cache.get(String.valueOf(i)) == i);
		}
	}

	/**
	 * Verifies that when a value is gotten from a cache where it is already stored it doesn't increment the number of misses
	 */
	@Test
	public void testGetHits(){
		int providerCapacity = 20;
		StringIntProvider provider = new StringIntProvider(providerCapacity);
		int cacheCapacity = providerCapacity / 2;
		Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, cacheCapacity);

		populateCache(cache, 0, cacheCapacity);

		assertTrue(cache.getNumMisses() == cacheCapacity);

		// Apply a set of Hits
		for(int i = 0; i < cacheCapacity; i++){
			cache.get(String.valueOf(i)); // Hit
		}

		// Cache should not have missed any values
		assertTrue(cache.getNumMisses() == cacheCapacity);
	}

	/**
	 * Verifies that when a value is gotten from a cache where it is not already stored it always increments the number of misses
	 */
	@Test
	public void testGetMisses() {
		int providerCapacity = 20;
		StringIntProvider provider = new StringIntProvider(providerCapacity);
		int cacheCapacity = providerCapacity / 2;
		Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, cacheCapacity);

		populateCache(cache, 0, cacheCapacity);

		assertTrue(cache.getNumMisses() == cacheCapacity);

		// Apply a set of misses
		for (int i = cacheCapacity; i < cacheCapacity * 2; i++) {
			cache.get(String.valueOf(i));
		}

		// Cache should have missed
		assertTrue(cache.getNumMisses() == cacheCapacity * 2);
	}

	/**
	 * Verifies that the StringIntProvider is only being fetched on at the proper time, tested for both hits & misses
	 */
	@Test
	public void testDataProviderFetches(){
		int providerCapacity = 20;
		StringIntProvider provider = new StringIntProvider(providerCapacity);
		int cacheCapacity = providerCapacity / 2;
		Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, cacheCapacity);

		populateCache(cache, 0, cacheCapacity);

		assertTrue(provider.getNumFetches() == cacheCapacity);

		// Apply a set of Hits
		for(int i = 0; i < cacheCapacity; i++){
			cache.get(String.valueOf(i));
		}

		assertTrue(provider.getNumFetches() == cacheCapacity);

		// Apply a set of Misses
		for(int i = 0; i < cacheCapacity; i++){
			cache.get(String.valueOf(i + cacheCapacity ));
		}

		assertTrue(provider.getNumFetches() == cacheCapacity * 2);
	}

	/**
	 * Verifies that the StringIntProvider is only being fetched on at the proper time, tested for hits
	 */
	@Test
	public void testDataProviderFetchesOnHits(){
		int providerCapacity = 20;
		StringIntProvider provider = new StringIntProvider(providerCapacity);
		int cacheCapacity = providerCapacity / 2;
		Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, cacheCapacity);

		populateCache(cache, 0, cacheCapacity);

		assertTrue(provider.getNumFetches() == cacheCapacity);

		// Apply a set of Hits
		for(int i = 0; i < cacheCapacity; i++){
			cache.get(String.valueOf(i));
		}

		assertTrue(provider.getNumFetches() == cacheCapacity);
	}

	/**
	 * Verifies that the StringIntProvider is only being fetched on at the proper time, tested for misses
	 */
	@Test
	public void testDataProviderFetchesOnMisses(){
		int providerCapacity = 20;
		StringIntProvider provider = new StringIntProvider(providerCapacity);
		int cacheCapacity = providerCapacity / 2;
		Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, cacheCapacity);

		populateCache(cache, 0, cacheCapacity);

		assertTrue(provider.getNumFetches() == cacheCapacity);

		// Apply a set of Misses
		for(int i = 0; i < cacheCapacity; i++){
			cache.get(String.valueOf(i + cacheCapacity ));
		}

		assertTrue(provider.getNumFetches() == cacheCapacity * 2);
	}

	/**
	 * Verifies that the hit portion of the get function is constant time
	 * Extra Credit
	 */
	@Test
	public void testConstantTimeHits () {
		StringIntProvider provider = new StringIntProvider(500000);
		int bound = 50;
		int multiplier = 10000;
		int amountGets = 1000000;
		long [] timeCosts = new long[bound+1];

		// Loops through 10,000 to 500,000 capacity of cache
		for (int i = 1; i < bound+1; i++) {
			int capacity = i * multiplier;
			Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, capacity);

			populateCache(cache, 0, capacity);

			Random rand = new Random();

			// Time the retrieval time of get for 100,000 random hit values
			long start = System.currentTimeMillis();
			for (int m = 0; m < amountGets; m++) {
				cache.get(String.valueOf(rand.nextInt(capacity)));
			}
			long end = System.currentTimeMillis();

			// Store the time cost
			timeCosts[i] = end - start;
		}

		// Calculates the ratio
		double counter = 0;
		for (int k = 1; k < bound+1; k++) {
			for (int l = k+1; l < bound+1; l++) {
				if (timeCosts[l] > timeCosts[k]) {
					counter++;
				}
			}
		}

		// Calculate ratio of time l < k
		double ratio = counter/((bound)*((bound-1)/2));

		// test if constant time (~0.5), adjusted
		assertTrue(((0.1 < ratio) && (ratio < 0.9)));
	}

	/**
	 * Verifies that the hit portion of the get function is constant time
	 * Extra Credit
	 */
	@Test
	public void testConstantTimeMisses () {
		StringIntProvider provider = new StringIntProvider(500000);
		int bound = 50;
		int multiplier = 10000;
		int amountGets = 1000000;
		long [] timeCosts = new long[bound+1];

		// Loops through 10,000 to 500,000 capacity of cache
		for (int i = 1; i < bound+1; i++) {
			int capacity = i * multiplier;
			Cache<String, Integer> cache = new LRUCache<String, Integer>(provider, capacity);

			populateCache(cache, 0, capacity);

			Random rand = new Random();

			// Time the retrieval time of get for 100,000 random miss values
			long start = System.currentTimeMillis();
			for (int m = 0; m < amountGets; m++) {
				cache.get(String.valueOf(rand.nextInt(capacity) + capacity));
			}
			long end = System.currentTimeMillis();

			// Store the time cost
			timeCosts[i] = end - start;
		}

		// Calculates the ratio
		double counter = 0;
		for (int k = 1; k < bound+1; k++) {
			for (int l = k+1; l < bound+1; l++) {
				if (timeCosts[l] > timeCosts[k]) {
					counter++;
				}
			}
		}

		// Calculate ratio of time l < k
		double ratio = counter/((bound)*((bound-1)/2));

		// Test if constant time (~0.5), adjusted
		assertTrue(((0.1 < ratio) && (ratio < 0.9)));
	}

	/**
	 * Implementation of DataProvider which uses a String version of an int to retrieve the actual in itself
	 * Example key Value Pairs: "12", 12 or "357", 357
	 */
	public static class StringIntProvider implements DataProvider <String, Integer>{

		// StringIntProvider uses a simple hashmap to store the keyvalue pairs
		private Map<String, Integer> data;

		// Number of gets that the StringIntProvider sends to the DataProvider
		private int numFetches;

		StringIntProvider(int capacity){
			data = new HashMap<>();
			numFetches = 0;
			populate(capacity);
		}

		// Gets a value from the provider
		public Integer get (String key) {
			numFetches++;
			return data.get(key);
		}

		// Populate With values up until n
		private void populate (int n) {
			for (int i = 0; i < n; i++) {
				data.put(String.valueOf(i), i);
			}
		}

		// Returns the number of fetches
		public int getNumFetches(){
			return numFetches;
		}
	}
}
