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

    private ContextMenu rightClickMenu4Tiles = null;
    private BorderPane explorer;
    private Button back;
    private boolean mark=false;
    static  CPU cpu=new CPU();
    public Explorer(CPU cpu) {
        this.cpu=cpu;
        stage = new Stage();
        stage.setTitle("Explorer");
        stage.setHeight(600);
        stage.setWidth(850);
        stage.setOnCloseRequest(event -> onClose());
        back = new Button(null, new ImageView("res/ExplorerIcons/icon-back.png"));
        back.setDisable(true);
        initRootLayout();
        refresh();

        stage.setScene(new Scene(explorer));
        stage.show();
    }


    private void onClose(){
        Main.fileSystem.store();
        Main.fileSystem.goRoot();
        stage.close();
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
            close.setOnAction(event ->  onClose());
            fileMenu.getItems().addAll(newFileMenu, close);
        }

        Menu editMenu = new Menu("Edit");
        {
            MenuItem copyMenu = new MenuItem("Copy");
            copyMenu.setOnAction(event -> {Main.fileSystem.copy(Main.fileSystem.getSelected()); refresh();});
            MenuItem cutMenu = new MenuItem("Cut");
            cutMenu.setOnAction(event -> {Main.fileSystem.cut(Main.fileSystem.getSelected()); refresh(); });
            MenuItem pasteMenu = new MenuItem("Paste");
            pasteMenu.setOnAction(event -> {Main.fileSystem.paste(); refresh(); mark=false;});
            MenuItem deleteMenu = new MenuItem("Delete");
            deleteMenu.setOnAction(event -> {Main.fileSystem.delete(Main.fileSystem.getSelected()); refresh(); mark=false;});

            editMenu.getItems().addAll(copyMenu, cutMenu, pasteMenu, deleteMenu);
        }
        menuBar.getMenus().addAll(fileMenu, editMenu);

        // populating ToolBar
        back.setOnAction(event -> {
            Main.fileSystem.back();
            refresh();
            if (Main.fileSystem.getCurrentFolder().getName().equals("root")) {
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
        copyBtn.setOnAction(event -> {Main.fileSystem.copy(Main.fileSystem.getSelected()); refresh();});
        Button cutBtn = new Button();
        cutBtn.setTooltip(new Tooltip("Cut"));
        cutBtn.setGraphic(new ImageView("res/ExplorerIcons/cut.png"));
        cutBtn.setOnAction(event -> {Main.fileSystem.cut(Main.fileSystem.getSelected()); refresh(); });
        Button pasteBtn = new Button();
        pasteBtn.setTooltip(new Tooltip("Paste"));
        pasteBtn.setGraphic(new ImageView("res/ExplorerIcons/paste.png"));
        pasteBtn.setOnAction(event -> { Main.fileSystem.paste(); refresh(); mark=false;});
        Button delete = new Button();
        delete.setTooltip(new Tooltip("Delete"));
        delete.setGraphic(new ImageView("res/ExplorerIcons/delete.png"));
        delete.setOnAction(event -> {Main.fileSystem.delete(Main.fileSystem.getSelected()); refresh(); mark=false;});

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
        explorer.setBottom(urlBar());
        populateTiles(tiles);
        explorer.setCenter(tiles);
    }

    private Label urlBar() {
        Label path = new Label(Main.fileSystem.getCurrentFolder().getPath());
        path.setPrefWidth(Double.MAX_VALUE);
        path.setStyle("-fx-font-size: 13; -fx-font-family: cursive; -fx-label-padding: 5; -fx-background-color: aliceblue; -fx-opacity: 0.4;");
        return path;
    }

    private void populateTiles(TilePane tiles) {
        Label view[] = new Label[Main.fileSystem.getCurrentFolder().getChildren().size()];
        for (int i = 0; i < view.length; i++) {
            Directory dir = Main.fileSystem.getCurrentFolder().getChildren().get(i);
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
                            Main.fileSystem.select(dir, currView);
                            mark = true;
                        }
                    }
                    if(event.getClickCount() == 2) {
                        mark = false;
                        Main.fileSystem.open(dir);
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
            { tiles.getChildren().add(view[i]); }
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
                case "png":
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
        pasteItem.setOnAction(event1 -> { Main.fileSystem.paste(); refresh(); });
        propertiesItem.setOnAction(event1 -> { propertyDialog(Main.fileSystem.getCurrentFolder()); });
        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        SeparatorMenuItem separatorMenuItem2 = new SeparatorMenuItem();
        rightClickMenu4Tiles.getItems().addAll(newFolderItem, newFileItem, separatorMenuItem1, pasteItem, separatorMenuItem2, propertiesItem);
        if(event.getButton().equals(MouseButton.SECONDARY)
                && (!event.getTarget().toString().contains("label") && !event.getTarget().toString().contains("Label"))) {
            mark = false;
            Main.fileSystem.select(null, null);
            rightClickMenu4Tiles.show(explorer, event.getScreenX(), event.getScreenY());
        } else if (event.getButton().equals(MouseButton.PRIMARY)
                && (!event.getTarget().toString().contains("label") && !event.getTarget().toString().contains("Label"))) {
            refresh();
            mark=false;
            Main.fileSystem.select(null, null);
            rightClickMenu4Tiles.hide();
        } else {
            rightClickMenu4Tiles.hide();
        }
    }

    private ContextMenu dirRightClickMenu(Directory dir) {
        ContextMenu rightClickMenu = new ContextMenu();
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(e -> {
            Main.fileSystem.open(dir);
            if (dir instanceof Folder)
                refresh();
        });

        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e -> Main.fileSystem.copy(dir));

        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setOnAction(e -> Main.fileSystem.cut(dir));

        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction(event1 -> renameDir(dir));

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> {
            Main.fileSystem.delete(dir);
            refresh();
        });

        MenuItem propertiesItem = new MenuItem("Properties");
        propertiesItem.setOnAction(event -> propertyDialog(dir));

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
        result.ifPresent(name -> Main.fileSystem.newFile(name, "txt", "r/w"));
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
        result.ifPresent(name -> Main.fileSystem.newFolder(name));
        refresh();
    }

    private void renameDir(Directory dir) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename");
        dialog.setContentText("New name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> Main.fileSystem.rename(dir, name));
        refresh();
    }

    public void propertyDialog(Directory dir){
        Stage propertyStage = new Stage();
        propertyStage.setTitle("Properties");
        propertyStage.setResizable(false);

        GridPane root= new GridPane();
        root.setHgap(10);
        root.setVgap(5);

        Label nameLabel = new Label();
        setIcon(dir, nameLabel);
        TextField name =  new TextField(dir.getName());
        name.setEditable(false);
        root.add(nameLabel, 1, 1);
        root.add(name, 2, 1);

        Label typeLabel = new Label("Type: ");
        TextField type=new TextField();
        type.setEditable(false);
        root.add(typeLabel, 1, 2);
        root.add(type, 2, 2);

        System.out.println(dir.getRealPath());
        Label sizeLabel = new Label("Size: ");
        TextField size =  new TextField(dir.getSize() + "");
        size.setEditable(false);
        root.add(sizeLabel, 1, 3);
        root.add(size, 2, 3);

        Label locationLabel = new Label("Location: ");
        TextField location = new TextField(dir.getParent().getPath());
        location.setEditable(false);
        root.add(locationLabel, 1, 4);
        root.add(location, 2, 4);

        Label permissionLabel = new Label("Permission: ");
        TextField permission = new TextField();
        permission.setEditable(false);

        Label children = new Label("Contains: ");
        TextField contains = new TextField();
        contains.setEditable(false);


        if (dir  instanceof Folder) {
            type.setText("Folder");
            contains.setText(((Folder) dir).getChildren().size() + "");
            root.add(children, 1, 5);
            root.add(contains, 2, 5);
        } else {
            type.setText(((File) dir).getExtension());
            permission.setText(((File) dir).getPermission());
            root.add(permissionLabel, 1, 5);
            root.add(permission, 2, 5);
        }


        propertyStage.setScene(new Scene(root,290,300));
        propertyStage.show();
    }


}