package sfe.os;
import apps.*;
import java.io.*;
import directory.*;
import directory.File;


public class FileSystem {

    private static final String COPY_PROCESS = "copy";
    private static final String CUT_PROCESS = "cut";
    private Folder root, currentFolder;
    private Directory selected = null;
    Directory toBePasted;
    String whichProcess;
    public FileSystem() {
        root = new Folder("root", "/home", null);
        currentFolder = root;
        Folder storage = newFolder("home");
        storage.setHidden();
        this.seeds(storage, "src/storage");
        this.retrieve();
    }
    public Folder getRoot() {
        return this.root;
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
    Folder newFolder(String name) {
        String path = this.currentFolder.getPath() + "/" + name;
        Folder child = new Folder(name, path, this.currentFolder);
        this.currentFolder.getChildren().add(child);
        return child;
    }
    File newFile(String name, String ext, String permission) {
        String path = this.currentFolder.getPath() + "/" + name + ext;
        File child = new File(name, ext, path, this.currentFolder, permission);
        this.currentFolder.getChildren().add(child);
        return child;
    }
    void rename(Directory toBeRenamed, String name) {
        for (int i = 0; i < this.currentFolder.getChildren().size(); ++i) {
            if (toBeRenamed == this.currentFolder.getChildren().get(i)) {
                this.currentFolder.getChildren().get(i).setName(name);
                break;
            }
        }
    }
    void delete(Directory toBeDeleted) {
        for (int i = 0; i < this.currentFolder.getChildren().size(); ++i) {
            if (toBeDeleted == this.currentFolder.getChildren().get(i)) {
                this.currentFolder.getChildren().remove(i);
                break;
            }
        }
    }
    void open(Directory toBeOpened) {
        for (int i = 0; i < this.currentFolder.getChildren().size(); ++i) {
            if (toBeOpened == this.currentFolder.getChildren().get(i)) {
                if (toBeOpened instanceof Folder) {
                    this.currentFolder = (Folder) toBeOpened;
                } else {
                    String pth = toBeOpened.getRealPath();
                    int idx = 0;
                    if(pth != null) {
                        pth.indexOf("/storage");
                        pth = pth.substring(idx);
                        pth = pth.replaceAll("%20", " ");
                    }
                    switch (((File) toBeOpened).getExtension()) {
                        case "txt":
                            System.out.println("Opening text editor");
                            new TextEditor((File) toBeOpened);
                            break;
                        case "jpg":
                            System.out.println("Opening image viewer");
                            new ImageViewer(pth);
                            break;
                        case "mp3":
                            System.out.println("Opening music player");
                            new MyMedia(pth);
                            break;
                        case "mp4":
                            System.out.println("Opening video player");
                            new MyMedia(pth);
                            break;
                        case "pdf":
                            System.out.println("Opening pdf viewer");
//                            new PDFViewer(toBeOpened.getRealPath());
                            break;
                        case "html":
                            System.out.println("Opening browser");
                            new WebBrowser(toBeOpened.getRealPath());
                            break;
                    }
                }
            }
        }
    }
    void back() {
        this.currentFolder = this.currentFolder.getParent() != null ? this.currentFolder.getParent() : this.root;
    }
    void copy(Directory toBeCopied) {
        for (int i = 0; i < this.currentFolder.getChildren().size(); ++i) {
            if (toBeCopied == this.currentFolder.getChildren().get(i)) {
                this.whichProcess = COPY_PROCESS;
                this.toBePasted = this.currentFolder.getChildren().get(i);
                break;
            }
        }
    }
    void cut(Directory toBeCutted) {
        for (int i = 0; i < this.currentFolder.getChildren().size(); ++i) {
            if (toBeCutted == this.currentFolder.getChildren().get(i)) {
                this.whichProcess = CUT_PROCESS;
                this.toBePasted = this.currentFolder.getChildren().get(i);
                break;
            }
        }
    }
    void paste() {
        if (this.whichProcess.equals(CUT_PROCESS)) {
            for (int i = 0; i < this.toBePasted.getParent().getChildren().size(); i++) {
                if (toBePasted.getParent().getChildren().get(i) == toBePasted) {
                    toBePasted.getParent().getChildren().remove(i);
                    break;
                }
            }
        }
        toBePasted.setPath(this.currentFolder.getPath() + "/" + toBePasted.getName());
        this.currentFolder.getChildren().add(toBePasted);
    }
    void store() {
        String address = "data.txt";
        ObjectOutputStream fileSystemData;
        try {
            fileSystemData = new ObjectOutputStream(new FileOutputStream(address));
            fileSystemData.writeObject(root);
            fileSystemData.close();
        } catch (IOException e) {
            System.out.println("store(): " + e.toString());
        }
    }
    void retrieve() {
        String address = "data.txt";
        ObjectInputStream fileSystemData;
        try {
            fileSystemData = new ObjectInputStream(new FileInputStream(address));
            Folder tmp = (Folder) fileSystemData.readObject();
            fileSystemData.close();
            this.root = tmp;
            this.currentFolder = tmp;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("retrieve(): " + e.toString());
        }
    }
    void seeds(Folder currPos, String path) {
        String name, extension, permission = "";
        java.io.File resDir = (new java.io.File(path));
        for(java.io.File currFile: resDir.listFiles()) {
            if(currFile.isDirectory()) {
                name = currFile.getName();
                Folder folder = new Folder(name, currPos.getPath() + "/" + name, currPos);
                folder.setRealPath(currFile.getPath());
                currPos.getChildren().add(folder);
                seeds(folder, currFile.getPath());
            }else {
                name = currFile.getName().substring(0, currFile.getName().indexOf('.'));
                extension = currFile.getName().substring(currFile.getName().indexOf('.') + 1);
                permission = extension.equals(".html") ? "r" : "r/w";
                File fle = new File(name, extension, currPos.getPath() + "/" + name + "." + extension,currPos, permission);
                fle.setRealPath(currFile.toURI().toString());
                currPos.getChildren().add(fle);
            }
        }
    }
    void printAll() {
        printAll(this.root, 0);
    }
    void printAll(Directory current, int cnt) {
        if (current == null) { return; }
        int t = cnt;
        while (t-- > 0) { System.out.print("-"); }
        System.out.println(current.getName());
        if (current instanceof File) { return; }
        for (int i = 0; i < ((Folder) current).getChildren().size(); ++i) {
            printAll(((Folder) current).getChildren().get(i), cnt + 2);
        }
    }
}
