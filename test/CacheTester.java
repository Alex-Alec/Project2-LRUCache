import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class CacheTester {
	@Test
	public void leastRecentlyUsedIsCorrect () {
		DataProvider<Integer,String> provider = null; // Need to instantiate an actual DataProvider
		Cache<Integer,String> cache = new LRUCache<Integer,String>(provider, 5);
	}

	@Test
	public void checkConstantTime () {
		StringIntProvider provider = new StringIntProvider();
		provider.populate(200000);
		long [] timeCosts = new long[100];
		for (int i = 1; i < 100; i++) {
			int capacity = i*1000;
			final Cache<String, Integer> cache = new LRUCache<String, Integer> (provider, capacity);
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
		for (int k = 0; k < 100; k++) {
			for (int l = k++; l < 100; l++) {
				if (timeCosts[l] > timeCosts[k]) {
					counter++;
				}
			}
		}
		double time = counter/4950;
		assertTrue(((0.4 > time) && (time < 0.6)));
	}

	//Test List
	//get num misses (hitting & missing, only hitting, only missing)
	//check eviction (also check recency list is correct)
	//check cache size 0
	//check cache size is correct (check bounds)
	//base test of cache.get is correct

}
