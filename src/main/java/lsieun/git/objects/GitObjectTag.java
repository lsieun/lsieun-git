package lsieun.git.objects;

import lsieun.utils.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class GitObjectTag extends GitObject {
    public String object;
    public String type;
    public String tag;
    public String tagger;
    public List<String> messageList = new ArrayList<>();

    public GitObjectTag(GitObject gitObject) {
        this.objectType = gitObject.objectType;
        this.content = gitObject.content;
        if (objectType != GitObjectType.TAG) {
            throw new RuntimeException("illegal type: " + objectType.getValue());
        }
    }

    public String getObjectId() {
        return GitObject.getId(object);
    }

    @Override
    public byte[] toByteArray() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("%s\n", object);
        fm.format("%s\n", type);
        fm.format("%s\n", tag);
        fm.format("%s\n", tagger);
        fm.format("\n");
        fm.format("%s\n", messageList.get(0));

        String content_str = sb.toString();
        content = content_str.getBytes(StandardCharsets.UTF_8);

        String first_part_str = String.format("tag %d", content.length);
        byte[] first_part_bytes = first_part_str.getBytes(StandardCharsets.UTF_8);
        byte[] second_part_bytes = new byte[]{0};
        return ByteUtils.concatenate(first_part_bytes, second_part_bytes, content);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("%s %d%n", objectType.getValue(), content.length);
        fm.format("%s%n", object);
        fm.format("%s%n", type);
        fm.format("%s%n", tag);
        fm.format("%s%n", tagger);
        fm.format("%n");

        for (String message : messageList) {
            fm.format("%s%n", message);
        }

        return sb.toString();
    }

    public static GitObjectTag fromByteArray(byte[] bytes) {
        GitObject gitObject = GitObject.fromByteArray(bytes);
        GitObjectTag tag = new GitObjectTag(gitObject);

        byte[] content = tag.content;

        String content_str = new String(content, StandardCharsets.UTF_8);
        String[] array = content_str.split(GitObject.NEW_LINE);
        for (String item : array) {
            if (item.equals(GitObject.EMPTY)) continue;

            if (item.startsWith("object")) {
                tag.object = item;
            }
            else if (item.startsWith("type")) {
                tag.type = item;
            }
            else if (item.startsWith("tagger")) {
                tag.tagger = item;
            }
            else if (item.startsWith("tag")) {
                tag.tag = item;
            }

            else {
                tag.messageList.add(item);
            }
        }
        return tag;
    }
}
