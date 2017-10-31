package com.reactiveandroid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks field that is the primary key.
 * Primary key field should be Long type
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {

    String DEFAULT_ID_NAME = "id";

    /**
     * Specifies a name for primary key column
     */
    String name() default DEFAULT_ID_NAME;

}
