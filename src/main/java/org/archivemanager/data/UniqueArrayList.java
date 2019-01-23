package org.archivemanager.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class UniqueArrayList<E> extends ArrayList<E> {
    private static final long serialVersionUID = -3266211134567313423L;

    
	@Override
    public boolean add(E element) {
        if(this.contains(element)) {
        	this.remove(element);            
        }
        return super.add(element);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        Collection<E> copy = new LinkedList<E>(collection);
        copy.removeAll(this);
        return super.addAll(copy);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        Collection<E> copy = new LinkedList<E>(collection);
        copy.removeAll(this);
        return super.addAll(index, copy);
    }

    @Override
    public void add(int index, E element) {
        if(this.contains(element)) {
            this.remove(element);
        }
        super.add(index, element);
    }
}   