package com.reactiveandroid.internal.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface ModelCache<ModelClass> {

    /**
     * Adds a model to this cache.
     *
     * @param id    The id of the model to use.
     * @param model The model to add
     */
    void addModel(long id, @NonNull ModelClass model);

    /**
     * Removes a model from this cache.
     *
     * @param id The id of the model to remove.
     */
    @Nullable
    ModelClass removeModel(long id);

    /**
     * Clears out all models from this cache.
     */
    void clear();

    /**
     * @param id The id of the model to retrieve.
     * @return a model for the specified id. May be null.
     */
    @Nullable
    ModelClass get(long id);

    /**
     * Sets a new size for the underlying cache (if applicable) and may destroy the cache.
     *
     * @param size The size of cache to set to
     */
    void setCacheSize(int size);


}
