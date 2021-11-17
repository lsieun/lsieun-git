package lsieun.git.index;

import lsieun.utils.ByteUtils;
import lsieun.utils.HexUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

public class GitIndexExtensionResolveUndo extends GitIndexExtension {
    public final List<GitIndexExtensionResolveUndoEntry> entryList = new ArrayList<>();

    public GitIndexExtensionResolveUndo(GitIndexExtension parent) {
        this.signature_type = parent.signature_type;
        this.extension_data_bytes = parent.extension_data_bytes;
    }

    public static GitIndexExtensionResolveUndo fromByteArray(byte[] bytes) {
        GitIndexExtension parent = GitIndexExtension.fromByteArray(bytes);
        return fromParent(parent);
    }

    public static GitIndexExtensionResolveUndo fromParent(GitIndexExtension parent) {
        GitIndexExtensionResolveUndo extension = new GitIndexExtensionResolveUndo(parent);
        byte[] extension_data_bytes = extension.extension_data_bytes;
        int extension_data_length = extension_data_bytes.length;

        for (int from = 0;from < extension_data_length;) {
            // entry's path
            int path_name_stop = ByteUtils.findFistNUL(extension_data_bytes);
            byte[] path_name_bytes = Arrays.copyOfRange(extension_data_bytes, 0, path_name_stop);
            String path_name = new String(path_name_bytes, StandardCharsets.UTF_8);

            // entry's mode
            int entry_mode_start = path_name_stop + 1;
            int object_name_count = 0;
            List<String> entry_mode_list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                int entry_mode_stop = ByteUtils.findFistNUL(extension_data_bytes, entry_mode_start);
                byte[] entry_mode_bytes = Arrays.copyOfRange(extension_data_bytes, entry_mode_start, entry_mode_stop);
                String entry_mode = new String(entry_mode_bytes, StandardCharsets.UTF_8);
                if (!entry_mode.equals("0")) {
                    object_name_count += 1;
                }
                entry_mode_list.add(entry_mode);

                entry_mode_start = entry_mode_stop + 1;
            }

            // entry's object name
            List<String> object_name_list = new ArrayList<>();
            int object_name_index = entry_mode_start;
            for (int i = 0; i < object_name_count; i++) {
                byte[] object_name_bytes = Arrays.copyOfRange(extension_data_bytes, object_name_index, object_name_index + 20);
                String object_name = HexUtils.toHex(object_name_bytes).toLowerCase();
                object_name_list.add(object_name);
                object_name_index += 20;
            }

            // construct entry
            GitIndexExtensionResolveUndoEntry entry = new GitIndexExtensionResolveUndoEntry();
            entry.path_name = path_name;
            entry.entry_mode_list.addAll(entry_mode_list);
            entry.object_name_list.addAll(object_name_list);
            extension.entryList.add(entry);

            from = object_name_index;
        }

        return extension;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("Extension {%n");
        fm.format("    %s%n", signature_type);
        for (GitIndexExtensionResolveUndoEntry entry : entryList) {
            fm.format("    name = %s%n", entry.path_name);
        }
        fm.format("}");
        return sb.toString();
    }
}
