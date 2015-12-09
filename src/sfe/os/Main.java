package sfe.os;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Optional;

public class Main extends Application {

    private Stage stage;
    private BorderPane explorer;
    FileSystem fileSystem;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.stage = primaryStage;
        this.stage.setTitle("Explorer");
        this.stage.setFullScreen(true);
        this.stage.setFullScreenExitHint("");

        fileSystem = new FileSystem();

        desktop();

        stage.show();
    }

    private void desktop() {
        StackPane desktop = new StackPane();
        Label myPC = new Label();
        myPC.setGraphic(new ImageView("res/mypc.png"));
        myPC.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                if(event.getClickCount() == 2) {
                    initRootLayout();
                    openPath(fileSystem.getCurrentFolder());
                    stage.setScene(new Scene(explorer));
                    stage.setFullScreen(true);
                }
            }
        });
        desktop.getChildren().addAll(myPC);
        stage.setScene(new Scene(desktop));
    }

    private void initRootLayout() {
        explorer = new BorderPane();
        VBox topContainer = new VBox();
        MenuBar menuBar = new MenuBar();
        ToolBar toolBar = new ToolBar();

        // populating MenuBar
        Menu file = new Menu("File");
        {
            MenuItem newFileMenu = new MenuItem("New File");
            newFileMenu.setOnAction(event -> newFileDialog());
            MenuItem close = new MenuItem("Close");
            close.setOnAction(event -> System.exit(0));
            file.getItems().addAll(newFileMenu, close);
        }
        Menu edit = new Menu("Edit");
        {
            MenuItem copyMenu = new MenuItem("Copy");
            MenuItem cutMenu = new MenuItem("Cut");
            MenuItem pasteMenu = new MenuItem("Paste");
            MenuItem deleteMenu = new MenuItem("Delete");
            edit.getItems().addAll(copyMenu, cutMenu, pasteMenu, deleteMenu);
        }
        menuBar.getMenus().addAll(file, edit);

        // populating ToolBar
        Button back = new Button();
            back.setGraphic(new ImageView("res/icon-back.png"));
            back.setOnAction(event -> openPath(fileSystem.getCurrentFolder().parent));
        Button newFile = new Button();
            newFile.setGraphic(new ImageView("res/newFile.png"));
            newFile.setOnAction(event -> newFileDialog());
        Button newFolder = new Button();
            newFolder.setGraphic(new ImageView("res/newFolder.png"));
        Button delete = new Button();
            delete.setGraphic(new ImageView("res/delete.png"));
        toolBar.getItems().addAll(back, newFile, newFolder, delete);

        topContainer.getChildren().addAll(menuBar, toolBar);

        explorer.setTop(topContainer);
    }

    private void openPath(Folder currFolder) {
        TilePane tiles = new TilePane();
        tiles.setPrefColumns(6);
        tiles.setHgap(4);
        tiles.setVgap(4);

        LinkedList<Directory> dirs = currFolder.getChildren();

        Label view[] = new Label[dirs.size()];
        for (int i = 0; i < view.length; i++) {
            Directory dir = dirs.get(i);
            view[i] = new Label(dir.name);
            if (dir instanceof Folder) {
                view[i].setGraphic(new ImageView("res/folder.png"));
            } else {
                File file = (File) dir;
                switch (file.getExtension()) {
                    case "txt":
                        view[i].setGraphic(new ImageView("res/txt.png"));
                        break;
                    case "mp3":
                        view[i].setGraphic(new ImageView("res/mp3.png"));
                        break;
                    default:
                        view[i].setGraphic(new ImageView("res/file.png"));
                        break;
                }
            }
            tiles.getChildren().add(view[i]);
        }

        explorer.setCenter(tiles);
    }

    private void newFileDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("New File");
        dialog.setHeaderText("Header text mlosh lzma bs 3agbny xD");
        dialog.setGraphic(new ImageView("res/newFile.png"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField textField = new TextField();
        textField.setPromptText("Username");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(textField, 1, 0);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("txt","mp3");
        comboBox.setValue("txt");

        grid.add(new Label("File type:"), 0, 1);
        grid.add(comboBox, 1, 1);


        ButtonType okBtnType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okBtnType, ButtonType.CANCEL);

        Node okBtn = dialog.getDialogPane().lookupButton(okBtnType);
        okBtn.setDisable(true);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            okBtn.setDisable(newValue.trim().isEmpty());
        });


        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okBtnType)
                return new Pair<>(textField.getText(), comboBox.getValue());
            return null;
        });


        Platform.runLater(textField::requestFocus);
        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            String name = pair.getKey();
            String type = pair.getValue();
            switch (type) {
                case "txt":
                    fileSystem.newTxtFile(name);
                    break;
                case "mp3":
                    fileSystem.newMp3File(name);
                    break;
            }
        });

        openPath(fileSystem.getCurrentFolder());
    }

}
