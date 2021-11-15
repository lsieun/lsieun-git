package lsieun.git.objects;

public enum GitObjectType {
    BLOB("blob"),
    TREE("tree"),
    COMMIT("commit"),
    TAG("tag");

    private final String value;

    GitObjectType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GitObjectType fromString(String value) {
        for (GitObjectType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new RuntimeException("unsupported value: " + value);
    }
}
