package lsieun.git.index;

import lsieun.utils.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GitIndexExtension {
    public GitIndexExtensionType signature_type;
    public byte[] extension_data_bytes;

    public static GitIndexExtension fromByteArray(byte[] bytes) {
        // signature
        byte[] signature_bytes = Arrays.copyOfRange(bytes, 0, 4);
        String signature = new String(signature_bytes, StandardCharsets.UTF_8);

        // extension size
        byte[] extension_size_bytes = Arrays.copyOfRange(bytes, 4, 8);
        int extension_size = ByteUtils.toInt(extension_size_bytes);

        // extension data
        byte[] extension_data_bytes = Arrays.copyOfRange(bytes, 8, bytes.length);
        if (extension_size != extension_data_bytes.length) {
            throw new RuntimeException("There is something wrong!");
        }

        // construct an extension
        GitIndexExtension extension = new GitIndexExtension();
        extension.signature_type = GitIndexExtensionType.fromString(signature);
        extension.extension_data_bytes = extension_data_bytes;


        return extension;
    }
}
