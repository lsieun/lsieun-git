package lsieun.git.index;

import lsieun.git.objects.GitObjectType;

public enum GitIndexExtensionType {
    TREE("TREE"),
    REUC("REUC"),
    LINK("link"),
    ;
    private final String value;

    GitIndexExtensionType(String value) {
        this.value = value;
    }

    public static GitIndexExtensionType fromString(String value) {
        for (GitIndexExtensionType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new RuntimeException("unsupported value: " + value);
    }
}
