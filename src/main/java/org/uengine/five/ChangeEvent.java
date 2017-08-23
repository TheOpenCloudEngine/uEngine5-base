package org.uengine.five;

/**
 * Created by uengine on 2017. 8. 23..
 */
public abstract class ChangeEvent<T> {

    public T getObject() {
        return object;
    }

    T object;

    public ChangeEvent(T object) {
        this.object = object;
    }
}
