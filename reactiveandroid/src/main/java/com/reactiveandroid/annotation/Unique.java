package com.reactiveandroid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the column as unique, meaning its value cannot be repeated.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {

    /**
     * If false, we expect {@link #uniqueGroups()} to be specified.
     */
    boolean unique() default true;

    /**
     * Defines how to handle unique conflicts
     */
    ConflictAction onUniqueConflict() default ConflictAction.FAIL;

    /**
     * Marks this column as part of a unique group.
     */
    int[] uniqueGroups() default {};

}
