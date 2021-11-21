package lsieun.git.pack;

import lsieun.cst.Const;
import lsieun.utils.ByteUtils;
import lsieun.utils.HashUtils;
import lsieun.utils.HexUtils;

import java.util.*;

public class GitPackIndex {
    public static final byte[] MAGIC_NUMBER = new byte[]{
            (byte) 255, 116, 79, 99
    };

    public int version;
    public int size;
    public final List<GitPackIndexEntry> entryList = new ArrayList<>();
    public String pack_data_checksum;
    public String pack_index_checksum;

    public static GitPackIndex fromByteArray(byte[] bytes) {
        int length = bytes.length;

        byte[] magic_number_bytes = Arrays.copyOfRange(bytes, 0, 4);
        if (!Arrays.equals(MAGIC_NUMBER, magic_number_bytes)) {
            throw new RuntimeException("magic number is not correct: " + HexUtils.toHex(magic_number_bytes));
        }
        byte[] version_bytes = Arrays.copyOfRange(bytes, 4, 8);
        int version = ByteUtils.toInt(version_bytes);

        int[] fanout_table = new int[256];
        int byteIndex = 8;
        int count_size = 4;
        for (int i = 0; i < 256; i++) {
            byte[] count_bytes = Arrays.copyOfRange(bytes, byteIndex, byteIndex + count_size);
            int count = ByteUtils.toInt(count_bytes);
            fanout_table[i] = count;
            byteIndex += count_size;
        }

        int num = fanout_table[255];
        String[] sha1_array = new String[num];

        int sha1_size = Const.SHA1_BYTE_SIZE;
        for (int i = 0; i < num; i++) {
            byte[] sha1_bytes = Arrays.copyOfRange(bytes, byteIndex, byteIndex + sha1_size);
            String sha1 = HexUtils.toHex(sha1_bytes).toLowerCase();
            sha1_array[i] = sha1;
            byteIndex += sha1_size;
        }

        int[] crc_array = new int[num];
        int crc_size = 4;
        for (int i = 0; i < num; i++) {
            byte[] crc_bytes = Arrays.copyOfRange(bytes, byteIndex, byteIndex + crc_size);
            int crc = ByteUtils.toInt(crc_bytes);
            crc_array[i] = crc;
            byteIndex += crc_size;
        }

        int[] offset_array = new int[num];
        int offset_size = 4;
        for (int i = 0; i < num; i++) {
            byte[] offset_bytes = Arrays.copyOfRange(bytes, byteIndex, byteIndex + offset_size);
            int offset = ByteUtils.toInt(offset_bytes);
            offset_array[i] = offset;
            byteIndex += offset_size;
        }

        List<GitPackIndexEntry> entryList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            GitPackIndexEntry entry = new GitPackIndexEntry();
            entry.sha1 = sha1_array[i];
            entry.crc = crc_array[i];
            entry.offset = offset_array[i];
            entryList.add(entry);
        }
        Collections.sort(entryList);

        byte[] remaining_bytes = Arrays.copyOfRange(bytes, byteIndex, length - 40);
        int remaining_length = remaining_bytes.length;
        if (remaining_length != 0) {
            System.out.println("remaining bytes: " + remaining_bytes.length);
            System.out.println(HexUtils.toHex(remaining_bytes));
        }

        byte[] pack_data_checksum_bytes = Arrays.copyOfRange(bytes, length - 40, length - 20);
        String pack_data_checksum = HexUtils.toHex(pack_data_checksum_bytes);

        byte[] previous_bytes = Arrays.copyOfRange(bytes, 0, length - 20);
        byte[] sha1_bytes = HashUtils.sha1(previous_bytes);
        String calculated_sha1 = HexUtils.toHex(sha1_bytes);

        byte[] pack_index_checksum_bytes = Arrays.copyOfRange(bytes, length - 20, length);
        String pack_index_checksum = HexUtils.toHex(pack_index_checksum_bytes);
        if (!calculated_sha1.equals(pack_index_checksum)) {
            String message = String.format("checksum is not correct: %s(caculated), %s(expected)", calculated_sha1, pack_index_checksum);
            throw new RuntimeException(message);
        }

        GitPackIndex gitPackIndex = new GitPackIndex();
        gitPackIndex.version = version;
        gitPackIndex.size = num;
        gitPackIndex.entryList.addAll(entryList);
        gitPackIndex.pack_data_checksum = pack_data_checksum;
        gitPackIndex.pack_index_checksum = pack_index_checksum;
        return gitPackIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("GitPackIndex {%n");
        fm.format("    version: %d%n", version);
        fm.format("    size: %d%n", size);
        for (GitPackIndexEntry entry : entryList) {
            fm.format("    %s, offset: %d%n", entry.sha1, entry.offset);
        }
        fm.format("    .pack checksum: %s%n", pack_data_checksum);
        fm.format("    .idx  checksum: %s%n", pack_index_checksum);
        fm.format("}%n");
        return sb.toString();
    }
}
