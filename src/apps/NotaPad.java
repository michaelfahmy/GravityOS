package apps;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.controlsfx.dialog.FontSelectorDialog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by yasmine on 18/12/2015.
 */
public class NotaPad {

    public NotaPad()

    {Stage primaryStage=new Stage();
        primaryStage.setTitle("NotePad");
        primaryStage.setScene(new nota().not());
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    class nota{
        javafx.scene.control.MenuBar menuBar = new javafx.scene.control.MenuBar();
        javafx.scene.control.Menu menu = new javafx.scene.control.Menu("File");
        javafx.scene.control.MenuItem firstitem = new javafx.scene.control.MenuItem("New");
        javafx.scene.control.MenuItem seconditem = new javafx.scene.control.MenuItem("Open");
        javafx.scene.control.MenuItem thirditem = new javafx.scene.control.MenuItem("Save");
        javafx.scene.control.MenuItem fourthitem = new javafx.scene.control.MenuItem("Exit");
        javafx.scene.control.Menu menu2= new javafx.scene.control.Menu("Format");
        javafx.scene.control.MenuItem firstmenu2 = new javafx.scene.control.MenuItem("Font");
        javafx.scene.control.MenuItem secondmenu2 = new javafx.scene.control.MenuItem("Wrap Text");
        javafx.scene.control.Menu menu3=new javafx.scene.control.Menu("Help");
        javafx.scene.control.MenuItem firstmenu3=new javafx.scene.control.MenuItem("AboutNotePad");
        javafx.scene.control.TextArea txt=new javafx.scene.control.TextArea();
        public Scene not()
        {
            GridPane gridpane = new GridPane();
            menuBar.getMenus().add(menu);
            firstitem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    txt.setText("");
                }
            });
            seconditem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t)
                {boolean option;
                    JFileChooser choice = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("txt Files", "txt");
                    choice.setFileFilter(filter);
                    choice.showOpenDialog(new Component() {
                        @Override
                        public String getName() {
                            return super.getName();
                        }
                    });
                    option=choice.isFileSelectionEnabled();
                    if (option) {
                        txt.setText("");
                        try {
                            Scanner scan = new Scanner(new FileReader(choice.getSelectedFile().getPath()));
                            while (scan.hasNext())
                                txt.appendText(scan.nextLine());
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else {
                        System.out.println("File access cancelled by user.");
                    }
                }
            });
            thirditem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    if(!txt.getText().isEmpty()){
                        JFileChooser choice= new JFileChooser();
                        choice.setDialogTitle("Specify a file to save");

                        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt Files", "txt");
                        choice.setFileFilter(filter);
                        int userSelection = choice.showSaveDialog(new Component() {
                            @Override
                            public String getName() {
                                return super.getName();
                            }
                        });
                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                            BufferedWriter out = null;
                            try {
                                out = new BufferedWriter(new FileWriter(choice.getSelectedFile().getPath()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                out.write(txt.getText());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Save as file: " + choice.getSelectedFile().getAbsolutePath());

                        }}
                    else
                    {JOptionPane.showMessageDialog (null, "text is empty", "Erorr", JOptionPane.ERROR_MESSAGE);
                    }

                }
            });

            fourthitem.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    Platform.exit();
                }
            });
            firstmenu2.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    javafx.scene.text.Font f = txt.getFont();
                    FontSelectorDialog fontS = new FontSelectorDialog(f);
                    fontS.showAndWait();
                    if (fontS.getResult() != null) {
                        System.out.println(String.valueOf(fontS.getResult()));
                        txt.setFont(fontS.getResult());
                    }

                }
            });
            secondmenu2.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    txt.setWrapText(true);
                }
            });
            firstmenu3.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    String message = "Sample application using the simple text editor component\n" +
                            "\n" +
                            "Version 12/12/2015\n";
                    JOptionPane.showMessageDialog(new JPanel(), "<html><center>" + "<br>" + message);

                }
            });
            menu.getItems().addAll(firstitem,seconditem,thirditem,fourthitem );
            menuBar.getMenus().add(menu2);
            menu2.getItems().addAll(firstmenu2,secondmenu2);
            menuBar.getMenus().add(menu3);
            menu3.getItems().addAll(firstmenu3);
            menuBar.getStylesheets().add(NotaPad.class.getResource("Style.css").toExternalForm());
            Group root = new Group();
            gridpane.getChildren().add(menuBar);
            menuBar.setTranslateY(-80);
            txt.setTranslateY(20);
            txt.setMaxSize(450,450);
            gridpane.getChildren().add(txt);
            Scene scene = new Scene(root,400,192);
            menuBar.prefWidthProperty().bind(scene.widthProperty());
            root.getChildren().add(gridpane);
            return  scene;
        }


    }
}
