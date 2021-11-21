package lsieun.git.pack;

import lsieun.utils.ArchiveUtils;
import lsieun.utils.ByteUtils;
import lsieun.utils.HashUtils;
import lsieun.utils.HexUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

public class GitPack {
    // 12-byte header
    public String signature;
    public int version;
    public int entries_count;

    public final List<GitPackEntry> entryList = new ArrayList<>();
    public String checksum;

    public static GitPack fromByteArray(byte[] bytes, GitPackIndex gitPackIndex) {
        int length = bytes.length;
//        System.out.println(HexUtils.toHex(bytes));

        // 12-byte header
        byte[] signature_bytes = Arrays.copyOfRange(bytes, 0, 4);
        byte[] version_bytes = Arrays.copyOfRange(bytes, 4, 8);
        byte[] entries_count_bytes = Arrays.copyOfRange(bytes, 8, 12);
        String signature = new String(signature_bytes, StandardCharsets.UTF_8);
        int version = ByteUtils.toInt(version_bytes);
        int entries_count = ByteUtils.toInt(entries_count_bytes);

        if (entries_count != gitPackIndex.size) {
            String message = String.format("entry counts do not equal: %d(actual), %d(expected)", entries_count, gitPackIndex.size);
            throw new RuntimeException(message);
        }

        // entries
        List<GitPackEntry> entryList = new ArrayList<>();
        int byteIndex;


        for (int i = 0; i < entries_count; i++) {
            byteIndex = gitPackIndex.entryList.get(i).offset;
            int n = 0;
            while (true) {
                byte b = bytes[byteIndex + n];
                n++;
                if (b > 0) {
                    break;
                }
            }

            byte[] n_bytes = Arrays.copyOfRange(bytes, byteIndex, byteIndex + n);

            byte firstByte = n_bytes[0];
            int type = (firstByte >> 4) & 0b0111;

            int data_length = 0;
            for (int j = 0; j < n; j++) {
                if (j == 0) {
                    data_length = n_bytes[j] & 0x0F;
                }
                else {
                    int left_shift = ((j - 1) * 7) + 4;
                    data_length = ((n_bytes[j] & 0x7F) << left_shift) | data_length;
                }
            }

            byteIndex += n;

            int stop;
            if (i != entries_count - 1) {
                stop = gitPackIndex.entryList.get(i + 1).offset;
            }
            else {
                stop = length - 20;
            }


            byte[] compressed_bytes = Arrays.copyOfRange(bytes, byteIndex, stop);
            byte[] inflated_bytes = ArchiveUtils.inflate(compressed_bytes);
            if (data_length != inflated_bytes.length) {
                throw new RuntimeException("length is not correct");
            }


            GitPackEntry entry = new GitPackEntry();
            entry.sha1 = gitPackIndex.entryList.get(i).sha1;
            entry.type = GitPackType.fromInt(type);
            entry.data_length = data_length;
            entry.data_bytes = inflated_bytes;


            entryList.add(entry);
        }

        byte[] previous_bytes = Arrays.copyOfRange(bytes, 0, length - 20);
        byte[] sha1_bytes = HashUtils.sha1(previous_bytes);
        String calculated_sha1 = HexUtils.toHex(sha1_bytes);

        byte[] checksum_bytes = Arrays.copyOfRange(bytes, length - 20, length);
        String checksum = HexUtils.toHex(checksum_bytes);
        if (!calculated_sha1.equals(checksum)) {
            String message = String.format("checksum is not correct: %s(caculated), %s(expected)", calculated_sha1, checksum);
            throw new RuntimeException(message);
        }


        GitPack gitPack = new GitPack();
        gitPack.signature = signature;
        gitPack.version = version;
        gitPack.entries_count = entries_count;
        gitPack.entryList.addAll(entryList);
        gitPack.checksum = checksum;
        return gitPack;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("GitPack {%n");
        fm.format("    signature: %s%n", signature);
        fm.format("    version: %d%n", version);
        fm.format("    entries_count: %d%n", entries_count);
        for (GitPackEntry entry : entryList) {
            fm.format("    %s, type: %s, length: %d%n", entry.sha1, entry.type, entry.data_length);
        }
        fm.format("    checksum: %s%n", checksum);
        fm.format("}%n%n");
        return sb.toString();
    }
}
