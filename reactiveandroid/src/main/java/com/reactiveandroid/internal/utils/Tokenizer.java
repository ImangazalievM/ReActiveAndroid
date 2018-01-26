
package com.reactiveandroid.internal.utils;

import java.io.IOException;
import java.io.InputStream;


public class Tokenizer {

    private final InputStream stream;
    private boolean isNext;
    private int current;

    public Tokenizer(final InputStream in) {
        this.stream = in;
    }

    public boolean hasNext() throws IOException {
        if (!isNext) {
            isNext = true;
            current = this.stream.read();
        }
        return current != -1;
    }

    public int next() throws IOException {
        if (!isNext) {
            current = stream.read();
        }
        isNext = false;
        return current;
    }

    public boolean skip(String s) throws IOException {
        if (s == null || s.length() == 0) {
            return false;
        }

        if (s.charAt(0) != current) {
            return false;
        }

        int len = s.length();
        stream.mark(len - 1);
        for (int n = 1; n < len; n++) {
            int value = stream.read();

            if (value != s.charAt(n)) {
                stream.reset();
                return false;
            }
        }
        return true;
    }

}
