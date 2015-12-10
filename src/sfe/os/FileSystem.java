package sfe.os;

import java.util.LinkedList;


class Directory {

    String name, path;
    Folder parent;

    public Directory(String name, String path, Folder parent) {
        this.name = name;
        this.path = path;
        this.parent = parent;
    }

}

class Folder extends Directory {

    LinkedList<Directory> children;

    public Folder(String name, String path, Folder parent) {
        super(name, path, parent);
        children = new LinkedList<Directory>();
    }

    public LinkedList<Directory> getChildren() {
        return children;
    }
}

class File extends Directory {

    String permission;
    String extension;

    public File(String name, String path, Folder parent, String permission, String extension) {
        super(name + "." + extension, path, parent);
        this.permission = permission;
        this.extension = extension;
    }
}
 
public class FileSystem {
    
    private static final String COPY_PROCESS = "copy";
    private static final String CUT_PROCESS = "cut";
 
    private final Folder root;
    private Folder currentFolder;
    Directory toBePasted = null;
    String whichProcess = COPY_PROCESS;
 
    public FileSystem() {
        root = new Folder("root", "", null);
        currentFolder = root;
    }

    public Folder getCurrentFolder() {
        return currentFolder;
    }
 
    void newFolder(String name) {
        String path = this.currentFolder.path + "/" + name;
        Folder child = new Folder(name, path, this.currentFolder);
        this.currentFolder.children.add(child);
    }
 
    void newTxtFile(String name) {
        String path = this.currentFolder.path + "/" + name + ".txt";
        File child = new File(name, path, this.currentFolder, "read/write", "txt");
        this.currentFolder.children.add(child);
    }
 
    void newMp3File(String name) {
        String path = this.currentFolder.path + "/" + name + ".mp3";
        File child = new File(name, path, this.currentFolder, "read-only", "mp3");
        this.currentFolder.children.add(child);
    }
 
    void delete(Directory toBeDeleted) {
        for (int i = 0; i < this.currentFolder.children.size(); ++i) {
            if (toBeDeleted == this.currentFolder.children.get(i)) {
                this.currentFolder.children.remove(i);
                break;
            }
        }
    }
 
    void open(Directory toBeOpened) {
        for (int i = 0; i < this.currentFolder.children.size(); ++i) {
            if (toBeOpened == this.currentFolder.children.get(i)) {
                if (toBeOpened instanceof Folder) {
                    this.currentFolder = (Folder) toBeOpened;
                } else {
                    if (((File) toBeOpened).extension.equals("txt")) {
                        // Open text editor..
                        System.out.println("Opening txt file");
                    } else {
                        // Open mp3 player.
                        System.out.println("Opening mp3 file");
                    }
                }
            }
        }
    }
 
    void back() {
        this.currentFolder = this.currentFolder.parent;
    }
 
    void copy(Directory toBeCopied) {
        for (int i = 0; i < this.currentFolder.children.size(); ++i) {
            if (toBeCopied == this.currentFolder.children.get(i)) {
                this.whichProcess = COPY_PROCESS;
                this.toBePasted = this.currentFolder.children.get(i);
                break;
            }
        }
    }
 
    void cut(Directory toBeCutted) {
        for (int i = 0; i < this.currentFolder.children.size(); ++i) {
            if (toBeCutted == this.currentFolder.children.get(i)) {
                this.whichProcess = CUT_PROCESS;
                this.toBePasted = this.currentFolder.children.get(i);
                break;
            }
        }
    }
 
    void paste() {
        if (this.whichProcess.equals(CUT_PROCESS)) {
            for (int i = 0; i < this.toBePasted.parent.children.size(); i++) {
                if (toBePasted.parent.children.get(i) == toBePasted) {
                    toBePasted.parent.children.remove(i);
                    break;
                }
            }
        }
        toBePasted.path = this.currentFolder.path + "/" + toBePasted.name;
        this.currentFolder.children.add(toBePasted);
    }
 
    void printAll() {
        printAll(this.root, 0);
    }
 
    void printAll(Directory current, int cnt) {
        if (current == null) {
            return;
        }
        int t = cnt;
        while (t-- > 0) {
            System.out.print("-");
        }
        System.out.println(current.name);
        if (current instanceof File) {
            return;
        }
        for (int i = 0; i < ((Folder) current).children.size(); ++i) {
            printAll(((Folder) current).children.get(i), cnt + 2);
        }
    }
}
