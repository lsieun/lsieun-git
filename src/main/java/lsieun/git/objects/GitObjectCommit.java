package lsieun.git.objects;

import lsieun.utils.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class GitObjectCommit extends GitObject {
    public String tree;
    // TODO: 这里应该是一个List类型
    public String parent;
    public String author;
    public String committer;
    public List<String> messageList = new ArrayList<>();

    public GitObjectCommit(GitObject gitObject) {
        this.objectType = gitObject.objectType;
        this.content = gitObject.content;
        if (objectType != GitObjectType.COMMIT) {
            throw new RuntimeException("illegal type: " + objectType.getValue());
        }
    }

    @Override
    public byte[] toByteArray() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("%s\n", tree);
        if (parent != null) {
            fm.format("%s\n", parent);
        }
        fm.format("%s\n", author);
        fm.format("%s\n", committer);
        fm.format("\n");
        fm.format("%s\n", messageList.get(0));

        int size = messageList.size();
        if (size > 1) {
            fm.format("\n");
            for (int i = 1; i < size; i++) {
                String message = messageList.get(i);
                fm.format("%s\n", message);
            }
        }
        String content_str = sb.toString();
        content = content_str.getBytes(StandardCharsets.UTF_8);

        String first_part_str = String.format("commit %d", content.length);
        byte[] first_part_bytes = first_part_str.getBytes(StandardCharsets.UTF_8);
        byte[] second_part_bytes = new byte[]{0};
        return ByteUtils.concatenate(first_part_bytes, second_part_bytes, content);
    }

    public String getTreeId() {
        return GitObject.getId(tree);
    }

    public String getParentId() {
        if (parent == null) return null;
        return GitObject.getId(parent);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("%s %d%n", objectType.getValue(), content.length);
        fm.format("%s%n", tree);
        fm.format("%s%n", parent != null ? parent : "parent null");
        fm.format("%s%n", author);
        fm.format("%s%n", committer);
        fm.format("%n");

        for (String message : messageList) {
            fm.format("%s%n", message);
        }

        return sb.toString();
    }



    public static GitObjectCommit fromByteArray(byte[] bytes) {
        GitObject gitObject = GitObject.fromByteArray(bytes);
        GitObjectCommit commit = new GitObjectCommit(gitObject);
        byte[] content = commit.content;

        String content_str = new String(content, StandardCharsets.UTF_8);
        String[] array = content_str.split(GitObject.NEW_LINE);
        for (String item : array) {
            if (item.equals(GitObject.EMPTY)) continue;

            if (item.startsWith("tree")) {
                commit.tree = item;
            }
            else if (item.startsWith("parent")) {
                commit.parent = item;
            }
            else if (item.startsWith("author")) {
                commit.author = item;
            }
            else if (item.startsWith("committer")) {
                commit.committer = item;
            }
            else {
                commit.messageList.add(item);
            }
        }
        return commit;
    }

}
