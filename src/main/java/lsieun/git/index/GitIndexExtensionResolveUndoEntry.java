package lsieun.git.index;

import java.util.ArrayList;
import java.util.List;

public class GitIndexExtensionResolveUndoEntry {
    public String path_name;
    public final List<String> entry_mode_list = new ArrayList<>();
    public final List<String> object_name_list = new ArrayList<>();
}
