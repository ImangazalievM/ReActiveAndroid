package com.reactiveandroid.annotation;

/**
 * Represents a SQL COLLATE method for comparing string columns.
 */
public enum ConflictAction {

    ROLLBACK, ABORT, FAIL, IGNORE, REPLACE

}