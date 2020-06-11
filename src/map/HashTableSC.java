/** HashTableSC:
 * Data structure that stores key-value pairs using a hash table.
 * Uses a array as its primary container and for every given key-value,
 * it takes the key and computes a hash code corresponding to the index 
 * in the array. When to keys have the same hash code (refereed to as a 
 * hash collision), then we insert both keys in the same index using a 
 * linked structure.
 * 
 * @author Abdiel Cortes, GitHub: AbdielCortes
 */

package map;

import list.ArrayList;
import list.List;

@SuppressWarnings("unchecked")
public class HashTableSC<K, V> implements Map<K, V> {
	
	/* Node class that composes linked structure used within map
	 * each element within the array is null when empty or a 
	 * Node that can point to other nodes in a linked list way.  
	 */
	private class Node {
		private K key;
		private V value;
		private Node next;
		
		/** Constructor that assigns all the values to their corresponding fields.
		 * 
		 * @param key: object used for hashing
		 * @param value: object to be stored
		 * @param next: next node in the linked structure
		 */
		public Node(K key, V value, Node next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}
		
		/** Constructor that only takes the key and the value, sets the next as null,
		 * delegates work to first constructor. Used for when node is the only note in 
		 * that position on the array or when the node is the end of the linked structure.
		 * @see Node(K key, V value, Node next)
		 *  
		 * @param key: object used for hashing
		 * @param value: object to be stored
		 */
		public Node(K key, V value) {
			this(key, value, null);
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public Node getNext() {
			return next;
		}

		@SuppressWarnings("unused")
		public void setKey(K key) {
			this.key = key;
		}

		public void setValue(V value) {
			this.value = value;
		}

		public void setNext(Node next) {
			this.next = next;
		}
		
		/** Takes all the values of the Node and set them to null.
		 * Used for when we want to remove a value from our HashMap.
		 */
		public void clear()  {
			this.key = null;
			this.value = null;
			this.next = null;
		}
		
	} // End of node class
	
	
	private Object[] hashContainer; // Array that stores the head node for each linked structure corresponding to a hash code

	private int currentSize; // The current amount of key-value pairs stored
	
	private final static double LOAD_FACTOR = 0.75; // The max ratio of numOfElements / sizeOfArray
	
	
	/** Constructor that takes an integer corresponding to the size of the array to be created.
	 * 
	 * @param initialSize: size of array to be created
	 */
	public HashTableSC(int initialSize) {
		if (initialSize < 1) {
			throw new IllegalArgumentException("Size must be larger than 0.");
		}
		
		this.hashContainer = new Object[initialSize];
		this.currentSize = 0;
	}
	
	/** Constructor that creates the array with an initial size of 128.
	 * Delegates work to other constructor.
	 * @see HashMap(int initialSize)
	 */
	public HashTableSC() {
		this(11);
	}
	
	/** Function that return the hash code for a given key.
	 * It takes the key Object and uses the Object.toString(), 
	 * then takes the ASCII value for each character and sums them.
	 * 
	 * @param key: object used for hashing
	 * @return hash code of key
	 */
	private int hashFunction(K key) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null.");
		}
		
		int sum = 0;
		String converted = key.toString();
		// A hash collision will occur when a string is a contains the same
		// characters as an already existing key, but in a different order
		// so for a given string with length n, we have n! different permutations
		// of that string that will give the same hash code.
		
		for (int i = 0; i < converted.length(); i++) {
			sum += converted.charAt(i);
		}
		
