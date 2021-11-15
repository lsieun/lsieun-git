package lsieun.git.objects;

import lsieun.utils.ByteUtils;
import lsieun.utils.HashUtils;
import lsieun.utils.HexUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GitObject {
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String NEW_LINE = "\n";

    public GitObjectType objectType;
    public byte[] content;

    public String getSHA1() {
        byte[] bytes = toByteArray();
        byte[] sha1_bytes = HashUtils.sha1(bytes);
        return HexUtils.toHex(sha1_bytes).toLowerCase();
    }

    public byte[] toByteArray() {
        String first_part_str = String.format("%s %d", objectType.getValue(), content.length);
        byte[] first_part_bytes = first_part_str.getBytes(StandardCharsets.UTF_8);
        byte[] second_part_bytes = new byte[]{0};
        return ByteUtils.concatenate(first_part_bytes, second_part_bytes, content);
    }

    @Override
    public String toString() {
        String content_str = new String(content, StandardCharsets.UTF_8);
        return String.format("%s %d %s", objectType.getValue(), content.length, content_str);
    }

    public static GitObject fromByteArray(byte[] bytes) {
        // 第一步，将bytes分成两部分
        int index = ByteUtils.findFistNUL(bytes);
        int length = bytes.length;

        byte[] first_part_bytes = Arrays.copyOfRange(bytes, 0, index);
        byte[] second_part_bytes = Arrays.copyOfRange(bytes, index + 1, length);

        // 第二步，获取类型（blob、tree、commit、tag），并验证内容（content）长度是否正确
        String first_part_str = new String(first_part_bytes, StandardCharsets.UTF_8);
        String[] array = first_part_str.split(SPACE, 2);
        String type_str = array[0];
        String content_length_str = array[1];
        int content_length = Integer.parseInt(content_length_str);
        if (content_length != second_part_bytes.length) {
            String message = String.format("content length is not correct: %d(expected) and %d(actual)", content_length, second_part_bytes.length);
            throw new RuntimeException(message);
        }

        // 第三步，生成GitObject对象
        GitObject obj = new GitObject();
        obj.objectType = GitObjectType.fromString(type_str);
        obj.content = second_part_bytes;

        return obj;
    }

    public static String getId(String item) {
        String[] array = item.split(SPACE, 2);
        return array[1];
    }
}
