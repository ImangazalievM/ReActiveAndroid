package com.reactiveandroid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	/**
	 * Specifies a name for this column
	 */
	String name() default "";

	/**
	 * Specifies column length
	 */
	int length() default -1;

	/**
	 * Marks column as NOT NULL
	 */
	boolean notNull() default false;

	/**
	 * Defines how to handle conflicts for not null column
	 */
	ConflictAction onNullConflict() default ConflictAction.FAIL;

	/**
	 * Defines action action to be performed
	 * on delete of referenced record.
	 */
	ForeignKeyAction onDelete() default ForeignKeyAction.NO_ACTION;

	/**
	 * Defines action action to be performed
	 * on update of referenced record.
	 */
	ForeignKeyAction onUpdate() default ForeignKeyAction.NO_ACTION;

	/**
	 * Marks the column as unique, meaning its value cannot be repeated.
	 */
	boolean unique() default false;

	/**
	 * Defines how to handle unique conflicts
	 */
	ConflictAction onUniqueConflict() default ConflictAction.FAIL;

	/**
	 * Marks this column as part of a unique group.
	 */
	int[] uniqueGroups() default {};

	/**
	 * Creates an index for this column. A single column can belong to multiple
	 * indexes within the same table if you wish.
	 */
	boolean index() default false;

	/**
	 * Marks this column as part of a index group.
	 */
	int[] indexGroups() default {};

	/**
	 * Specifies COLLATE method for comparing string columns
	 */
	Collate collate() default Collate.NONE;
	
}
