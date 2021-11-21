package lsieun.utils;

public class BitUtils {
    private static final String EMPTY = "";

    public static String fromByte(byte byteValue) {
        int length = Byte.SIZE;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int val = ((byte) (byteValue << i) < 0) ? 0x31 : 0x30;
            bytes[i] = (byte) val;
        }
        return new String(bytes);
    }

    public static String fromByteArray(byte[] byteArray) {
        if (byteArray == null) return EMPTY;

        int length = byteArray.length;
        if (length == 0) return EMPTY;

        int totalSize = length * Byte.SIZE;
        byte[] bytes = new byte[totalSize];
        for (int i = 0; i < totalSize; i++) {
            int byteIndex = i / Byte.SIZE;
            int bitIndex = i % Byte.SIZE;
            int val = ((byte) (byteArray[byteIndex] << bitIndex) < 0) ? 0x31 : 0x30;
            bytes[i] = (byte) val;
        }
        return new String(bytes);
    }
}
