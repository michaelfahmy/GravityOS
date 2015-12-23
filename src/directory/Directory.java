package directory;

import java.io.Serializable;

public class Directory implements Serializable {

    private String name, path;
    public String realPath;
    private Folder parent;
    private boolean isHidden = false;
    public Directory(String name, String path, Folder parent) {
        this.name = name;
        this.path = path;
        this.parent = parent;
    }
    public boolean isHidden() {
        return isHidden;
    }
    public void setHidden() { isHidden = true; }
    public void setRealPath(String realPath) { this.realPath = realPath; }
    public String getRealPath() { return this.realPath; }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) { this.path = path; }
    public Folder getParent() { return this.parent; }
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
}
