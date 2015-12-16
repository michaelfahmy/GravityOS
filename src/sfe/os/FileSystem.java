package sfe.os;

import apps.ImageViewer;
import apps.JavaFXMediaPlayer;
import apps.WebBrowser;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.LinkedList;
import java.util.Objects;


class Directory implements Serializable {

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
        children = new LinkedList<>();
    }

    public LinkedList<Directory> getChildren() {
        return children;
    }
}

class File extends Directory {

    String permission;
    String extension;

    public File(String name, String extension, String path, Folder parent, String permission) {
        super(name + "." + extension, path, parent);
        this.permission = permission;
        this.extension = extension;
    }
}
 
public class FileSystem {
    
    private static final String COPY_PROCESS = "copy";
    private static final String CUT_PROCESS = "cut";
    private Folder root;
    private Folder currentFolder;
    private Directory selected = null;
    Directory toBePasted = null;
    String whichProcess = COPY_PROCESS;

    public FileSystem() {
        root = new Folder("root", "", null);
        currentFolder = root;
        this.retrieve();
    }

    public void select(Directory selected) {
        this.selected = selected;
    }

    public Directory getSelected() {
        return selected;
    }

    public Folder getCurrentFolder() {
        return currentFolder;
    }

    void newFolder(String name) {
        String path = this.currentFolder.path + "/" + name;
        Folder child = new Folder(name, path, this.currentFolder);
        this.currentFolder.children.add(child);
    }
 
    void newFile(String name, String ext, String permission) {
        String path = this.currentFolder.path + "/" + name + ext;
        File child = new File(name, ext, path, this.currentFolder, permission);
        this.currentFolder.children.add(child);
    }

    void rename(Directory toBeRenamed, String name) {
        for (int i = 0; i < this.currentFolder.children.size(); ++i) {
            if (toBeRenamed == this.currentFolder.children.get(i)) {
                this.currentFolder.children.get(i).name = name;
                break;
            }
        }
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
                    switch (((File) toBeOpened).extension) {
                        case "txt":
                            System.out.println("Opening notepad");
                            break;
                        case "jpg":
                            System.out.println("Opening image viewer");
                            new ImageViewer(new FileChooser().showOpenDialog(null).toURI().toString());
                            break;
                        case "mp3":
                            System.out.println("Opening music player");
                            new JavaFXMediaPlayer(new FileChooser().showOpenDialog(null).toURI().toString());
                            break;
                        case "mp4":
                            System.out.println("Opening video player");
                            new JavaFXMediaPlayer(new FileChooser().showOpenDialog(null).toURI().toString());
                            break;
                        case "pdf":
                            System.out.println("Opening pdf viewer");
                            break;
                        case "html":
                            System.out.println("Opening browser");
                            new WebBrowser();
                            break;
                    }
                }
            }
        }
    }
 
    void back() {
        this.currentFolder = this.currentFolder.parent != null ? this.currentFolder.parent : this.root;
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

    void store() {
        String address = "data.txt";
        ObjectOutputStream fileSystemData;
        try {
            fileSystemData = new ObjectOutputStream(new FileOutputStream(address));
            fileSystemData.writeObject(root);
            fileSystemData.close();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    void retrieve() {
        String address = "data.txt";
        FileInputStream fileInput;
        ObjectInputStream fileSystemData;
        try {
            fileInput = new FileInputStream(address);
            fileSystemData = new ObjectInputStream(fileInput);
            Folder tmp = (Folder) fileSystemData.readObject();
            fileInput.close();
            fileSystemData.close();
            this.root = tmp;
            this.currentFolder = tmp;
        } catch (IOException e) {
            this.root = null;
            this.currentFolder = null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
