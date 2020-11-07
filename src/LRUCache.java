import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of <tt>Cache</tt> that uses a least-recently-used (LRU)
 * eviction policy.
 */
public class LRUCache<T, U> implements Cache<T, U> {
	private int capacity;
	private DataProvider<T, U> provider;
	private Map<T, Node<U>> cache = new HashMap<>();
	private int numMisses;

	private Node<U> head, tail;
	private int numElements;

	/**
	 * @param provider the data provider to consult for a cache miss
	 * @param capacity the exact number of (key,value) pairs to store in the cache
	 */
	public LRUCache (DataProvider<T, U> provider, int capacity) {
		head = tail = null;
		numElements = 0;
		numMisses = 0;
		this.capacity = capacity;
		this.provider = provider;
	}

	/**
	 * Returns the value associated with the specified key.
	 * @param key the key
	 * @return the value associated with the key
	 */
	public U get (T key) {
		if(!cache.containsKey(key)){ // miss
			if(numElements == capacity) {
				remove(head);
				cache.remove(key);
			}
			U newEntry = provider.get(key);
			Node<U> newNode = new Node(null, null, newEntry);
			cache.put(key, newNode);
			add(newNode);
			numMisses++;
			return newEntry;
		}else{ // hit
			Node<U> current = cache.get(key);
			remove(current);
			add(current);
			return current.data;
		}

	}

	/**
	 * Returns the number of cache misses since the object's instantiation.
	 * @return the number of cache misses since the object's instantiation.
	 */
	public int getNumMisses () {
		return numMisses;
	}

	private boolean add(Node<U> node) {
		if (head == null) {
			head = node;
			tail = node;
		} else {
			tail.next = node;
			node.prev = tail;
			node.next = null;
			tail = node;
		}
		return true;
	}

	private void remove(Node<U> node){
		if(node.prev == null){
			node.next.prev = null;
			head = node.next;
		}else {
			node.prev = node.next;
		}
	}

	private static class Node<U>{
		Node next, prev;
		U data;

		public Node(Node next, Node prev, U data){
			this.next = next;
			this.prev = prev;
			this.data = data;
		}
	}
}
