package com.reactiveandroid.internal.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.annotation.Table;

public class ModelLruCache<ModelClass> implements ModelCache<ModelClass> {

    private final LruCache<Long, ModelClass> cache;

    /**
     * @param size The size, if less than or equal to 0 we set it to {@link Table#DEFAULT_CACHE_SIZE}.
     */
    public static <ModelClass> ModelLruCache<ModelClass> newInstance(int size) {
        if (size <= 0) {
            size = Table.DEFAULT_CACHE_SIZE;
        }
        return new ModelLruCache<>(size);
    }

    public ModelLruCache(int size) {
        this.cache = new LruCache<>(size);
    }


    @Override
    public void addModel(long id, @NonNull ModelClass model) {
        synchronized (cache) {
            cache.put(id, model);
        }
    }

    @Nullable
    @Override
    public ModelClass removeModel(long id) {
        ModelClass model;
        synchronized (cache) {
            model = cache.remove(id);
        }
        return model;
    }

    @Override
    public void clear() {
        synchronized (cache) {
            cache.evictAll();
        }
    }

    @Override
    public void setCacheSize(int size) {
        cache.resize(size);
    }

    @Nullable
    @Override
    public ModelClass get(long id) {
        return cache.get(id);
    }

}