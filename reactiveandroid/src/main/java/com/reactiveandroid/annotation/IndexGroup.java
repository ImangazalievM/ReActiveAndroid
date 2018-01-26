package com.reactiveandroid.annotation;

/**
 * Defines index group. For every unique number entered,
 * it will generate a CREATE INDEX statement.
 */
public @interface IndexGroup {

    /**
     * Specifies number for this group, to use it with "indexGroups" parameter in {@link Column} annotation
     */
    int groupNumber();

    /**
     * Specifies postfix for index table. For example, if name equals "my_index",
     * then index table name will be index_ModelTableName_my_index
     */
    String name();

    /**
     * Specifies postfix for index table. For example, if name equals "my_index",
     * then index table name will be index_ModelTableName_my_index
     */
    boolean unique() default false;

}
