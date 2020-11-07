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
		if (capacity == 0) {
			return null;
		}
		if(!cache.containsKey(key)){ // miss
			if(numElements == capacity) {
				remove(head);
			}else{
				numElements++;
			}
			U newEntry = provider.get(key);
			Node<U> newNode = new Node(null, null, newEntry);
			cache.put(key, newNode);
			add(newNode);
			numMisses++;
			return newEntry;
		}else if(cache.get(key).isRemoved()) { // already at capacity
			remove(head);
			U newEntry = provider.get(key);
			Node<U> newNode = new Node(null, null, newEntry);
			cache.put(key, newNode);
			add(newNode);
			numMisses++;
			return newEntry;
		} else{ // hit
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

	public void printLinked(){
		Node<U> current = head;
		System.out.print(current.data + " ");
		while(current.next != null){
			System.out.print(current.next.data + " ");
			current = current.next;
		}
		System.out.println();
	}

	private void add(Node<U> node) {
		node.setIsRemoved(false);
		if (head == null){
			head = node;
			tail = node;
		}else{
			tail.next = node;
			node.prev = tail;
			tail = node;
		}


		/*
		if (head == null) {
			head = node;
			tail = node;
		} else {
			tail.next = node;
			node.prev = tail;
			tail = node;
			tail.next = null;
		}

		 */
	}

	private void remove(Node<U> node){
		node.setIsRemoved(true);
		if(node.equals(head)){//it is the head node
			if(node.next == null){// Head is the only element
				head = null;
				tail = null;
			}else{ // There are elements after head
				node.next.prev = null;
				head = node.next;
				node.next = null; // make sure no faults occur with this node
			}
		} else{ // not head node
			if (node.equals(tail)){ // tail
				node.prev.next = null;
				tail = node.prev;
				node.prev = null; // no faults
			}else{ // not tail
				node.next.prev = node.prev;
				node.prev.next = node.next;
				node.next = null;
				node.prev = null;
			}
		}

	}

	private static class Node<U>{
		Node next, prev;
		boolean isRemoved = false;
		U data;

		public Node(Node next, Node prev, U data){
			this.next = next;
			this.prev = prev;
			this.data = data;
		}

		public boolean isRemoved(){
			return isRemoved;
		}
		public void setIsRemoved(boolean isRemoved){
			this.isRemoved = isRemoved;
		}
	}
}
