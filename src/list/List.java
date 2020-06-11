package list;

public interface List<E> extends Iterable<E> {

	void add(E obj);
	void add(int index, E obj);
	boolean remove(E obj);
	boolean remove(int index);
	int removeAll(E obj);
	E get(int index);
	E set(int index, E obj);
	E first();
	E last();
	int firstIndex(E obj);
	int lastIndex(E obj);
	int size();
	boolean isEmpty();
	boolean contains(E obj);
	void clear();
}