package apps;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ImageViewer {
    private double angles[] = {90, 180, 270, 360};
    private int currentAngle = 0;
    private double height;

    Stage stage;
    ImageView imgView = new ImageView();

    public ImageViewer(String fileUrl) {
        stage = new Stage();
        stage.setTitle("Image Viewer");

        Image img = new Image(fileUrl);
        height = img.getHeight();

        imgView = new ImageView(img);
        imgView.setPreserveRatio(true);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setContent(imgView);

        BorderPane border = new BorderPane();
        border.setTop(menuBar());
        border.setCenter(scroll);
        border.setBottom(controlsBar());

        stage.setScene(new Scene(border));
        stage.show();
    }

    public MenuBar menuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        {
            MenuItem newFileMenu = new MenuItem("Open...");
            newFileMenu.setOnAction(event -> { /* open file chooser for image files */ } );
            MenuItem close = new MenuItem("Exit");
            close.setOnAction(event -> stage.close());
            fileMenu.getItems().addAll(newFileMenu, close);
        }

        menuBar.getMenus().addAll(fileMenu);
        return menuBar;
    }

    public HBox controlsBar() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);

        Button buttonZoomIn = new Button(null, new ImageView(new Image("res/ImageViewerIcons/zoomIn.png")));
        Button buttonZoomOut = new Button(null, new ImageView(new Image("res/ImageViewerIcons/zoomOut.png")));
        Button buttonRotateL = new Button(null, new ImageView(new Image("res/ImageViewerIcons/rotateL.png")));
        Button buttonRotateR = new Button(null, new ImageView(new Image("res/ImageViewerIcons/rotateR.png")));
        Button fullScreen = new Button("full screen");

        fullScreen.setOnAction(event1 -> {
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
        });
        buttonZoomIn.setOnAction(event -> {
            height *= 1.5;
            imgView.setFitHeight(height);
        });
        buttonZoomOut.setOnAction(event -> {
            height /= 1.5;
            imgView.setFitHeight(height);
        });
        buttonRotateL.setOnAction(event -> {
            imgView.setRotate(angles[currentAngle]);
            currentAngle = (currentAngle + 1) % 4;
        });
        buttonRotateR.setOnAction(event -> {
            currentAngle = currentAngle == 0 ? 3 : currentAngle - 1;
            imgView.setRotate(angles[currentAngle]);
        });

        hbox.getChildren().addAll(buttonRotateL, buttonZoomIn, fullScreen,buttonZoomOut, buttonRotateR);

        return hbox;
    }

}