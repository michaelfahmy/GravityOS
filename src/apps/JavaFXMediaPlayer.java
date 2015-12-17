package apps;


import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JavaFXMediaPlayer {

    public JavaFXMediaPlayer(String fileUrl) {
        Stage stage = new Stage();
        stage.setTitle("Media Player");
        stage.setScene(new SceneGenerator().createScene(fileUrl));
        stage.show();
    }

}

class SceneGenerator {
    Label currentlyPlaying = new Label();
    ProgressBar progress = new ProgressBar();
    ChangeListener<Duration> progressChangeListener;

    public Scene createScene(String fileUrl) {

        BorderPane layout = new BorderPane();

        MediaPlayer player = new MediaPlayer(new Media(fileUrl));

        MediaView mediaView = new MediaView(player);

        Button play = new Button("Pause");
        play.setOnAction(actionEvent -> {
            if ("Pause".equals(play.getText())) {
                mediaView.getMediaPlayer().pause();
                play.setText("Play");
            } else {
                mediaView.getMediaPlayer().play();
                play.setText("Pause");
            }
        });

        // start playing the track.
        mediaView.getMediaPlayer().play();
        setCurrentlyPlaying(mediaView.getMediaPlayer());

        // silly invisible button used as a template to get the actual preferred size of the Pause button.
        Button invisiblePause = new Button("Pause");
        invisiblePause.setVisible(false);
        play.prefHeightProperty().bind(invisiblePause.heightProperty());
        play.prefWidthProperty().bind(invisiblePause.widthProperty());

        // layout the scene.
        layout.setStyle("-fx-background-color: cornsilk; -fx-font-size: 20; -fx-padding: 20; -fx-alignment: center;");

        VBox top = new VBox(10, invisiblePause, currentlyPlaying);
        top.setAlignment(Pos.CENTER);
        HBox bottom = new HBox(10, play, progress);
        bottom.setAlignment(Pos.CENTER);

        layout.setTop(top);
        layout.setCenter(mediaView);
        layout.setBottom(bottom);

        progress.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(progress, Priority.ALWAYS);
        return new Scene(layout, 900, 600);
    }

    /** sets the currently playing label to the label of the new media player and updates the progress monitor. */
    private void setCurrentlyPlaying(final MediaPlayer newPlayer) {
        progress.setProgress(0);
        progressChangeListener = (observableValue, oldValue, newValue) ->
                progress.setProgress(1.0 * newPlayer.getCurrentTime().toMillis() / newPlayer.getTotalDuration().toMillis());

        newPlayer.currentTimeProperty().addListener(progressChangeListener);

        String source = newPlayer.getMedia().getSource();
        source = source.substring(0, source.length() - ".mp4".length());
        source = source.substring(source.lastIndexOf("/") + 1).replaceAll("%20", " ");
        currentlyPlaying.setText("Now Playing: " + source);
    }
}