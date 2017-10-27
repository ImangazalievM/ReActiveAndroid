package com.reactiveandroid.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MiscUtils {

    @NonNull
    public static String md5Hex(String input) throws RuntimeException {
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(input.getBytes());

            byte[] a = digest.digest();
            int len = a.length;

            StringBuilder sb = new StringBuilder(len << 1);
            for (byte anA : a) {
                sb.append(Character.forDigit((anA & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(anA & 0x0f, 16));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("An error occurred while generating MD5-hash");
        }
    }

}
