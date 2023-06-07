package edu.uniandes.util;

import java.util.*;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class OrderedMap<K,V> {
    LinkedList<OMEntry<K,V>> iterable = new LinkedList<>();
    Map<K, Integer> table = new HashMap<>();

    public V put(K key, V value) {
        Integer index = table.get(key);
        if (index == null) {//Is not present
            iterable.add(new OMEntry<>(key, value));
            table.put(key, iterable.size() - 1);
        } else {
            iterable.get(index).setValue(value);
        }
        return value;
    }

    @SafeVarargs public final void putAll(OrderedMap<? extends K, ? extends V>... maps) {
        for (OrderedMap<? extends K, ? extends V> map : maps) {
            map.forEach(this::put);
        }
    }

    public V get(K key) {
        Integer index = table.get(key);
        if (index == null) return null;
        return iterable.get(index).value;
    }

    public V get(int index) {
        return iterable.get(index).value;
    }

    @SuppressWarnings("unchecked") public <T> T[] getKeys() {
        return (T[]) iterable.stream().map(OMEntry::getKey).toArray();
    }

    @SuppressWarnings("unchecked") public <T> T[] getValues() {
        return (T[]) iterable.stream().map(OMEntry::getValue).toArray();
    }

    public boolean remove(K key) {
        Integer index = table.remove(key);
        if (index == null) return false;
        iterable.remove((int) index);
        return true;
    }

    public void clear() {
        iterable.clear();
        table.clear();
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (OMEntry<K, V> entry : iterable) {
            K k; V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (IllegalStateException ise) {
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }

    static class OMEntry<K,V> implements Map.Entry<K,V> {
        private final K key;
        private V value;

        public OMEntry(K key,
                       V value) {
            this.key = key;
            this.value = value;
        }

        @Override public K getKey() {return key;}
        @Override public V getValue() {return value;}
        @Override public String toString() {return key + "=" + value;}

        @Override public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}
