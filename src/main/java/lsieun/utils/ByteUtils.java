package lsieun.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteUtils {
    public static final int NOT_EXIST = -1;

    public static int findFistNUL(byte[] bytes) {
        return findFistNUL(bytes, 0);
    }

    public static int findFistNUL(byte[] bytes, int startIndex) {
        return findFist(bytes, startIndex, 0);
    }

    public static int findFist(byte[] bytes, int startIndex, int val) {
        byte target = (byte) val;
        if (bytes == null) return NOT_EXIST;
        int length = bytes.length;
        if (length < 1) return NOT_EXIST;
        if (startIndex < 0 || startIndex >= length) return NOT_EXIST;

        for (int i = startIndex; i < length; i++) {
            byte b = bytes[i];
            if (b == target) {
                return i;
            }
        }
        return NOT_EXIST;
    }

    public static byte[] fromInt(final int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >> 24);
        bytes[1] = (byte) ((i >> 16) & 0xFF);
        bytes[2] = (byte) ((i >> 8) & 0xFF);
        bytes[3] = (byte) (i & 0xFF);
        return bytes;
    }

    public static int toInt(final byte[] bytes) {
        return toInt(bytes, 0);
    }

    public static int toInt(final byte[] bytes, final int defaultValue) {
        if (bytes == null || bytes.length < 1) return defaultValue;

        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }

    public static byte[] merge(byte[]... bytesArray) {
        if (bytesArray == null || bytesArray.length < 1) return null;

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        for (byte[] bytes : bytesArray) {
            if (bytes != null && bytes.length > 0) {
                try {
                    bao.write(bytes);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bao.toByteArray();
    }

    public static byte[] concatenate(byte[] bytes1, byte[] bytes2) {
        int len1 = bytes1.length;
        int len2 = bytes2.length;

        byte[] result_bytes = new byte[len1 + len2];

        System.arraycopy(bytes1, 0, result_bytes, 0, len1);
        System.arraycopy(bytes2, 0, result_bytes, len1, len2);

        return result_bytes;
    }

    public static byte[] concatenate(byte[] bytes1, byte[] bytes2, byte[] bytes3) {
        int len1 = bytes1.length;
        int len2 = bytes2.length;
        int len3 = bytes3.length;

        byte[] result_bytes = new byte[len1 + len2 + len3];

        System.arraycopy(bytes1, 0, result_bytes, 0, len1);
        System.arraycopy(bytes2, 0, result_bytes, len1, len2);
        System.arraycopy(bytes3, 0, result_bytes, len1 + len2, len3);

        return result_bytes;
    }
}
