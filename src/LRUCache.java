import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of <tt>Cache</tt> that uses a least-recently-used (LRU)
 * eviction policy.
 */
public class LRUCache<T, U> implements Cache<T, U> {
	private int capacity;
	private int numMisses;
	private int numElements;

	private DataProvider<T, U> provider;
	private Map<T, Node<T, U>> cache = new HashMap<>();
	private Node<T, U> head, tail;

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

		if (capacity == 0) {// If the cache's capacity is 0, return null
			return provider.get(key);
		}

		if(!cache.containsKey(key)){ // Miss, Account for the fact that cache could still contain the key or encounter a new key

			if(numElements == capacity) { // If the cache is already at capacity, have to remove a value from the cache
				cache.remove(head.key);
				remove(head);
			}else{ // If the cache isn't at capacity, no elements need to be removed
				numElements++;
			}

			// Get value from data provider & make a new Node
			U newEntry = provider.get(key);
			Node<T, U> newNode = new Node(null, null, newEntry, key);

			// Add the key value pair to the cache
			cache.put(key, newNode);

			// Add the new Node to the recency list
			add(newNode);

			numMisses++;
			return newEntry;

		} else{ // Hit

			// Get the current node
			Node<T, U> current = cache.get(key);

			// Remove & re-add the node, to update its place in the recency linked list
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

	/**
	 * Adds a new value to the end of the recency linked list
	 * @param node
	 */
	private void add(Node<T, U> node) {
		
		if (head == null){ // Check if the linked list is empty
			head = node;
			tail = node;
		}else{ // Else add a node to the tail of the list
			tail.next = node;
			node.prev = tail;
			tail = node;
		}
	}

	/**
	 * Removes a specific Node from the recency linked list
	 * @param node
	 */
	private void remove(Node<T, U> node){

		if(node.equals(head)){ // Removing head Node

			if(node.next == null){// Head is the only element, then make the list empty
				head = null;
				tail = null;
			}else{ // If Head isn't the only element, then properly remove it
				node.next.prev = null;
				head = node.next;
				node.next = null;
			}

		} else{ // Removing not head Node

			if (node.equals(tail)){ // Removing tail Node
				node.prev.next = null;
				tail = node.prev;
				node.prev = null;

			}else{ // Removing a non-head & non-tail node
				node.next.prev = node.prev;
				node.prev.next = node.next;
				node.next = null;
				node.prev = null;
			}
		}

	}

	/**
	 * An implementation of Node used specifically in a doubly linked list storing the recency of the cache
	 * @param <U>
	 */
	private static class Node<T, U>{
		Node next, prev;
		U data;
		T key;

		/**
		 * Constructor for Node
		 * @param next
		 * @param prev
		 * @param data
		 */
		public Node(Node next, Node prev, U data, T key){
			this.next = next;
			this.prev = prev;
			this.data = data;
			this.key = key;
		}

	}
}
