package lsieun.git.index;

import lsieun.utils.ByteUtils;
import lsieun.utils.HashUtils;
import lsieun.utils.HexUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

public class GitIndex {
    // 12-byte header
    public String signature;
    public int version;
    public int entries_count;

    // A number of sorted index entries
    public final List<GitIndexEntry> entryList = new ArrayList<>();

    // Extensions
    public GitIndexExtension extension;

    // Hash checksum
    public String hash_checksum;

    public static GitIndex fromByteArray(byte[] bytes) {
        // 12-byte header
        byte[] signature_bytes = Arrays.copyOfRange(bytes, 0, 4);
        byte[] version_bytes = Arrays.copyOfRange(bytes, 4, 8);
        byte[] entry_count_bytes = Arrays.copyOfRange(bytes, 8, 12);
        String signature = new String(signature_bytes, StandardCharsets.UTF_8);
        int version = ByteUtils.toInt(version_bytes);
        int entry_count = ByteUtils.toInt(entry_count_bytes);

        // A number of sorted index entries
        List<GitIndexEntry> entryList = new ArrayList<>();
        int from = 12;
        for (int i = 0; i < entry_count; i++) {
            byte[] flags_bytes = Arrays.copyOfRange(bytes, from + 60, from + 62);
            int flags = ByteUtils.toInt(flags_bytes);
            int name_length = flags & 0xFFF;
            int to = from + 62 + name_length;
            int entry_bytes_count = to - from;

            int remainder = entry_bytes_count % 8;
            to = to + 8 - remainder;

            byte[] entry_bytes = Arrays.copyOfRange(bytes, from, to);
            GitIndexEntry entry = GitIndexEntry.fromByteArray(entry_bytes);

            entryList.add(entry);

            from = to;
        }

        // Extension
        byte[] extension_bytes = Arrays.copyOfRange(bytes, from, bytes.length - 20);
        GitIndexExtensionTree extension = GitIndexExtensionTree.fromByteArray(extension_bytes);

        // Hash Checksum
        byte[] previous_bytes = Arrays.copyOfRange(bytes, 0, bytes.length - 20);
        byte[] actual_checksum_bytes = HashUtils.sha1(previous_bytes);
        byte[] hash_checksum_bytes = Arrays.copyOfRange(bytes, bytes.length - 20, bytes.length);
        if (!Arrays.equals(actual_checksum_bytes, hash_checksum_bytes)) {
            throw new RuntimeException("hash checksum is not correct");
        }

        // construct git index
        GitIndex gitIndex = new GitIndex();
        gitIndex.signature = signature;
        gitIndex.version = version;
        gitIndex.entries_count = entry_count;
        gitIndex.entryList.addAll(entryList);
        if (extension.signature_type == GitIndexExtensionType.TREE) {
            gitIndex.extension = GitIndexExtensionTree.fromByteArray(extension_bytes);
        }
        else {
            gitIndex.extension = extension;
        }
        gitIndex.hash_checksum = HexUtils.toHex(hash_checksum_bytes);

        return gitIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("signature: %s%n", signature);
        fm.format("version: %d%n", version);
        fm.format("entries_count: %d%n", entries_count);
        for (GitIndexEntry entry : entryList) {
            fm.format("    %s%n", entry);
        }
        fm.format("%s%n", extension);
        fm.format("Hash Checksum: %s%n", hash_checksum);
        return sb.toString();
    }
}
