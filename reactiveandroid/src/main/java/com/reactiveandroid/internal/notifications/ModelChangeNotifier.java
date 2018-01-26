package com.reactiveandroid.internal.notifications;

import android.support.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ModelChangeNotifier {

    private static ModelChangeNotifier notifier;

    @NonNull
    public static ModelChangeNotifier get() {
        if (notifier == null) {
            notifier = new ModelChangeNotifier();
        }
        return notifier;
    }

    private final Map<Class<?>, Set<OnModelChangedListener>> modelChangedListenerMap = new LinkedHashMap<>();
    private final Map<Class<?>, Set<OnTableChangedListener>> tableChangedListenerMap = new LinkedHashMap<>();

    public <T> void notifyModelChanged(@NonNull T model, @NonNull ChangeAction action) {
        final Set<OnModelChangedListener> listeners = modelChangedListenerMap.get(model.getClass());
        if (listeners != null) {
            for (OnModelChangedListener listener : listeners) {
                if (listener != null) {
                    listener.onModelChanged(model, action);
                }
            }
        }
    }

    public void notifyTableChanged(@NonNull Class<?> table, @NonNull ChangeAction action) {
        final Set<OnTableChangedListener> listeners = tableChangedListenerMap.get(table);
        if (listeners != null) {
            for (OnTableChangedListener listener : listeners) {
                if (listener != null) {
                    listener.onTableChanged(table, action);
                }
            }
        }
    }


    public <T> void registerForModelChanges(@NonNull Class<T> table,
                                            @NonNull OnModelChangedListener<T> listener) {
        Set<OnModelChangedListener> listeners = modelChangedListenerMap.get(table);
        if (listeners == null) {
            listeners = new LinkedHashSet<>();
            modelChangedListenerMap.put(table, listeners);
        }
        listeners.add(listener);
    }

    public <T> void unregisterForModelStateChanges(@NonNull Class<T> table,
                                                   @NonNull OnModelChangedListener<T> listener) {
        Set<OnModelChangedListener> listeners = modelChangedListenerMap.get(table);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public <T> void registerForTableChanges(@NonNull Class<T> table,
                                            @NonNull OnTableChangedListener listener) {
        Set<OnTableChangedListener> listeners = tableChangedListenerMap.get(table);
        if (listeners == null) {
            listeners = new LinkedHashSet<>();
            tableChangedListenerMap.put(table, listeners);
        }
        listeners.add(listener);
    }

    public <T> void unregisterForTableChanges(@NonNull Class<T> table,
                                              @NonNull OnTableChangedListener listener) {
        Set<OnTableChangedListener> listeners = tableChangedListenerMap.get(table);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

}
