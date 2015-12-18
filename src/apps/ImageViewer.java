package apps;


import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ImageViewer {

    public ImageViewer(String fileUrl) {
        System.out.println(fileUrl);

        Stage stage = new Stage();
        stage.setTitle("Image Viewer");

        StackPane sp = new StackPane();
        ImageView imgView = new ImageView(new Image(fileUrl));
        sp.getChildren().add(imgView);


        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setContent(sp);

        stage.setScene(new Scene(scroll));
        stage.show();
    }

}
