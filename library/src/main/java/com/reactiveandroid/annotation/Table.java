package com.reactiveandroid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    String DEFAULT_ID_NAME = "id";
    int DEFAULT_CACHE_SIZE = 50;

    /**
     * Specifies a name of the table
     */
    String name() default "";

    /**
     * Specifies a database for the table
     */
    Class<?> database();

    /**
     * Specifies a name for primary key column
     */
    String idName() default DEFAULT_ID_NAME;

    UniqueGroup[] uniqueGroups() default {};

    IndexGroup[] indexGroups() default {};

    /**
     * Enables caching mechanism if "true"
     */
    boolean cachingEnabled() default false;

    /**
     * Cache size for this table
     */
    int cacheSize() default DEFAULT_CACHE_SIZE;

}
