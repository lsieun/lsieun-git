package lsieun.utils;

public class PathManager {
    public static String getPath(String relativePath) {
        String dir = PathManager.class.getResource("/").getPath();
        return dir + relativePath;
    }
}
