package lsieun.git.index;

import lsieun.utils.ByteUtils;
import lsieun.utils.HexUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GitIndexEntry {
    // 40 bytes
    public int ctime_seconds;
    public int ctime_nanosecond;
    public int mtime_seconds;
    public int mtime_nanosecond;
    public int dev;
    public int ino;
    public int mode;
    public int uid;
    public int gid;
    public int file_size;

    // 20 bytes
    public String sha1;

    // file name
    public int flags;
    public String file_name;

    public static GitIndexEntry fromByteArray(byte[] bytes) {
        byte[] ctime_seconds_bytes = Arrays.copyOfRange(bytes, 0, 4);
        byte[] ctime_nanosecond_bytes = Arrays.copyOfRange(bytes, 4, 8);
        byte[] mtime_seconds_bytes = Arrays.copyOfRange(bytes, 8, 12);
        byte[] mtime_nanosecond_bytes = Arrays.copyOfRange(bytes, 12, 16);
        byte[] dev_bytes = Arrays.copyOfRange(bytes, 16, 20);
        byte[] ino_bytes = Arrays.copyOfRange(bytes, 20, 24);
        byte[] mode_bytes = Arrays.copyOfRange(bytes, 24, 28);
        byte[] uid_bytes = Arrays.copyOfRange(bytes, 28, 32);
        byte[] gid_bytes = Arrays.copyOfRange(bytes, 32, 36);
        byte[] file_size_bytes = Arrays.copyOfRange(bytes, 36, 40);

        byte[] sha1_bytes = Arrays.copyOfRange(bytes, 40, 60);
        String sha1 = HexUtils.toHex(sha1_bytes).toLowerCase();

        byte[] flags_bytes = Arrays.copyOfRange(bytes, 60, 62);
        int flags = ByteUtils.toInt(flags_bytes);
        int file_name_length = flags & 0xFFF;

        byte[] file_name_bytes = Arrays.copyOfRange(bytes, 62, 62 + file_name_length);
        String file_name = new String(file_name_bytes, StandardCharsets.UTF_8);

        GitIndexEntry entry = new GitIndexEntry();
        entry.ctime_seconds = ByteUtils.toInt(ctime_seconds_bytes);
        entry.ctime_nanosecond = ByteUtils.toInt(ctime_nanosecond_bytes);
        entry.mtime_seconds = ByteUtils.toInt(mtime_seconds_bytes);
        entry.mtime_nanosecond = ByteUtils.toInt(mtime_nanosecond_bytes);
        entry.dev = ByteUtils.toInt(dev_bytes);
        entry.ino = ByteUtils.toInt(ino_bytes);
        entry.mode = ByteUtils.toInt(mode_bytes);
        entry.uid = ByteUtils.toInt(uid_bytes);
        entry.gid = ByteUtils.toInt(gid_bytes);
        entry.file_size = ByteUtils.toInt(file_size_bytes);
        entry.sha1 = sha1;
        entry.flags = flags;
        entry.file_name = file_name;
        return entry;
    }

    @Override
    public String toString() {
        String mode_str = getModeStr();
        return String.format("%s %s %s", mode_str, sha1, file_name);
    }

    public String getModeStr() {
        int object_type_val = (mode >> 12) & 0xF;
        String object_type;
        switch (object_type_val) {
            case 0b1000:
                object_type = "100";
                break;
            case 0b1010:
                object_type = "101";
                break;
            case 0b1110:
                object_type = "111";
                break;
            default:
                throw new RuntimeException("unexpected value: " + object_type_val);
        }

        int read = (mode >> 6) & 0x07;
        int write = (mode >> 3) & 0x07;
        int exec = mode & 0x07;
        return String.format("%s%d%d%d", object_type, read, write, exec);
    }
}
