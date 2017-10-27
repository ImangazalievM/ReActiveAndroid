package com.reactiveandroid.query;

public interface Query {

    /**
     * @return Generated SQL query statement
     */
    String getSql();

    /**
     * @return Query arguments
     */
    String[] getArgs();

    final class MalformedQueryException extends RuntimeException {
        public MalformedQueryException(String detailMessage) {
            super(detailMessage);
        }
    }

}