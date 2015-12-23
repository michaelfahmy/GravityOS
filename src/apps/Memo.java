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
import sfe.os.*;

import javax.swing.*;
import java.io.*;


public class Memo {

    private Stage stage;
    private directory.File chosenFile = null;
    static private int cnt = 0;

    sfe.os.FileChooser fileChooser;
    public Memo(directory.File chosenFile) {
        this.chosenFile = chosenFile != null ? chosenFile : null;
        ++cnt;
        stage = new Stage();
        stage.setTitle("Memo");
        stage.setScene(new nota().not());
        stage.show();
    }
    public File getChosenFile() {
        return this.chosenFile;
    }
    class nota {
        // fileChooser.fileSystem.txtEditorList.remove(chosenFile);

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


        void closeIt() {
            for(Memo cur: FileSystem.txtEditorList) {
                if(cur.chosenFile == chosenFile) {
                    FileSystem.txtEditorList.remove(cur);
                    break;
                }
            }
        }

        public Scene not() {
            BorderPane border = new BorderPane();
            newItem.setOnAction(t -> txt.setText(""));
            openItem.setOnAction(t -> {
                fileChooser = new sfe.os.FileChooser(null, "open");
                stage.close();
            });
            saveItem.setOnAction(t -> {
                if (!txt.getText().isEmpty()) {
                    OutputStream file = null;
                    try {
                        file = new FileOutputStream(chosenFile.getRealPath() == null ? "src/res/Text files/txtFile"+cnt+".txt": chosenFile.getRealPath());
                        file.write(txt.getText().getBytes());
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.print("File Not Found\n");
                    }
                    if(chosenFile.getRealPath() == null) {
                        chosenFile.setRealPath("src/res/Text files/txtFile" + cnt + ".txt");
                    }
                    if(chosenFile == null) {
                        fileChooser = new sfe.os.FileChooser("", "save");
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