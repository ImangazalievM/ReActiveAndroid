package com.reactiveandroid.internal.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.Model;

public interface ModelCache<ModelClass extends Model> {

    /**
     * Adds a model to this cache.
     *
     * @param id    The id of the model to use.
     * @param model The model to add
     */
    void addModel(@Nullable long id, @NonNull ModelClass model);

    /**
     * Removes a model from this cache.
     *
     * @param id The id of the model to remove.
     */
    ModelClass removeModel(@NonNull long id);

    /**
     * Clears out all models from this cache.
     */
    void clear();

    /**
     * @param id The id of the model to retrieve.
     * @return a model for the specified id. May be null.
     */
    ModelClass get(@Nullable long id);

    /**
     * Sets a new size for the underlying cache (if applicable) and may destroy the cache.
     *
     * @param size The size of cache to set to
     */
    void setCacheSize(int size);


}
