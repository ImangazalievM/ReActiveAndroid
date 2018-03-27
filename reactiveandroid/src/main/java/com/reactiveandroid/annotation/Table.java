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

    int DEFAULT_CACHE_SIZE = 50;

    /**
     * @return Specifies a name of the table
     */
    String name() default "";

    /**
     * @return Specifies a database for the table
     */
    Class<?> database();

    UniqueGroup[] uniqueGroups() default {};

    IndexGroup[] indexGroups() default {};

    /**
     * @return Enables caching mechanism if "true"
     */
    boolean cachingEnabled() default false;

    /**
     * @return Cache size for this table
     */
    int cacheSize() default DEFAULT_CACHE_SIZE;

    /**
     * @return Create table upon startup
     */
    boolean createWithDatabase() default true;

}
