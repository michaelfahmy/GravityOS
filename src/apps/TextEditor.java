package apps;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.dialog.FontSelectorDialog;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;


public class TextEditor {

    private Stage stage;

    public TextEditor() {
        stage = new Stage();
        stage.setTitle("Text Editor");
        stage.setScene(new nota().not());
        stage.setResizable(false);
        stage.show();
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

        TextArea txt = new TextArea();

        public Scene not() {
            BorderPane border = new BorderPane();
            newItem.setOnAction(t -> txt.setText(""));
            openItem.setOnAction(t -> {
                FileChooser choice = new FileChooser();
                choice.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("txt Files", "txt"));
                File f = choice.showOpenDialog(null);
                if (f != null) {
                    txt.setText("");
                    try {
                        Scanner scan = new Scanner(new FileReader(f));
                        while (scan.hasNext())
                            txt.appendText(scan.nextLine());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("File access cancelled by user.");
                }
            });
            saveItem.setOnAction(t -> {
                if (!txt.getText().isEmpty()) {
                    FileChooser choice = new FileChooser();
                    choice.setTitle("Specify a file to save");
                    choice.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("txt Files", "txt"));
                    File f = choice.showSaveDialog(null);
                    if (f != null) {
                        BufferedWriter out;
                        try {
                            out = new BufferedWriter(new FileWriter(f));
                            out.write(txt.getText());
                            out.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println("Save as file: " + f);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "text is empty", "Error", JOptionPane.ERROR_MESSAGE);
                }

            });
            exitItem.setOnAction(t -> stage.close());

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
            menuBar.getStylesheets().add(TextEditor.class.getResource("NotpadStyle.css").toExternalForm());

            border.setTop(menuBar);
            border.setCenter(txt);

            return new Scene(border, 600, 400);
        }
    }
}
