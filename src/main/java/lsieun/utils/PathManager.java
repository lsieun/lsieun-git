package lsieun.utils;

import java.io.File;

public class PathManager {
    public static String getFilePath(String relativePath) {
        String dir = FileUtils.class.getResource("/").getPath();
        String filepath = dir + relativePath;
        if (filepath.contains(":")) {
            return filepath.substring(1);
        }
        return filepath;
    }

    public static String getWorkingDirectory() {
        String dir_path = System.getProperty("user.dir");

        // check whether the dir_path exists
        File dirFile = new File(dir_path);
        if (!dirFile.exists()) {
            throw new RuntimeException("Not Exist: " + dir_path);
        }
        if (!dirFile.isDirectory()) {
            throw new RuntimeException("Not Directory: " + dir_path);
        }

        // check .git directory
        String git_dir = dir_path + File.separator + ".git";
        File gitDirFile = new File(git_dir);
        if (!gitDirFile.exists() || !gitDirFile.isDirectory()) {
            throw new RuntimeException("Not Working Directory: " + dir_path);
        }
        return dir_path;
    }

    public static String getGitDIR() {
        return getWorkingDirectory() + File.separator + ".git";
    }

    public static String getIndexPath() {
        return getGitDIR() + File.separator + "index";
    }

    public static String getObjectPath(String sha1) {
        return getGitDIR() + File.separator + "objects" + File.separator + sha1.substring(0, 2) + File.separator + sha1.substring(2);
    }

    public static String getPackIndex(String sha1) {
        String filename = String.format("pack-%s.idx", sha1);
        return getPack(filename);
    }

    public static String getPackData(String sha1) {
        String filename = String.format("pack-%s.pack", sha1);
        return getPack(filename);
    }

    public static String getPack(String filename) {
        return getGitDIR() + File.separator + "objects" + File.separator + "pack" + File.separator + filename;
    }
}
