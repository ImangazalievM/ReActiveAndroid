package com.reactiveandroid.query;

import android.support.annotation.NonNull;

public interface Query {

    /**
     * @return Generated SQL query statement
     */
    @NonNull
    String getSql();

    /**
     * @return Query arguments
     */
    @NonNull
    String[] getArgs();

    final class MalformedQueryException extends RuntimeException {
        public MalformedQueryException(String detailMessage) {
            super(detailMessage);
        }
    }

}