package com.reactiveandroid.internal.serializer;

/**
 *
 */
public class Blob {

    private byte[] blob;

    public Blob() {
    }

    public Blob(byte[] blob) {
        this.blob = blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public byte[] getBlob() {
        return blob;
    }

}