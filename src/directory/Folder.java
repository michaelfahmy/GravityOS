package directory;

import java.util.LinkedList;

public class Folder extends Directory {

    public LinkedList<Directory> children;
    public Folder(String name, String path, Folder parent) {
        super(name, path, parent);
        children = new LinkedList<>();
    }
    public LinkedList<Directory> getChildren() {
        return children;
    }

}
