package lsieun.git.index;

import lsieun.utils.ByteUtils;
import lsieun.utils.HexUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

public class GitIndexExtensionCacheTree extends GitIndexExtension {
    public final List<GitIndexExtensionCacheTreeEntry> entryList = new ArrayList<>();

    public GitIndexExtensionCacheTree(GitIndexExtension parent) {
        this.signature_type = parent.signature_type;
        this.extension_data_bytes = parent.extension_data_bytes;
    }

    public static GitIndexExtensionCacheTree fromByteArray(byte[] bytes) {
        GitIndexExtension parent = GitIndexExtension.fromByteArray(bytes);
        return fromParent(parent);
    }

    public static GitIndexExtensionCacheTree fromParent(GitIndexExtension parent) {
        GitIndexExtensionCacheTree extension = new GitIndexExtensionCacheTree(parent);

        byte[] extension_data_bytes = extension.extension_data_bytes;
        int length = extension_data_bytes.length;
        for (int start = 0; start < length; ) {
            // name
            int nul_index = ByteUtils.findFistNUL(extension_data_bytes, start);
            byte[] name_bytes = Arrays.copyOfRange(extension_data_bytes, start, nul_index);
            String name = new String(name_bytes, StandardCharsets.UTF_8);

            // entry_count
            int space_index = ByteUtils.findFist(extension_data_bytes, nul_index + 1, 32);
            byte[] entry_count_bytes = Arrays.copyOfRange(extension_data_bytes, nul_index + 1, space_index);
            String entry_count_str = new String(entry_count_bytes, StandardCharsets.UTF_8);
            int entry_count = Integer.parseInt(entry_count_str);

            // subtrees_num
            int new_line_index = ByteUtils.findFist(extension_data_bytes, space_index + 1, 10);
            byte[] subtrees_num_bytes = Arrays.copyOfRange(extension_data_bytes, space_index + 1, new_line_index);
            String subtrees_num_str = new String(subtrees_num_bytes, StandardCharsets.UTF_8);
            int subtrees_num = Integer.parseInt(subtrees_num_str);

            // object name
            int object_name_index = new_line_index + 1;
            String object_name = null;
            if (entry_count > -1) {
                byte[] object_name_bytes = Arrays.copyOfRange(extension_data_bytes, object_name_index, object_name_index + 20);
                object_name = HexUtils.toHex(object_name_bytes).toLowerCase();
                object_name_index += 20;
            }

            // construct an entry
            GitIndexExtensionCacheTreeEntry entry = new GitIndexExtensionCacheTreeEntry();
            entry.path_name = name;
            entry.entry_count = entry_count;
            entry.subtrees_num = subtrees_num;
            entry.object_name = object_name;
            extension.entryList.add(entry);

            // update 'start'
            start = object_name_index;
        }

        return extension;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("Extension {%n");
        fm.format("    %s%n", signature_type);
        for (GitIndexExtensionCacheTreeEntry entry : entryList) {
            fm.format("    name = %s, entry_count = %d, subtrees_num = %d, object_name = %s%n",
                    entry.path_name, entry.entry_count, entry.subtrees_num, entry.object_name);
        }
        fm.format("}");
        return sb.toString();
    }
}
