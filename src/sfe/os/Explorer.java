package sfe.os;

import directory.Directory;
import directory.File;
import directory.Folder;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;


public class Explorer {

    private Stage stage;
    public static FileSystem fileSystem;
    private ContextMenu rightClickMenu4Tiles = null;
    private BorderPane explorer;
    private Button back;
    private boolean mark=false;


    public Explorer() {
        stage = new Stage();
        stage.setTitle("Explorer");
        stage.setHeight(600);
        stage.setWidth(850);

        fileSystem = new FileSystem();

        back = new Button(null, new ImageView("res/ExplorerIcons/icon-back.png"));
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
            copyMenu.setOnAction(event -> {fileSystem.copy(fileSystem.getSelected()); refresh();});
            MenuItem cutMenu = new MenuItem("Cut");
            cutMenu.setOnAction(event -> {fileSystem.cut(fileSystem.getSelected()); refresh(); });
            MenuItem pasteMenu = new MenuItem("Paste");
            pasteMenu.setOnAction(event -> {fileSystem.paste(); refresh(); mark=false;});
            MenuItem deleteMenu = new MenuItem("Delete");
            deleteMenu.setOnAction(event -> {fileSystem.delete(fileSystem.getSelected()); refresh(); mark=false;});

            editMenu.getItems().addAll(copyMenu, cutMenu, pasteMenu, deleteMenu);
        }
        menuBar.getMenus().addAll(fileMenu, editMenu);

        // populating ToolBar
        back.setOnAction(event -> {
            fileSystem.back();
            refresh();
            if (fileSystem.getCurrentFolder().getName().equals("root")) {
                back.setDisable(true);
                mark=false;
            }
        });
        Button newFile = new Button();
        newFile.setTooltip(new Tooltip("New File"));
        newFile.setGraphic(new ImageView("res/ExplorerIcons/newFile.png"));
        newFile.setOnAction(event -> { newFileDialog(); mark=false; });
        Button newFolder = new Button();
        newFolder.setTooltip(new Tooltip("New Folder"));
        newFolder.setGraphic(new ImageView("res/ExplorerIcons/newFolder.png"));
        newFolder.setOnAction(event -> { newFolderDialog(); mark=false; });
        Button copyBtn = new Button();
        copyBtn.setTooltip(new Tooltip("Copy"));
        copyBtn.setGraphic(new ImageView("res/ExplorerIcons/copy.png"));
        copyBtn.setOnAction(event -> {fileSystem.copy(fileSystem.getSelected()); refresh();});
        Button cutBtn = new Button();
        cutBtn.setTooltip(new Tooltip("Cut"));
        cutBtn.setGraphic(new ImageView("res/ExplorerIcons/cut.png"));
        cutBtn.setOnAction(event -> {fileSystem.cut(fileSystem.getSelected()); refresh(); });
        Button pasteBtn = new Button();
        pasteBtn.setTooltip(new Tooltip("Paste"));
        pasteBtn.setGraphic(new ImageView("res/ExplorerIcons/paste.png"));
        pasteBtn.setOnAction(event -> { fileSystem.paste(); refresh(); mark=false;});
        Button delete = new Button();
        delete.setTooltip(new Tooltip("Delete"));
        delete.setGraphic(new ImageView("res/ExplorerIcons/delete.png"));
        delete.setOnAction(event -> {fileSystem.delete(fileSystem.getSelected()); refresh(); mark=false;});

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
        explorer.setBottom(new Label(fileSystem.getCurrentFolder().getPath()));
        populateTiles(tiles);
        explorer.setCenter(tiles);

    }

    private void populateTiles(TilePane tiles) {
        Label view[] = new Label[fileSystem.getCurrentFolder().getChildren().size()];
        for (int i = 0; i < view.length; i++) {
            Directory dir = fileSystem.getCurrentFolder().getChildren().get(i);
            view[i] = new Label(dir.getName());
            view[i].setContentDisplay(ContentDisplay.TOP);
            view[i].setPadding(new Insets(0, 5, 0, 5));
            view[i].setGraphicTextGap(1);
            view[i].setWrapText(true);
            view[i].setContextMenu(dirRightClickMenu(dir));
            setIcon(dir, view[i]);
            final Label currView = view[i];
            view[i].setOnMouseClicked(event -> {
                if(event.getButton().equals(MouseButton.PRIMARY)) {
                    if(event.getClickCount() == 1) {
                        if(!mark) {
                            fileSystem.select(dir, currView);
                            mark = true;
                        }
                    }
                    if(event.getClickCount() == 2) {
                        mark = false;
                        fileSystem.open(dir);
                        if (dir instanceof Folder) {
                            back.setDisable(false);
                            refresh();
                        }
                    }
                }
            });
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

    private void setIcon(Directory dir, Label view) {
        if (dir instanceof Folder) {
            view.setGraphic(new ImageView("res/ExplorerIcons/folder.png"));
        } else {
            File file = (File) dir;
            switch (file.getExtension()) {
                case "txt":
                    view.setGraphic(new ImageView("res/ExplorerIcons/txt.png"));
                    break;
                case "jpg":
                    view.setGraphic(new ImageView("res/ExplorerIcons/jpg.png"));
                    break;
                case "mp3":
                    view.setGraphic(new ImageView("res/ExplorerIcons/mp3.png"));
                    break;
                case "mp4":
                    view.setGraphic(new ImageView("res/ExplorerIcons/mp4.png"));
                    break;
                case "html":
                    view.setGraphic(new ImageView("res/ExplorerIcons/html.png"));
                    break;
                default:
                    view.setGraphic(new ImageView("res/ExplorerIcons/file.png"));
                    break;
            }
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
        if(event.getButton().equals(MouseButton.SECONDARY)
            && (!event.getTarget().toString().contains("label") && !event.getTarget().toString().contains("Label"))) {
            mark = false;
            fileSystem.select(null, null);
            rightClickMenu4Tiles.show(explorer, event.getScreenX(), event.getScreenY());
        } else if (event.getButton().equals(MouseButton.PRIMARY)
                && (!event.getTarget().toString().contains("label") && !event.getTarget().toString().contains("Label"))) {
            refresh();
            mark=false;
            fileSystem.select(null, null);
            rightClickMenu4Tiles.hide();
        } else {
            rightClickMenu4Tiles.hide();
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New File");
        dialog.setHeaderText("Enter your new file name below");
        dialog.setGraphic(new ImageView("res/ExplorerIcons/newFile.png"));
        dialog.setContentText("File name:");

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setDisable(true);

        dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            okBtn.setDisable(newValue.trim().isEmpty());
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> fileSystem.newFile(name, "txt", "r/w"));
        refresh();
    }

    private void newFolderDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Folder");
        dialog.setHeaderText("Enter your new folder name below");
        dialog.setGraphic(new ImageView("res/ExplorerIcons/newFolder.png"));
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
        dialog.setContentText("New name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> fileSystem.rename(dir, name));
        refresh();
    }


}
