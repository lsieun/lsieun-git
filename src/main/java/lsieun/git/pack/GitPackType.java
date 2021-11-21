package lsieun.git.pack;

public enum GitPackType {
    OBJ_COMMIT(1),
    OBJ_TREE(2),
    OBJ_BLOB(3),
    OBJ_TAG(4),
    OBJ_OFS_DELTA(6),
    OBJ_REF_DELTA(7);
    public int value;

    GitPackType(int val) {
        this.value = val;
    }

    public static GitPackType fromInt(int value) {
        for (GitPackType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new RuntimeException("unsupported value: " + value);
    }
}
