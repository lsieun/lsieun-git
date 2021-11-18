package lsieun.git.objects;

import lsieun.utils.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

public class GitObjectTree extends GitObject {
    public final List<GitObjectTreeEntry> entryList = new ArrayList<>();

    public GitObjectTree(GitObject gitObject) {
        this.objectType = gitObject.objectType;
        this.content = gitObject.content;
        if (objectType != GitObjectType.TREE) {
            throw new RuntimeException("illegal type: " + objectType.getValue());
        }
    }

    public static GitObjectTree fromByteArray(byte[] bytes) {
        GitObject gitObject = GitObject.fromByteArray(bytes);
        return fromParent(gitObject);
    }

    public static GitObjectTree fromParent(GitObject gitObject) {
        GitObjectTree tree = new GitObjectTree(gitObject);
        byte[] content = tree.content;

        int content_length = content.length;
        for (int from = 0; from < content_length; ) {
            int index = ByteUtils.findFistNUL(content, from);
            int to = index + 21;

            byte[] entry_bytes = Arrays.copyOfRange(content, from, to);
            GitObjectTreeEntry item = GitObjectTreeEntry.fromByteArray(entry_bytes);
            tree.entryList.add(item);

            from = to;
        }
        return tree;
    }

    public byte[] toByteArray() {
        byte[] entryListByteArray = new byte[0];
        for (GitObjectTreeEntry entry : entryList) {
            byte[] entryByteArray = entry.toByteArray();
            entryListByteArray = ByteUtils.concatenate(entryListByteArray, entryByteArray);
        }

        String first_part_str = String.format("tree %d", entryListByteArray.length);
        byte[] first_part_bytes = first_part_str.getBytes(StandardCharsets.UTF_8);
        byte[] second_part_bytes = new byte[]{0};
        return ByteUtils.concatenate(first_part_bytes, second_part_bytes, entryListByteArray);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("tree %d%n", content.length);
        for (GitObjectTreeEntry entry : entryList) {
            fm.format("%s %s %s%n", entry.mode, entry.name, entry.sha1);
        }
        return sb.toString();
    }
}
