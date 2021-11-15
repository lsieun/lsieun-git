package run;

import lsieun.cst.Const;
import lsieun.git.objects.*;
import lsieun.utils.ArchiveUtils;
import lsieun.utils.FileUtils;
import lsieun.utils.HexUtils;
import lsieun.utils.PathManager;

import java.util.Arrays;

public class GitObjectRun {
    public static void main(String[] args) {
        // 1. get filepath
        String filepath = PathManager.getPath("blob-3b18e512dba79e4c8300dd08aeb37f8e728b8dad");
        System.out.println(filepath);

        // 2. read bytes and inflate it
        byte[] original_bytes = FileUtils.readBytes(filepath);
        byte[] inflated_bytes = ArchiveUtils.inflate(original_bytes);
        byte[] deflated_bytes = ArchiveUtils.deflate(inflated_bytes);
        System.out.println(HexUtils.toHex(inflated_bytes));
        System.out.println(HexUtils.toHex(deflated_bytes));
        System.out.println(Arrays.equals(original_bytes, deflated_bytes));
        System.out.println(Const.DIVISION_LINE);

        // 3. parse git object
        GitObject gitObject = GitObject.fromByteArray(inflated_bytes);
        GitObjectType objectType = gitObject.objectType;
        switch (objectType) {
            case BLOB:
                parseBlob(inflated_bytes);
                break;
            case TREE:
                parseTree(inflated_bytes);
                break;
            case COMMIT:
                parseCommit(inflated_bytes);
                break;
            case TAG:
                parseTag(inflated_bytes);
                break;
            default:
                throw new RuntimeException("unsupported object type: " + objectType);
        }
    }

    public static void parseBlob(byte[] bytes) {
        GitObjectBlob blob = GitObjectBlob.fromByteArray(bytes);
        System.out.println(blob.getSHA1());
        System.out.println(blob);
    }

    public static void parseTree(byte[] bytes) {
        GitObjectTree tree = GitObjectTree.fromByteArray(bytes);
        System.out.println(tree.getSHA1());
        System.out.println(tree);

        byte[] bytes2 = tree.toByteArray();
        System.out.println(Arrays.equals(bytes, bytes2));
    }

    public static void parseCommit(byte[] bytes) {
        GitObjectCommit commit = GitObjectCommit.fromByteArray(bytes);
        System.out.println(commit.getSHA1());
        System.out.println(commit);
        System.out.println(commit.getTreeId());
        System.out.println(commit.getParentId());
    }

    public static void parseTag(byte[] bytes) {
        GitObjectTag tag = GitObjectTag.fromByteArray(bytes);
        System.out.println(tag.getSHA1());
        System.out.println(tag);
        System.out.println(tag.getObjectId());
    }
}
