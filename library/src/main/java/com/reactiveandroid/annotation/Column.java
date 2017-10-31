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
	 * Specifies COLLATE method for comparing string columns
	 */
	Collate collate() default Collate.NONE;

}
