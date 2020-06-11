package map;

import list.List;

public interface Map<K, V> {
	V get(K key);
	void put(K key, V value);
	V remove(K key);
	boolean containsKey(K key);
	boolean containsValue(V value);
	List<K> getKeys();
	List<V> getValues();
	int size();
	boolean isEmpty();
	void clear();
}
