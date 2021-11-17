package run;

import lsieun.cst.Const;
import lsieun.git.index.GitIndex;
import lsieun.utils.FileUtils;
import lsieun.utils.PathManager;

import java.nio.charset.StandardCharsets;

public class GitIndexRun {
    public static void main(String[] args) {
        // 1. get filepath
        String filepath = PathManager.getIndexPath();
        System.out.println(filepath);

        // 2. read bytes
        byte[] bytes = FileUtils.readBytes(filepath);
        System.out.println(Const.DIVISION_LINE);

        // 3. parse git index
        GitIndex gitIndex = GitIndex.fromByteArray(bytes);
        System.out.println(gitIndex);

        // 4. output
        String content = gitIndex.toString();
        byte[] content_bytes = content.getBytes(StandardCharsets.UTF_8);
        String output_filepath = PathManager.getFilePath("git-index.txt");
        FileUtils.writeBytes(output_filepath, content_bytes);
        System.out.println("file:///" + output_filepath);
    }
}
