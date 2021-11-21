package lsieun.git.pack;

public class GitPackIndexEntry implements Comparable<GitPackIndexEntry> {
    public String sha1;
    public int crc;
    public int offset;

    @Override
    public int compareTo(GitPackIndexEntry another) {
        return Integer.compare(this.offset, another.offset);
    }
}
