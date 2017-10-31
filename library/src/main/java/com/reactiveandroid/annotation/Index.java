package com.reactiveandroid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Creates an index for this column. A single column can belong to multiple
 * indexes within the same table if you wish.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

    /**
     * Marks this column as part of a index group.
     */
    int[] indexGroups() default {};

}
