
package com.doing.library.core.collection;

import java.util.Collection;
import java.util.LinkedList;

public class NoDuplicatesLinkedList<E> extends LinkedList<E> {

    public NoDuplicatesLinkedList() {
        super();
    }

    public NoDuplicatesLinkedList(final Collection<? extends E> collection) {
        super(collection);
    }

    @Override
    public void add(final int index, final E element) {
        if (!contains(element)) {
            super.add(index, element);
        }
    }

    @Override
    public boolean add(final E e) {
        return !contains(e) && super.add(e);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> collection) {
        final Collection<E> copy = new LinkedList<E>(collection);
        copy.removeAll(this);
        return super.addAll(index, copy);
    }

    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        final Collection<E> copy = new LinkedList<E>(collection);
        copy.removeAll(this);
        return super.addAll(copy);
    }
}
