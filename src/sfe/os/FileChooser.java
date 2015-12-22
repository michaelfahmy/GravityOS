package sfe.os;

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

    private FileSystem fileSystem;
    private Stage stage;
    private BorderPane explorer;
    private String selectedFilePath;
    private Button back, action;
    private String SAVE = "save", OPEN = "open", operation = SAVE, realPath;
    TextField fileName = new TextField();
    ComboBox<String> fileTypes;
    VBox vBox;
    HBox hBox;

    public FileChooser(String realPath, String operation) {
        this.operation = operation;
        this.realPath = realPath;
        InitializeFileChooser();
    }

    void InitializeFileChooser() {
        stage = new Stage();
        stage.setTitle("FileChooser");
        stage.setHeight(500);
        stage.setWidth(750);
        explorer = new BorderPane();
        fileSystem = new FileSystem();
        action = new Button(operation.equals(SAVE) ? "Save" : "Open");
        if(!fileName.getText().isEmpty()) { action.setDisable(false); }
        action.setOnAction(event -> {
            if(operation.equals(SAVE)) {
                directory.File fle = new directory.File(fileName.getText().trim(), fileTypes.getValue(), fileSystem.getCurrentFolder().getPath() + "/" + fileName, fileSystem.getCurrentFolder(), fileTypes.getValue().equals("txt") ? "r/w" : "r");
                fle.setRealPath(realPath);
                fileSystem.getCurrentFolder().getChildren().add(fle);
            }else {
                fileSystem.open(fileSystem.getSelected());
            }
            stage.close();
        });
        back = new Button(null, new ImageView("res/icon-back.png"));
        back.setDisable(true);
        back.setOnAction(event -> {
            fileSystem.back();
            refresh();
            back.setDisable(false);
            if(fileSystem.getCurrentFolder() == fileSystem.getRoot()) {
                back.setDisable(true);
            }
        });

        explorer.setTop(back);
        vBox = new VBox();
        hBox = new HBox();
        fileTypes = new ComboBox<>();
        fileTypes.getItems().addAll("txt", "jpg", "mp3", "mp4", "pdf", "html");
        fileTypes.setValue("txt");
        fileName.setPrefWidth(stage.getWidth() - 150);
        fileTypes.setPrefWidth(stage.getWidth() - 150);
        vBox.getChildren().addAll(new HBox(new Label("  Name:\t"), fileName), new HBox(new Label("  Type:\t"), fileTypes));
        hBox.getChildren().addAll(vBox, action);
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
        Label view[] = new Label[fileSystem.getCurrentFolder().getChildren().size()];
        for (int i = 0; i < view.length; i++) {
            directory.Directory dir = fileSystem.getCurrentFolder().getChildren().get(i);
            view[i] = new Label(dir.getName());
            view[i].setContentDisplay(ContentDisplay.TOP);
            view[i].setPadding(new Insets(0, 5, 0, 5));
            view[i].setGraphicTextGap(1);
            view[i].setWrapText(true);
            setIcon(dir, view[i]);
            view[i].setOnMouseClicked(event -> {
                if(event.getButton().equals(MouseButton.PRIMARY)) {
                    if(event.getClickCount() == 2) {
                        fileSystem.open(dir);
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
            if(operation.equals(SAVE)) {
                if (dir.isHidden() && dir.getParent() == fileSystem.getRoot()) {
                    tiles.getChildren().add(view[i]);
                } else if (dir.getParent() != fileSystem.getRoot()) {
                    tiles.getChildren().add(view[i]);
                }
            }else {
                if(!dir.isHidden()) {
                    tiles.getChildren().add(view[i]);
                }
            }
        }
    }

    private void setIcon(directory.Directory dir, Label view) {
        if (dir instanceof directory.Folder) {
            view.setGraphic(new ImageView("res/folder.png"));
        } else {
            directory.File file = (directory.File) dir;
            switch (file.getExtension()) {
                case "txt":
                    view.setGraphic(new ImageView("res/txt.png"));
                    break;
                case "jpg":
                    view.setGraphic(new ImageView("res/jpg.png"));
                    break;
                case "png":
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
}
