package landmaster.landcore.api.coll;

import java.util.*;

import javax.annotation.*;

public final class ReverseListIterator<E> implements ListIterator<E> {
	private final @Nonnull ListIterator<E> original;
	
	public ReverseListIterator(@Nonnull ListIterator<E> original) {
		this.original = original;
	}

	@Override
	public boolean hasNext() {
		return original.hasPrevious();
	}

	@Override
	public E next() {
		return original.previous();
	}

	@Override
	public boolean hasPrevious() {
		return original.hasNext();
	}

	@Override
	public E previous() {
		return original.next();
	}

	@Override
	public int nextIndex() {
		return original.previousIndex();
	}

	@Override
	public int previousIndex() {
		return original.nextIndex();
	}

	@Override
	public void remove() {
		original.remove();
	}

	@Override
	public void set(E e) {
		original.set(e);
	}

	@Override
	public void add(E e) {
		original.add(e);
	}
	
}
