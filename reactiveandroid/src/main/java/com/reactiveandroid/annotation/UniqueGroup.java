package com.reactiveandroid.annotation;

/**
 * Defines unique group. For every unique number entered,
 * it will generate a UNIQUE() column statement.
 */
public @interface UniqueGroup {

    /**
     * Specifies number for this group, to use it with "uniqueGroups" parameter in {@link Column} annotation
     */
    int groupNumber();

    /**
     * Defines how to handle conflicts for a unique column
     */
    ConflictAction onUniqueConflict() default ConflictAction.FAIL;

}
