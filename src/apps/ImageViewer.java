package apps;
// Created by OMAR
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ImageViewer {
    private double angles[] = {90, 180, 270, 360};
    private int currentAngle = 0;
    Image img;
    private double height, width;
    ScrollPane scroll = new ScrollPane();
    ImageView imgView = new ImageView();
    public ImageViewer(String fileUrl) {
        System.out.println(fileUrl);
        img = new Image(fileUrl);
        height = img.getHeight();
        width = img.getWidth();
        Stage stage = new Stage();
        stage.setTitle("Image Viewer");
        imgView = new ImageView(img);
        BorderPane border = new BorderPane();
        border.setCenter(imgView);
        HBox hbox = addHBox();
        border.setBottom(hbox);
        imgView.setPreserveRatio(true);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setContent(border);
        stage.setScene(new Scene(scroll));
        stage.show();
    }
    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        Image imageZoomIn = new Image("res/zoomIn.png");
        Button buttonZoomIn = new Button("", new ImageView(imageZoomIn));
        Image imageZoomOut = new Image("res/zoomOut.png");
        Button buttonZoomOut = new Button("", new ImageView(imageZoomOut));
        Image imageRotateL = new Image("res/rotateR.png");
        Button buttonRotateL = new Button("", new ImageView(imageRotateL));
        Image imageRotateR = new Image("res/rotateL.png");
        Button buttonRotateR = new Button("", new ImageView(imageRotateR));
        hbox.getChildren().addAll(buttonZoomIn,buttonZoomOut, buttonRotateL,buttonRotateR);
        buttonZoomIn.setOnAction(event -> {
            height *= 1.5;
            width *= 1.2;
            imgView.setFitHeight(height);
            imgView.setFitWidth(width);
        });
        buttonZoomOut.setOnAction(event -> {
            height /= 1.5;
            width /= 1.2;
            imgView.setFitHeight(height);
            imgView.setFitWidth(width);
        });
        buttonRotateL.setOnAction(event -> {
            imgView.setRotate(angles[currentAngle]);
            currentAngle = (currentAngle + 1) % 4;
        });
        buttonRotateR.setOnAction(event -> {
            currentAngle = currentAngle == 0 ? 3 : currentAngle - 1;
            imgView.setRotate(angles[currentAngle]);
        });
        return hbox;
    }

}