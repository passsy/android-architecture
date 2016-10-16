package com.example.android.architecture.blueprints.todoapp.base;

/**
 * change notify callback of a viewmodel
 * @param <T>
 */
public interface NotifyChangeListener<T> {

    void onChange(final T t);
}
