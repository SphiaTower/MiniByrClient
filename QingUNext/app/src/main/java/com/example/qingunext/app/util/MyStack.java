package com.example.qingunext.app.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Rye on 7/19/2015.
 */
public class MyStack<T> implements Iterable<T> {
    private List<T> list = new ArrayList<>();

    private MyStack() {
    }

    public static <T> MyStack<T> newInstance() {
        return new MyStack<>();
    }

    public void push(T item) {
        list.add(item);
    }

    public T pop() {
        int index = list.size() - 1;
        if (index < 0) {
            return null;
        } else {
            return list.remove(index);
        }
    }

    public T peek() {
        int index = list.size() - 1;
        if (index < 0) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < list.size();
            }

            @Override
            public T next() {
                return list.get(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
