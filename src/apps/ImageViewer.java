package apps;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sfe.os.CPU;
import sfe.os.FileChooser;

import java.io.File;

public class ImageViewer {
    private double angles[] = {90, 180, 270, 360};
    private int currentAngle = 0;
    private double height;
    int id;
    Stage stage;
    ImageView imgView = new ImageView();
    static CPU cpu;
    public ImageViewer(File fileUrl,int id ,CPU cpu) {
        this.cpu=cpu;
        stage = new Stage();
        stage.setTitle("Image Viewer");
        this.id=id;
        BorderPane border = new BorderPane();
        border.setTop(menuBar());
        border.setCenter(viewer(fileUrl));
        border.setBottom(controlsBar());

        stage.setScene(new Scene(border));
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Image Viewer with id :"+id+" Is removed");
                cpu.RemoveProcess(id);
            }
        });
    }

    public MenuBar menuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));

        Menu fileMenu = new Menu("File");
        {
            MenuItem open = new MenuItem("Open...");
            open.setOnAction(event -> { new FileChooser("jpg", "", "open",cpu); stage.close(); } );
            MenuItem close = new MenuItem("Exit");
            close.setOnAction(event -> stage.close());
            fileMenu.getItems().addAll(open, close);
        }

        menuBar.getMenus().addAll(fileMenu);
        return menuBar;
    }

    public ScrollPane viewer(File fileUrl) {

        VBox zoomControls = new VBox();
        Label buttonZoomIn = new Label(null, new ImageView(new Image("res/ImageViewerIcons/zoomIn.png")));
        Label buttonZoomOut = new Label(null, new ImageView(new Image("res/ImageViewerIcons/zoomOut.png")));
        buttonZoomIn.setOpacity(0.3);
        buttonZoomOut.setOpacity(0.3);

        buttonZoomIn.setOnMouseEntered(event1 -> buttonZoomIn.setOpacity(0.5));
        buttonZoomIn.setOnMouseExited(event1 -> buttonZoomIn.setOpacity(0.3));
        buttonZoomOut.setOnMouseEntered(event1 -> buttonZoomOut.setOpacity(0.5));
        buttonZoomOut.setOnMouseExited(event1 -> buttonZoomOut.setOpacity(0.3));

        buttonZoomIn.setOnMouseClicked(event -> {
            height *= 1.5;
            imgView.setFitHeight(height);
        });
        buttonZoomOut.setOnMouseClicked(event -> {
            height /= 1.5;
            imgView.setFitHeight(height);
        });

        zoomControls.getChildren().addAll(buttonZoomIn, buttonZoomOut);
        zoomControls.setPickOnBounds(true);
        zoomControls.setAlignment(Pos.TOP_LEFT);
        zoomControls.setPadding(new Insets(10));
        if (fileUrl != null) {
            Image img = new Image(fileUrl.toURI().toString());
            height = img.getHeight();
            imgView = new ImageView(img);
        } else {
            imgView = new ImageView();
            imgView.setFitHeight(300);
            imgView.setFitWidth(400);
        }
        imgView.setPreserveRatio(true);
        imgView.setPickOnBounds(true);
        imgView.autosize();

        StackPane pane = new StackPane();
        pane.getChildren().addAll(imgView, zoomControls);
        pane.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setContent(pane);

        return scroll;
    }

    public HBox controlsBar() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(3, 10, 3, 10));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));

        Label buttonRotateL = new Label(null, new ImageView(new Image("res/ImageViewerIcons/rotateL.png")));
        Label buttonRotateR = new Label(null, new ImageView(new Image("res/ImageViewerIcons/rotateR.png")));
        Label fullScreen = new Label(null, new ImageView(new Image("res/ImageViewerIcons/fullScreen.png")));

        fullScreen.setOnMouseEntered(event2 -> fullScreen.setEffect(new Glow(3)));
        fullScreen.setOnMouseExited(event2 -> fullScreen.setEffect(null));

        buttonRotateR.setOnMouseEntered(event2 -> buttonRotateR.setEffect(new Glow(5)));
        buttonRotateR.setOnMouseExited(event2 -> buttonRotateR.setEffect(null));

        buttonRotateL.setOnMouseEntered(event2 -> buttonRotateL.setEffect(new Glow(5)));
        buttonRotateL.setOnMouseExited(event2 -> buttonRotateL.setEffect(null));

        fullScreen.setOnMouseClicked(event1 -> {
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
        });

        buttonRotateR.setOnMouseClicked(event -> {
            imgView.setRotate(angles[currentAngle]);
            currentAngle = (currentAngle + 1) % 4;
        });
        buttonRotateL.setOnMouseClicked(event -> {
            currentAngle = currentAngle == 0 ? 3 : currentAngle - 1;
            imgView.setRotate(angles[currentAngle]);
        });

        hbox.getChildren().addAll(buttonRotateL, fullScreen, buttonRotateR);

        return hbox;
    }

}