package sfe.os;

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

    public FileChooser(Directory tobe) {
        stage = new Stage();
        stage.setTitle("FileChooser");
        stage.setHeight(500);
        stage.setWidth(750);
        explorer = new BorderPane();
        fileSystem = new FileSystem();

        refresh(tobe);

        stage.setScene(new Scene(explorer));
        stage.show();
    }

    private void refresh(Directory tobe) {
        TilePane tiles = new TilePane();
        tiles.setPrefColumns(8);
        tiles.setHgap(25);
        tiles.setVgap(30);
        tiles.setPadding(new Insets(20));
        tiles.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        populateTiles(tiles, tobe);

        explorer.setCenter(tiles);
    }

    private void populateTiles(TilePane tiles, Directory tobe) {
        Label view[] = new Label[fileSystem.getCurrentFolder().getChildren().size()];
        for (int i = 0; i < view.length; i++) {
            Directory dir = fileSystem.getCurrentFolder().getChildren().get(i);
            view[i] = new Label(dir.name);
            view[i].setContentDisplay(ContentDisplay.TOP);
            view[i].setPadding(new Insets(0, 5, 0, 5));
            view[i].setGraphicTextGap(1);
            view[i].setWrapText(true);
            setIcon(dir, view[i]);
            view[i].setOnMouseClicked(event -> {
                if(event.getButton().equals(MouseButton.PRIMARY)) {
                    if(event.getClickCount() == 2) {
                        if (dir instanceof Folder) {
                            fileSystem.open(dir);
                            refresh(tobe);
                        } else {
                            selectedFilePath = dir.getRealPath();
                            tobe.setRealPath(selectedFilePath);
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

            if(dir.parent == null && dir.isHidden) { tiles.getChildren().add(view[i]); }
            else if (dir.parent != null){ tiles.getChildren().add(view[i]); }
        }
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

    public String getSelectedFilePath() {
        return selectedFilePath;
    }
}
