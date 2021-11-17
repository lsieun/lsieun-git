package run;

import lsieun.cst.Const;
import lsieun.git.objects.*;
import lsieun.utils.ArchiveUtils;
import lsieun.utils.FileUtils;
import lsieun.utils.PathManager;

public class GitObjectRun {
    public static void main(String[] args) {
        // 1. get filepath
        String filepath = PathManager.getObjectPath("0adbcc19863755f9d2a1e4c22714349e215affcd");
        System.out.println(filepath);

        // 2. read bytes and inflate it
        byte[] original_bytes = FileUtils.readBytes(filepath);
        byte[] inflated_bytes = ArchiveUtils.inflate(original_bytes);
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
    }

    public static void parseCommit(byte[] bytes) {
        GitObjectCommit commit = GitObjectCommit.fromByteArray(bytes);
        System.out.println(commit.getSHA1());
        System.out.println(commit);
    }

    public static void parseTag(byte[] bytes) {
        GitObjectTag tag = GitObjectTag.fromByteArray(bytes);
        System.out.println(tag.getSHA1());
        System.out.println(tag);
    }
}
