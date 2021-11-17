package run;

import lsieun.git.index.GitIndex;
import lsieun.utils.FileUtils;
import lsieun.utils.PathManager;

public class GitIndexRun {
    public static void main(String[] args) {
        String filepath = PathManager.getPath("tree-index");
        System.out.println(filepath);
        byte[] bytes = FileUtils.readBytes(filepath);
        GitIndex gitIndex = GitIndex.fromByteArray(bytes);
        System.out.println(gitIndex);
    }
}
