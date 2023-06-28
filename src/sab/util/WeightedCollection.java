package sab.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class WeightedCollection<E> implements Collection<E> {

    List<E> elements;
    double[] weights;

    public WeightedCollection() {
        elements = new ArrayList<>();
        weights = new double[0];
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public boolean contains(Object o) {
        return elements.contains(o);
    }

    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public boolean add(E e) {
        return add(e, 1);
    }


    public boolean add(E e, double weight) {
        if (elements.contains(e)) return false;
        double[] newWeights = new double[elements.size() + 1];
        int i = 0;
        if (weights.length > 0) {
            for (i = 0; i < elements.size(); i++) {
                newWeights[i] = weights[i];
            }
        }
        newWeights[i] = weight;
        weights = newWeights;
        return elements.add(e);
    }

    public boolean remove(Object o) {
        if (!elements.contains(o)) return false;
        int indexOf = elements.indexOf(o);
        double[] newWeights = new double[elements.size() - 1];
        for (int i = 0; i < elements.size() - 1; i++) {
            newWeights[i] = weights[i + (i > indexOf ? 1 : 0)];
        }
        return elements.remove(o);
    }

    public E getAndRemove() {
        double totalWeight = 0;
        for (double weight : weights) {
            totalWeight += weight;
        }
        double value = SabRandom.random() * totalWeight;
        E element = getFromValue(value);
        remove(element);
        return element;
    }

    public E get() {
        double totalWeight = 0;
        for (double weight : weights) {
            totalWeight += weight;
        }
        double value = SabRandom.random() * totalWeight;
        return getFromValue(value);
    }

    public E getFromValue(double value) {
        double totalWeight = 0;
        if (elements.size() == 1) return elements.get(0);
        for (int i = 0; i < weights.length; i++) {
            totalWeight += weights[i];
            if (value <= totalWeight) return elements.get(Math.min(i, elements.size() - 1));
        }
        return null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return elements.containsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        int i = 0;
        for (Object o : c) {
            if (elements.contains(o)) remove(o);
            i++;
        }
        return i > 0;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return elements.toArray(a);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            add(e);
        }
        return !c.isEmpty();
    }

    public boolean addAll(Collection<? extends E> c, double weight) {
        for (E e : c) {
            add(e, weight);
        }
        return !c.isEmpty();
    }

    public boolean addAll(E[] elements, double weight) {
        for (E e : elements) {
            add(e, weight);
        }
        return elements.length != 0;
    }
}
