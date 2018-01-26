package com.reactiveandroid.internal.log;

/**
 * Controls the level of logging.
 */
public enum LogLevel {
    /**
     * No logging.
     */
    NONE,
    /**
     * Log basic events.
     */
    BASIC,
    /**
     * Log all queries.
     */
    FULL;

    public boolean log(LogLevel logLevel) {
        return this.ordinal() >= logLevel.ordinal();
    }

}