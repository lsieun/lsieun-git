package lsieun.git.objects;

import lsieun.utils.ByteUtils;
import lsieun.utils.HexUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GitObjectTreeEntry {
    public String mode;
    public String name;
    public String sha1;

    public static GitObjectTreeEntry fromByteArray(byte[] bytes) {
        int index = ByteUtils.findFistNUL(bytes);
        byte[] first_part_bytes = Arrays.copyOfRange(bytes, 0, index);
        byte[] second_part_bytes = Arrays.copyOfRange(bytes, index + 1, index + 21);

        String first_part_str = new String(first_part_bytes, StandardCharsets.UTF_8);
        String second_part_str = HexUtils.toHex(second_part_bytes).toLowerCase();
        String[] array = first_part_str.split(GitObject.SPACE, 2);

        GitObjectTreeEntry item = new GitObjectTreeEntry();
        item.mode = array[0];
        item.name = array[1];
        item.sha1 = second_part_str;
        return item;
    }

    public byte[] toByteArray() {
        String first_part_str = String.format("%s %s", mode, name);
        byte[] first_part_bytes = first_part_str.getBytes(StandardCharsets.UTF_8);
        byte[] second_part_bytes = new byte[]{0};
        byte[] third_part_bytes = HexUtils.parse(sha1);
        return ByteUtils.concatenate(first_part_bytes, second_part_bytes, third_part_bytes);
    }
}
