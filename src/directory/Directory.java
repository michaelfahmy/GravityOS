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
    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }
    public String getRealPath() { return this.realPath; }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) { this.path = path; }
    public Folder getParent() { return this.parent; }
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    double getSize(Directory X) {
        double sz = 0;
        java.io.File f = null;
        if(X instanceof File) {
            String pth = X.getRealPath();
            if (pth != null)  {
                pth = pth.replaceAll("%20", " ").substring(5);
                f = new java.io.File(pth);
                sz = ((1.0 * f.length()) / Math.pow(1024, 2.0));
                return X.getRealPath() == null ? 0 : ((double)((int)(sz * 100)))/100.00;
            }
            return 0;
        }
        Folder cur = (Folder) X;

        for(Directory c: cur.getChildren()) {
            sz += getSize(c);
        }
        return ((double)((int)(sz * 100)))/100.00;
    }
    public double getSize() {
        return getSize(this);
    }
}
