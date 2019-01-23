package org.archivemanager.services.cache;
import java.util.HashMap;
import java.util.Map;
public class LRUCache<K, V>
{
    int maxSize;
    int currentSize = 0;

    Map<K, ValueHolder<K, V>> map;
    LinkedList<K> queue;

    public LRUCache(int maxSize)
    {
        this.maxSize = maxSize;
        map = new HashMap<K, ValueHolder<K, V>>();
        queue = new LinkedList<K>();
    }

    private void freeSpace()
    {
        K k = queue.remove();
        map.remove(k);
        currentSize--;
    }

    public void put(K key, V val)
    {
        while(currentSize >= maxSize)
        {
                freeSpace();
        }
        if(map.containsKey(key))
        {//just heat up that item
                get(key);
                return;
        }
        ListNode<K> ln = queue.add(key);
        ValueHolder<K, V> rv = new ValueHolder<K, V>(val, ln);
        map.put(key, rv);               
        currentSize++;
    }

    public V get(K key)
    {
        ValueHolder<K, V> rv = map.get(key);
        if(rv == null) return null;
        queue.remove(rv.queueLocation);
        rv.queueLocation = queue.add(key);//this ensures that each item has only one copy of the key in the queue
        return rv.value;
    }
}

class ListNode<K>
{
    ListNode<K> prev;
    ListNode<K> next;
    K value;
    public ListNode(K v)
    {
        value = v;
        prev = null;
        next = null;
    }
}

class ValueHolder<K,V>
{
    V value;
    ListNode<K> queueLocation;
    public ValueHolder(V value, ListNode<K> ql)
    {
        this.value = value;
        this.queueLocation = ql;
    }
}

class LinkedList<K>
{
    ListNode<K> head = null;
    ListNode<K> tail = null;

    public ListNode<K> add(K v)
    {
        if(head == null)
        {
                assert(tail == null);
                head = tail = new ListNode<K>(v);
        }
        else
        {
                tail.next = new ListNode<K>(v);
                tail.next.prev = tail;
                tail = tail.next;
                if(tail.prev == null)
                {
                        tail.prev = head;
                        head.next = tail;
                }
        }
        return tail;
    }

    public K remove()
    {
        if(head == null)
                return null;
        K val = head.value;
        if(head.next == null)
        {
                head = null;
                tail = null;
        }
        else
        {
                head = head.next;
                head.prev = null;
        }
        return val;
    }

    public void remove(ListNode<K> ln)
    {
        ListNode<K> prev = ln.prev;
        ListNode<K> next = ln.next;
        if(prev == null)
        {
                head = next;
        }
        else
        {
                prev.next = next;
        }
        if(next == null)
        {
                tail = prev;
        }
        else
        {
                next.prev = prev;
        }               
    }
}
