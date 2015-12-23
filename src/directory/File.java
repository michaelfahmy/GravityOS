package directory;

public class File extends Directory {

    String permission;
    String extension;
    public File(String name, String extension, String path, Folder parent, String permission) {
        super(name + "." + extension, path, parent);
        this.permission = permission;
        this.extension = extension;
    }
    public String getExtension() { return this.extension; }

    public String getPermission() {
        return permission;
    }
}
