package sfe.os;

import apps.FXMediaPlayer;
import apps.ImageViewer;
import apps.Memo;
import apps.WebBrowser;
import directory.Directory;
import directory.File;
import directory.Folder;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.io.*;

import java.util.LinkedList;


public class FileSystem {
    public static LinkedList<Memo> txtEditorList;
    private static final String COPY_PROCESS = "copy";
    private static final String CUT_PROCESS = "cut";
    private Folder root, currentFolder;
    private Directory selected = null;
    Directory toBePasted;
    String whichProcess;


    static CPU cpu=new CPU();
    public FileSystem(CPU cpu) {
        this.cpu=cpu;
        root = new Folder("root", "/home", null);
        currentFolder = root;
        this.seeds(root, "src/storage");
        this.retrieve();
        txtEditorList = new LinkedList<>();
    }

    public Folder getRoot() {
        return this.root;
    }

    void goRoot() { this.currentFolder = root;  }

    public void select(Directory selected, Label view) {
        this.selected = selected;
        if (view != null)
            view.setBackground(new Background(new BackgroundFill(Color.BLANCHEDALMOND, CornerRadii.EMPTY, Insets.EMPTY)));
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
                            boolean opened = false;
                            for (Memo txtE : txtEditorList) {
                                if (toBeOpened == txtE.getChosenFile()) {
                                    opened = true;
                                }
                            }
                            if (!opened) {
                                Process p = new Process("TextEditor");

                                cpu.addProcess(p);
                                if(cpu.list.size()==1){
                                    cpu.RR_Schedule();
                                }
                                txtEditorList.add(new Memo((File) toBeOpened, p.getId(), cpu));
                            }
                            else {
                                System.out.print("Synchronized!\n");
                                JOptionPane.showMessageDialog(null, "The file is opened!", "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                            break;
                        case "jpg":
                            System.out.println("Opening image viewer");
                            Process p=new Process("ImageViewer");
                            cpu.addProcess(p);
                            if(cpu.list.size()==1) {
                                cpu.RR_Schedule();
                            }
                            new ImageViewer(new java.io.File(pth),p.getId(),cpu);
                        case "mp3":
                            System.out.println("Opening music player");
                            Process p1=new Process("MusicPlayer");
                            cpu.addProcess(p1);
                            if(cpu.list.size()==1){
                                cpu.RR_Schedule();
                            }
                            new FXMediaPlayer(new java.io.File(pth),p1.getId(),cpu);
                            break;
                        case "mp4":

                            System.out.println("Opening video player");
                            Process p2=new Process("vedioPlayer");
                            cpu.addProcess(p2);
                            if(cpu.list.size()==1){
                                cpu.RR_Schedule();
                            }
                            new FXMediaPlayer(new java.io.File(pth),p2.getId(),cpu);
                            break;
                        case "html":
                            System.out.println("Opening browser");
                            Process p3=new Process("WebBrowser");
                            cpu.addProcess(p3);
                            if(cpu.list.size()==1){
                                cpu.RR_Schedule();
                            }
                            new WebBrowser(WebBrowser.defaultUrl,p3.getId(),cpu);
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
        if (resDir.listFiles() == null) return;
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
                permission = extension.equals(".txt") ? "r/w" : "r";
                File fle = new File(name, extension, currPos.getPath() + "/" + name + "." + extension,currPos, permission);
                fle.setRealPath(currFile.getAbsolutePath());
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
        System.out.println(current.getName() + "  " + current.getSize());
        if (current instanceof File) { return; }
        for (int i = 0; i < ((Folder) current).getChildren().size(); ++i) {
            printAll(((Folder) current).getChildren().get(i), cnt + 2);
        }
    }
}