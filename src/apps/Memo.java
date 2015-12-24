package apps;


import directory.File;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.dialog.FontSelectorDialog;
import sfe.os.CPU;
import sfe.os.FileChooser;
import sfe.os.FileSystem;

import javax.swing.*;
import java.io.*;


public class Memo {

    private Stage stage;
    public static directory.File chosenFile = null;
    static private int cnt = 0;
    int id;
    static CPU cpu=new CPU();
    sfe.os.FileChooser fileChooser;
    void closeIt() {
        for(Memo cur: FileSystem.txtEditorList) {
            if(cur.chosenFile == chosenFile) {
                FileSystem.txtEditorList.remove(cur);
                break;
            }
        }
    }
    public Memo(directory.File chosenFile,int id,CPU cpu) {
        this.id=id;
        this.cpu=cpu;
        this.chosenFile = chosenFile;
        ++cnt;
        stage = new Stage();
        stage.setTitle("Memo");
        stage.setScene(new nota().not());
        stage.show();
        stage.setOnCloseRequest(event -> { closeIt(); stage.close();
            System.out.println("Memo with id :"+id+" Is removed");
            cpu.RemoveProcess(id);
        });
    }
    public File getChosenFile() {
        return this.chosenFile;
    }
    class nota {

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");

        Menu formatMenu = new Menu("Format");
        MenuItem fontItem = new MenuItem("Font");
        MenuItem wrapItem = new MenuItem("Wrap Text");

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About Editor");
        TextArea txt;

        public nota() {
            this.txt = new TextArea();
            initialize();
        }

        void initialize() {
            if (chosenFile != null && chosenFile.getRealPath() != null) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(chosenFile.getRealPath())));
                    do {
                        txt.setText(txt.getText() + br.readLine() + "\n");
                    } while (br.ready());
                } catch (IOException e) {
                    txt.setText("");
                }
            }
        }

        public Scene not() {
            BorderPane border = new BorderPane();
            newItem.setOnAction(t -> txt.setText(""));
            openItem.setOnAction(t -> {
                fileChooser = new sfe.os.FileChooser("txt", null, "open",cpu);
                stage.close();
            });
            saveItem.setOnAction(t -> {
                if (!txt.getText().isEmpty()) {
                    OutputStream file = null;
                    if(chosenFile != null) {
                        try {
                            file = new FileOutputStream(chosenFile.getRealPath() == null ? "src/storage/Text files/txtFile"+cnt+".txt": chosenFile.getRealPath());
                            file.write(txt.getText().getBytes());
                            file.close();
                        } catch (IOException e) { /* do nothing.. */ }
                        if(chosenFile.getRealPath() == null) { chosenFile.setRealPath("src/storage/Text files/txtFile" + cnt + ".txt"); }
                    }else {
                        try {
                            file = new FileOutputStream("src/storage/Text files/txtFile" + cnt + ".txt");
                            file.write(txt.getText().getBytes());
                            file.close();
                            fileChooser = new FileChooser("txt", "src/storage/Text files/txtFile" + cnt +".txt", "save",cpu);
                        } catch (IOException e) { /*do nothing.. */ }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "text is empty", "Error", JOptionPane.ERROR_MESSAGE);
                }

            });
            exitItem.setOnAction(t -> { closeIt(); stage.close(); });

            fontItem.setOnAction(t -> {
                Font f = txt.getFont();
                FontSelectorDialog fontS = new FontSelectorDialog(f);
                fontS.showAndWait();
                if (fontS.getResult() != null) {
                    System.out.println(String.valueOf(fontS.getResult()));
                    txt.setFont(fontS.getResult());
                }
            });

            wrapItem.setOnAction(t -> txt.setWrapText(true));

            aboutItem.setOnAction(t -> {
                String message = "Sample application using the simple text editor component\n" +
                        "\n" +
                        "Version 12/12/2015\n";
                JOptionPane.showMessageDialog(new JPanel(), "<html><center>" + "<br>" + message);

            });

            fileMenu.getItems().addAll(newItem, openItem, saveItem, exitItem);
            formatMenu.getItems().addAll(fontItem, wrapItem);
            helpMenu.getItems().addAll(aboutItem);

            menuBar.getMenus().addAll(fileMenu, formatMenu, helpMenu);
            menuBar.getStylesheets().add(Memo.class.getResource("MemoStyle.css").toExternalForm());

            border.setTop(menuBar);
            border.setCenter(txt);

            return new Scene(border, 600, 400);
        }
    }
}