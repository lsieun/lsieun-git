package lsieun.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    public static String getBlobHash(String content) {
        byte[] content_bytes = content.getBytes(StandardCharsets.UTF_8);
        int content_bytes_length = content_bytes.length;
        String blob_text = String.format("blob %d\0%s\n", content_bytes_length + 1, content);
        byte[] blob_bytes = blob_text.getBytes(StandardCharsets.UTF_8);
        byte[] digest_bytes = sha1(blob_bytes);
        return HexUtils.toHex(digest_bytes);
    }

    public static byte[] sha1(byte[] bytes) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.reset();
            messageDigest.update(bytes);
            return messageDigest.digest();
        }
        catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        String sha1 = getBlobHash("");
        System.out.println(sha1);
    }
}
