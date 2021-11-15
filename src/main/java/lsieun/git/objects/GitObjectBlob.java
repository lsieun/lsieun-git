package lsieun.git.objects;

public class GitObjectBlob extends GitObject {
    public GitObjectBlob(GitObject gitObject) {
        this.objectType = gitObject.objectType;
        this.content = gitObject.content;
        if (objectType != GitObjectType.BLOB) {
            throw new RuntimeException("illegal type: " + objectType.getValue());
        }
    }

    public static GitObjectBlob fromByteArray(byte[] bytes) {
        GitObject gitObject = GitObject.fromByteArray(bytes);
        return new GitObjectBlob(gitObject);
    }
}
