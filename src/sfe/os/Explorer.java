package sfe.os;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class Explorer {

    private Stage stage;
    private FileSystem fileSystem;
    private ContextMenu rightClickMenu4Tiles = null;
    private BorderPane explorer;
    private Button back;


    public Explorer() {
        stage = new Stage();
        stage.setTitle("Explorer");
        stage.setHeight(600);
        stage.setWidth(850);

        fileSystem = new FileSystem();

        back = new Button(null, new ImageView("res/icon-back.png"));
        back.setDisable(true);
        initRootLayout();
        refresh();

        stage.setScene(new Scene(explorer));
        stage.show();
    }

    private void initRootLayout() {
        explorer = new BorderPane();
        VBox topContainer = new VBox();
        MenuBar menuBar = new MenuBar();
        ToolBar toolBar = new ToolBar();

        // populating MenuBar
        Menu fileMenu = new Menu("File");
        {
            MenuItem newFileMenu = new MenuItem("New File");
            newFileMenu.setOnAction(event -> newFileDialog());
            MenuItem close = new MenuItem("Close");
            close.setOnAction(event ->  { fileSystem.store(); stage.close(); });
            fileMenu.getItems().addAll(newFileMenu, close);
        }
        Menu editMenu = new Menu("Edit");
        {
            MenuItem copyMenu = new MenuItem("Copy");
            MenuItem cutMenu = new MenuItem("Cut");
            MenuItem pasteMenu = new MenuItem("Paste");
            pasteMenu.setOnAction(event -> {fileSystem.paste(); refresh();});
            MenuItem deleteMenu = new MenuItem("Delete");
            editMenu.getItems().addAll(copyMenu, cutMenu, pasteMenu, deleteMenu);
        }
        menuBar.getMenus().addAll(fileMenu, editMenu);

        // populating ToolBar
        back.setOnAction(event -> {
            fileSystem.back();
            refresh();
            if (fileSystem.getCurrentFolder().name.equals("root")) {
                back.setDisable(true);
            }
        });
        Button newFile = new Button();
        newFile.setTooltip(new Tooltip("New File"));
        newFile.setGraphic(new ImageView("res/newFile.png"));
        newFile.setOnAction(event -> newFileDialog());
        Button newFolder = new Button();
        newFolder.setTooltip(new Tooltip("New Folder"));
        newFolder.setGraphic(new ImageView("res/newFolder.png"));
        newFolder.setOnAction(event -> newFolderDialog());
        Button copyBtn = new Button();
        copyBtn.setTooltip(new Tooltip("Copy"));
        copyBtn.setGraphic(new ImageView("res/copy.png"));
        Button cutBtn = new Button();
        cutBtn.setTooltip(new Tooltip("Cut"));
        cutBtn.setGraphic(new ImageView("res/cut.png"));
        Button pasteBtn = new Button();
        pasteBtn.setTooltip(new Tooltip("Paste"));
        pasteBtn.setGraphic(new ImageView("res/paste.png"));
        pasteBtn.setOnAction(event -> { fileSystem.paste(); refresh(); });
        Button delete = new Button();
        delete.setTooltip(new Tooltip("Delete"));
        delete.setGraphic(new ImageView("res/delete.png"));

        toolBar.getItems().addAll(back, newFile, newFolder, copyBtn, cutBtn, pasteBtn, delete);

        topContainer.getChildren().addAll(menuBar, toolBar);

        explorer.setTop(topContainer);
    }

    private void refresh() {

        TilePane tiles = new TilePane();
        tiles.setPrefColumns(8);
        tiles.setHgap(25);
        tiles.setVgap(30);
        tiles.setPadding(new Insets(20));
        tiles.setOnMouseClicked(this::tilePaneRightClickContextMenu);
        tiles.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        populateTiles(tiles);

        explorer.setCenter(tiles);
    }

    private void populateTiles(TilePane tiles) {
        Label view[] = new Label[fileSystem.getCurrentFolder().getChildren().size()];
        for (int i = 0; i < view.length; i++) {
            Directory dir = fileSystem.getCurrentFolder().getChildren().get(i);
            view[i] = new Label(dir.name);
            view[i].setContentDisplay(ContentDisplay.TOP);
            view[i].setPadding(new Insets(0, 5, 0, 5));
            view[i].setGraphicTextGap(1);
            view[i].setWrapText(true);
            view[i].setContextMenu(dirRightClickMenu(dir));
            setIcon(dir, view[i]);
            view[i].setOnMouseClicked(event -> {
                if(event.getButton().equals(MouseButton.PRIMARY)) {
                    if(event.getClickCount() == 1) {
                        fileSystem.select(dir);
                    }
                    if(event.getClickCount() == 2) {
                        fileSystem.open(dir);
                        back.setDisable(false);
                        if (dir instanceof Folder)
                            refresh();
                    }
                }
            });
            final Label currView = view[i];
            view[i].setOnMouseEntered(event -> {
                currView.setScaleX(1.2);
                currView.setScaleY(1.2);
            });
            view[i].setOnMouseExited(event -> {
                currView.setScaleX(1);
                currView.setScaleY(1);
            });
            if(!dir.isHidden()) { tiles.getChildren().add(view[i]); }
        }
    }

    private void tilePaneRightClickContextMenu(MouseEvent event) {
        if(rightClickMenu4Tiles == null) { rightClickMenu4Tiles = new ContextMenu(); }
        else if(rightClickMenu4Tiles.getItems().size() > 0) {rightClickMenu4Tiles.getItems().clear();}
        MenuItem newFolderItem = new MenuItem("New Folder");
        MenuItem newFileItem = new MenuItem("New File");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem propertiesItem = new MenuItem("Properties");
        newFolderItem.setOnAction(event1 -> newFolderDialog());
        newFileItem.setOnAction(event1 ->  newFileDialog());
        pasteItem.setOnAction(event1 -> { fileSystem.paste(); refresh(); });
        propertiesItem.setOnAction(event1 -> { /* Properties.*/ });
        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        SeparatorMenuItem separatorMenuItem2 = new SeparatorMenuItem();
        rightClickMenu4Tiles.getItems().addAll(newFolderItem, newFileItem, separatorMenuItem1, pasteItem, separatorMenuItem2, propertiesItem);
        if(event.getButton().equals(MouseButton.SECONDARY) && (!event.getTarget().toString().contains("label") && !event.getTarget().toString().contains("Label"))) { rightClickMenu4Tiles.show(explorer, event.getScreenX(), event.getScreenY());}
        else { rightClickMenu4Tiles.hide(); }
    }

    private void setIcon(Directory dir, Label view) {
        if (dir instanceof Folder) {
            view.setGraphic(new ImageView("res/folder.png"));
        } else {
            File file = (File) dir;
            switch (file.extension) {
                case "txt":
                    view.setGraphic(new ImageView("res/txt.png"));
                    break;
                case "jpg":
                    view.setGraphic(new ImageView("res/jpg.png"));
                    break;
                case "mp3":
                    view.setGraphic(new ImageView("res/mp3.png"));
                    break;
                case "mp4":
                    view.setGraphic(new ImageView("res/mp4.png"));
                    break;
                case "pdf":
                    view.setGraphic(new ImageView("res/pdf.png"));
                    break;
                case "html":
                    view.setGraphic(new ImageView("res/html.png"));
                    break;
                default:
                    view.setGraphic(new ImageView("res/file.png"));
                    break;
            }
        }
    }

    private ContextMenu dirRightClickMenu(Directory dir) {
        ContextMenu rightClickMenu = new ContextMenu();
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(e -> {
            fileSystem.open(dir);
            if (dir instanceof Folder)
                refresh();
        });

        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e -> fileSystem.copy(dir));
        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setOnAction(e -> fileSystem.cut(dir));

        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction(event1 -> renameDir(dir));
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> {
            fileSystem.delete(dir);
            refresh();
        });
        MenuItem propertiesItem = new MenuItem("Properties");

        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        SeparatorMenuItem separatorMenuItem2 = new SeparatorMenuItem();
        SeparatorMenuItem separatorMenuItem3 = new SeparatorMenuItem();

        rightClickMenu.getItems().addAll(openItem, separatorMenuItem1, copyItem, cutItem, separatorMenuItem2, renameItem, deleteItem, separatorMenuItem3, propertiesItem);
        return rightClickMenu;
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
        textField.setPromptText("File name");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(textField, 1, 0);


        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Text", "Image", "Sound", "Video", "PDF", "Website");
        comboBox.setValue("Text");

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
            String permission = null, ext = null;
            switch (type) {
                case "Text":
                    ext = "txt";
                    permission = "r/w";
                    break;
                case "Image":
                    ext = "jpg";
                    permission = "r";
                    break;
                case "Sound":
                    ext = "mp3";
                    permission = "r";
                    break;
                case "Video":
                    ext = "mp4";
                    permission = "r";
                    break;
                case "PDF":
                    ext = "pdf";
                    permission = "r";
                    break;
                case "Website":
                    ext = "html";
                    permission = "r";
                    break;
            }
            File f = fileSystem.newFile(name, ext, permission);
            if (!type.equals("Text") && !type.equals("Website"))
                new sfe.os.FileChooser(f);
        });
        refresh();
    }

    private void newFolderDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Folder");
        dialog.setHeaderText("Header text mlosh lzma bs 3agbny xD");
        dialog.setGraphic(new ImageView("res/newFolder.png"));
        dialog.setContentText("Folder name:");

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setDisable(true);

        dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            okBtn.setDisable(newValue.trim().isEmpty());
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> fileSystem.newFolder(name));
        refresh();
    }

    private void renameDir(Directory dir) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename");
        dialog.setHeaderText("Header text mlosh lzma bs 3agbny xD");
        dialog.setContentText("New name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> fileSystem.rename(dir, name));
        refresh();
    }


}
