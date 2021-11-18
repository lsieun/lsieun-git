package run;

import lsieun.cst.Const;
import lsieun.git.objects.*;
import lsieun.utils.ArchiveUtils;
import lsieun.utils.FileUtils;
import lsieun.utils.PathManager;

import java.nio.charset.StandardCharsets;

public class GitObjectRun {
    public static void main(String[] args) {
        // 1. get filepath
        String filepath = PathManager.getObjectPath("fe850b5dbe6b218d30ab64310143c522fe7cbd68");
        System.out.println(filepath);

        // 2. read bytes and inflate it
        byte[] original_bytes = FileUtils.readBytes(filepath);
        byte[] inflated_bytes = ArchiveUtils.inflate(original_bytes);
        System.out.println(Const.DIVISION_LINE);

        // 3. parse git object
        GitObject gitObject = GitObject.fromByteArray(inflated_bytes);
        System.out.println(gitObject.getSHA1());
        System.out.println(gitObject);

        // 4. output
        String content = gitObject.toString();
        byte[] content_bytes = content.getBytes(StandardCharsets.UTF_8);
        String output_filepath = PathManager.getFilePath("git-object.txt");
        FileUtils.writeBytes(output_filepath, content_bytes);
        System.out.println("file:///" + output_filepath);
    }
}
