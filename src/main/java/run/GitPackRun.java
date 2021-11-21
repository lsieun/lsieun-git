package run;

import lsieun.cst.Const;
import lsieun.git.pack.GitPack;
import lsieun.git.pack.GitPackIndex;
import lsieun.utils.FileUtils;
import lsieun.utils.PathManager;

public class GitPackRun {
    public static void main(String[] args) {
        // 1. get filepath
        String pack_sha1 = "54eb3babc727c009c76d796f153e913a5e24d714";
        String pack_index_filepath = PathManager.getPackIndex(pack_sha1);
        String pack_data_filepath = PathManager.getPackData(pack_sha1);
        System.out.println(pack_index_filepath);
        System.out.println(pack_data_filepath);
        System.out.println(Const.DIVISION_LINE);

        // 2. read bytes
        byte[] pack_index_bytes = FileUtils.readBytes(pack_index_filepath);
        byte[] pack_data_bytes = FileUtils.readBytes(pack_data_filepath);

        // 3. parse git pack index and data
        GitPackIndex gitPackIndex = GitPackIndex.fromByteArray(pack_index_bytes);
        System.out.println(gitPackIndex);
        GitPack gitPack = GitPack.fromByteArray(pack_data_bytes, gitPackIndex);
        System.out.println(gitPack);
    }
}
