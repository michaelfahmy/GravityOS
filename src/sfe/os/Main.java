package sfe.os;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Desktop");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setScene(desktopScene());
        primaryStage.show();
    }

    private Scene desktopScene() {
        StackPane desktop = new StackPane();

        Label myPC = new Label("My PC", new ImageView("res/mypc.png"));
        myPC.setContentDisplay(ContentDisplay.TOP);
        myPC.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                if(event.getClickCount() == 2) {
                    new Explorer();
                }
            }
        });

        desktop.getChildren().addAll(myPC);

        return new Scene(desktop);
    }


}