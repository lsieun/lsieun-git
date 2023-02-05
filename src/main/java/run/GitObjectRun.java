package run;

import lsieun.cst.Const;
import lsieun.git.objects.*;
import lsieun.utils.*;

import java.nio.charset.StandardCharsets;

public class GitObjectRun {
    public static void main(String[] args) {
        // 1. get filepath
        String filepath = PathManager.getObjectPath("3b18e512dba79e4c8300dd08aeb37f8e728b8dad");
        System.out.println(filepath);

        // 2. read bytes and inflate it
        byte[] original_bytes = FileUtils.readBytes(filepath);
        System.out.println(HexUtils.format(original_bytes, HexFormat.FORMAT_FF_SPACE_FF));
        byte[] inflated_bytes = ArchiveUtils.inflate(original_bytes);
        System.out.println(HexUtils.format(inflated_bytes, HexFormat.FORMAT_FF_SPACE_FF));
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
        System.out.println("file://localhost/" + output_filepath);
    }
}
