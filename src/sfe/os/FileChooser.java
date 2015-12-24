package sfe.os;

import apps.Memo;
import directory.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class FileChooser {

    private Stage stage;
    private BorderPane explorer;
    private Button back, action;
    private String SAVE = "save", operation = SAVE, realPath, fileExtension;
    TextField fileName = new TextField();
    HBox hBox;
    static  CPU cpu;
    public FileChooser(String fileExtension, String realPath, String operation,CPU cpu) {
        this.cpu = cpu;
        this.operation = operation;
        this.realPath = realPath;
        this.fileExtension = fileExtension;
        Main.fileSystem.goRoot();
        InitializeFileChooser();
    }

    void InitializeFileChooser() {
        stage = new Stage();
        stage.setTitle("FileChooser");
        stage.setHeight(500);
        stage.setWidth(750);
        explorer = new BorderPane();
        action = new Button(operation.equals(SAVE) ? "Save" : "Open");
        if(!fileName.getText().isEmpty()) { action.setDisable(false); }
        action.setOnAction(event -> {
            if(operation.equals(SAVE)) {
                directory.File fle = new directory.File(fileName.getText().trim(), "txt", Main.fileSystem.getCurrentFolder().getPath() + "/" + fileName, Main.fileSystem.getCurrentFolder(), "r/w");
                fle.setRealPath(realPath);
                Memo.chosenFile = fle;
                Main.fileSystem.getCurrentFolder().getChildren().add(fle);
                Main.fileSystem.store();
                Main.fileSystem.goRoot();
            }else {
                Main.fileSystem.open(Main.fileSystem.getSelected());
            }
            stage.close();
        });
        back = new Button(null, new ImageView("res/ExplorerIcons/icon-back.png"));
        back.setDisable(true);
        back.setOnAction(event -> {
            Main.fileSystem.back();
            refresh();
            back.setDisable(false);
            if(Main.fileSystem.getCurrentFolder() == Main.fileSystem.getRoot()) {
                back.setDisable(true);
            }
        });

        explorer.setTop(back);
        hBox = new HBox();
        fileName.setPrefWidth(stage.getWidth() - 150);
        hBox.setPadding(new Insets(7));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(new HBox(new Label("  Name:\t"), fileName), action);
        if(operation.equals(SAVE))
            explorer.setBottom(hBox);
        refresh();
        stage.setScene(new Scene(explorer));
        stage.show();
    }

    private void refresh() {
        TilePane tiles = new TilePane();
        tiles.setPrefColumns(8);
        tiles.setHgap(25);
        tiles.setVgap(30);
        tiles.setPadding(new Insets(20));
        tiles.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        populateTiles(tiles);
        explorer.setCenter(tiles);
    }

    private void populateTiles(TilePane tiles) {
        Label view[] = new Label[Main.fileSystem.getCurrentFolder().getChildren().size()];
        for (int i = 0; i < view.length; i++) {
            directory.Directory dir = Main.fileSystem.getCurrentFolder().getChildren().get(i);
            view[i] = new Label(dir.getName());
            view[i].setContentDisplay(ContentDisplay.TOP);
            view[i].setPadding(new Insets(0, 5, 0, 5));
            view[i].setGraphicTextGap(1);
            view[i].setWrapText(true);
            setIcon(dir, view[i]);
            view[i].setOnMouseClicked(event -> {
                if(event.getButton().equals(MouseButton.PRIMARY)) {
                    if(event.getClickCount() == 2) {
                        Main.fileSystem.open(dir);
                        if (dir instanceof directory.Folder) {
                            back.setDisable(false);
                            refresh();
                        } else {
                            stage.close();
                        }

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
            if(dir instanceof Folder || (((File) dir).getExtension().equals(this.fileExtension) || (this.fileExtension.equals("mp3") && ((File) dir).getExtension().equals("mp4"))))
                tiles.getChildren().add(view[i]);
        }
    }

    private void setIcon(directory.Directory dir, Label view) {
        if (dir instanceof directory.Folder) {
            view.setGraphic(new ImageView("res/ExplorerIcons/folder.png"));
        } else {
            directory.File file = (directory.File) dir;
            switch (file.getExtension()) {
                case "txt":
                    view.setGraphic(new ImageView("res/ExplorerIcons/txt.png"));
                    break;
                case "jpg":
                    view.setGraphic(new ImageView("res/ExplorerIcons/jpg.png"));
                    break;
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
}