		return sum;
	}
	
	/** Takes a key and computes its hash code using hashFunction(key), 
	 * then it takes that hash code and using modulo returns a index.
	 * @see hashFunction(K key).
	 * 
	 * @param key: object used for hashing
	 * @return index of the hashContainer where the key belongs
	 */
	private int getHashIndex(K key) {
		return hashFunction(key) % hashContainer.length;
	}
	
	/** Computes hash code for a given key and return the node at that position.
	 * 
	 * @param key: object used for hashing
	 * @return node at the given key's hash position 
	 */
	private Node getHeadNode(K key) {
		return (Node) this.hashContainer[getHashIndex(key)];
	}

	/** Gets value corresponding to the given key.
	 * 
	 * @param key: object used for hashing
	 * @return value corresponding to key, null if key dosen't exist in HashMap 
	 */
	@Override
	public V get(K key) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null.");
		}
		
		Node head = getHeadNode(key);
		if (head == null) {
			return null;
		} else { 
			if (head.getKey().equals(key)) {
				return head.getValue();
			} else {
				for (Node currentNode = head.getNext(); currentNode != null; currentNode = currentNode.getNext()) {
					if (currentNode.getKey().equals(key)) {
						return currentNode.getValue();
					}
				}
			}
		}
		return null;
	}

	/** Inserts a key-value pair into the HashTableSC, if that key already exists,
	 * then the value of that key is updated.
	 * 
	 * @param key: object used for hashing
	 * @param value: object to be stored
	 */
	@Override
	public void put(K key, V value) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null.");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null.");
		}
		
		int index = getHashIndex(key);
		if (this.hashContainer[index] == null) { // if there are no nodes at that index
			this.hashContainer[index] = new Node(key, value);
			this.currentSize++;
		} else if (containsKey(key)){ // key already exists so we update its value
			Node head = (Node) this.hashContainer[index];
			for (Node currentNode = head; currentNode != null; currentNode = currentNode.getNext()) {
				if (currentNode.getKey().equals(key)) {
					currentNode.setValue(value);
				}
			}
		} else { // key dosen't exist so we insert it at the head of the sublist
			Node previousHead = (Node) this.hashContainer[index];
			Node newNode = new Node(key, value, previousHead);
			this.hashContainer[index] = newNode;
			this.currentSize++;
		}	
		
		// if inserting this new key-value pair caused the map to go over the load factor
		if (((this.size()) / this.hashContainer.length) > LOAD_FACTOR) { 
			rehash();
		}
	}
	
	/** Called when the load factor (#elements / arraySize) of the map get to large.
	 * takes all the key-value pairs and hashes them into a new array that is
	 * twice the size of the previous array.
	 */
	private void rehash() {
		Object[] larger = new Object[this.hashContainer.length * 2];
		
		for (Object head: this.hashContainer) { // iterates through entire array
			if (head != null) { // if at a certain index contains key-value pairs
				for (Node currentNode = (Node) head; currentNode != null; currentNode = currentNode.getNext()) {
					int index = hashFunction(currentNode.getKey()) % larger.length; // gets new hash index
					
					if (larger[index] == null) { // if there are no key-value pairs in that index
						larger[index] = new Node(currentNode.getKey(), currentNode.getValue());
					} else { // else a hash collision occurred
						Node previousHead = (Node) larger[index]; // store previous node at larger[index]
						Node newNode = new Node(currentNode.getKey(), currentNode.getValue(), previousHead); // create a new node with previousHead as next
						larger[index] = newNode; // insert newNode at index
					}
				}
			}
		}
		
		int size = this.currentSize;
		this.clear(); // set everything in the previous hashContainer to null
		this.currentSize = size;
		
		this.hashContainer = larger; // set hashConainer to the new larger array
	}

	/** Removes a key-value pair from the HashTableSC, returns the value
	 * corresponding to that key.
	 * 
	 * @param key: object used for hashing
	 * @return value corresponding to that key, null if key doesn't exist
	 */
	@Override
	public V remove(K key) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null.");
		}
		
		Node head = getHeadNode(key);
		if (head == null) {
			return null;
		} else if (head.getKey().equals(key)) {
			V result = head.getValue();
			Node newHead = head.getNext();
			this.hashContainer[getHashIndex(key)] = newHead;
			head.clear();
			this.currentSize--;
			return result;
		} else {
			Node currentNode = head;
			while (!currentNode.getNext().getKey().equals(key) && currentNode.getNext() != null) {
				currentNode = currentNode.getNext();
			}
			
			if (currentNode.getNext() != null) {
				V result = currentNode.getNext().getValue();
				Node deleteNode = currentNode.getNext();
				currentNode.setNext(deleteNode.getNext());
				deleteNode.clear();
				this.currentSize--;
				return result;
			}
		}
		return null;
	}

	/** Checks if the HashTableSC contains a given key.
	 * 
	 * @param key: object used for hashing
	 * @return true if HashTableSC contains key, false otherwise
	 */
	@Override
	public boolean containsKey(K key) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null.");
		}
		
		return get(key) != null;
	}
	
	/** Checks if the HashTableSC contains a given value.
	 * 
	 * @param value: object to be stored
	 * @return true if HashTableSC contains value, false otherwise 
	 */
	@Override
	public boolean containsValue(V value) {
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null.");
		}
		
		for (Object head: this.hashContainer) {
			if (head != null) {
				for (Node currentNode = (Node) head; currentNode != null; currentNode = currentNode.getNext()) {
					if (currentNode.getValue().equals(value)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	/** Takes all the keys in the HashTableSC and inserts them into an array.
	 * 
	 * @return array containing all the keys in the HashTableSC
	 */
	@Override
	public List<K> getKeys() {
		List<K> keys = new ArrayList<K>(this.size());
		
		for (Object head: this.hashContainer) {
			if (head != null) {
				for (Node currentNode = (Node) head; currentNode != null; currentNode = currentNode.getNext()) {
					keys.add(currentNode.getKey());
				}
			}
		}
		
		return keys;
	}

	/** Takes all the values in the HashTableSC and inserts them into an array.
	 * 
	 * @return array containing all the values in the HashTableSC
	 */
	@Override
	public List<V> getValues() {
		List<V> values = new ArrayList<V>(this.size());
		
		for (Object head: this.hashContainer) {
			if (head != null) {
				for (Node currentNode = (Node) head; currentNode != null; currentNode = currentNode.getNext()) {
					values.add(currentNode.getValue());
				}
			}
		}
		
		return values;
	}

	/** Returns the amount of key-value pairs stored in the HashTableSC.
	 * 
	 * @return currentSize
	 */
	@Override
	public int size() {
		return currentSize;
	}

	/** Check if there are key-value pairs stored in the HashTableSC.
	 * 
	 * @return true if there are no key-value pairs stored in the HashTableSC.
	 */
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/** Takes every key and value in the HashTableSC and sets them to null.
	 */
	@Override
	public void clear() {
		for (int i = 0; i < this.hashContainer.length; i++) {
			Node currentNode = (Node) this.hashContainer[i];
			while (currentNode != null) {
				Node delete = currentNode;
				currentNode = delete.getNext();
				delete.clear();
			}
			this.hashContainer[i] = null;
		}
		this.currentSize = 0;
	}
	
	/** Generates a string representing the HashTableSC in a easy to read way that
	 * can be seen in the debugger.
	 * [key1:value1, key2:value2, ... , keyN, valueN]
	 * 
	 * @return string representing the HashTableSC
	 */
	@Override
	public String toString() {
		if (isEmpty()) {
			return "[]";
		}
		String result = "[";
		
		for (Object head: this.hashContainer) {
			if (head != null) {
				for (Node currentNode = (Node) head; currentNode != null; currentNode = currentNode.getNext()) {
					result += currentNode.getKey() + ":" + currentNode.getValue() + ", ";
				}
			}
		}
		
		result = result.substring(0, result.length()-2) + "]";
		return result;
	}

}
