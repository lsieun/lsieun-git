package lsieun.git.pack;

public class GitPackEntry {
    public String sha1;
    public GitPackType type;
    public int data_length;
    public byte[] data_bytes;
}
