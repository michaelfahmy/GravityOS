package apps;
/**
 * Created by Michael Fahmy on 12/12/2015.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaFXMediaPlayer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Media Player");
        primaryStage.setScene(new SceneGenerator().createScene());
        primaryStage.show();
    }

}

class SceneGenerator {
    final Label currentlyPlaying = new Label();
    final ProgressBar progress = new ProgressBar();
    private ChangeListener<Duration> progressChangeListener;

    public Scene createScene() {
        final BorderPane layout = new BorderPane();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File("D:\\OpenSourceCode\\JavaFXMusicPlayer"));
        // determine the source directory for the playlist
        final File dir = chooser.showDialog(null);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Cannot find video source directory: " + dir);
            Platform.exit();
            return null;
        }

        // create some media players.
        final List<MediaPlayer> players = new ArrayList<MediaPlayer>();
        for (String file : dir.list((dir1, name) -> name.endsWith(".mp4") || name.endsWith(".mp3"))) {
            players.add(createPlayer("file:///" + (dir + "\\" + file).replace("\\", "/").replaceAll(" ", "%20")));
        }
        if (players.isEmpty()) {
            System.out.println("No audio found in " + dir);
            Platform.exit();
            return null;
        }

        // create a view to show the mediaplayers.
        final MediaView mediaView = new MediaView(players.get(0));

        final Button skip = new Button("Skip");
        final Button play = new Button("Pause");

        // play each audio file in turn.
        for (int i = 0; i < players.size(); i++) {
            final MediaPlayer player     = players.get(i);
            final MediaPlayer nextPlayer = players.get((i + 1) % players.size());
            player.setOnEndOfMedia(() -> {
                player.currentTimeProperty().removeListener(progressChangeListener);
                mediaView.setMediaPlayer(nextPlayer);
                nextPlayer.play();
            });
        }

        // allow the user to skip a track.
        skip.setOnAction(actionEvent -> {
            final MediaPlayer curPlayer = mediaView.getMediaPlayer();
            MediaPlayer nextPlayer = players.get((players.indexOf(curPlayer) + 1) % players.size());
            mediaView.setMediaPlayer(nextPlayer);
            curPlayer.currentTimeProperty().removeListener(progressChangeListener);
            curPlayer.stop();
            nextPlayer.play();
        });

        // allow the user to play or pause a track.
        play.setOnAction(actionEvent -> {
            if ("Pause".equals(play.getText())) {
                mediaView.getMediaPlayer().pause();
                play.setText("Play");
            } else {
                mediaView.getMediaPlayer().play();
                play.setText("Pause");
            }
        });

        // display the name of the currently playing track.
        mediaView.mediaPlayerProperty().addListener((observableValue, oldPlayer, newPlayer) -> {
            setCurrentlyPlaying(newPlayer);
        });

        // start playing the first track.
        mediaView.setMediaPlayer(players.get(0));
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
        HBox bottom = new HBox(10, skip, play, progress);
        bottom.setAlignment(Pos.CENTER);

        layout.setTop(top);
        layout.setCenter(mediaView);
        layout.setBottom(bottom);

//        layout.getChildren().addAll(
//                invisiblePause,
//                VBoxBuilder.create().spacing(10).alignment(Pos.CENTER).children(
//                        currentlyPlaying,
//                        mediaView,
//                        VBoxBuilder.create().spacing(10).alignment(Pos.CENTER).children(skip, play, progress)
//                ).build()
//        );

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

    /** @return a MediaPlayer for the given source which will report any errors it encounters */
    private MediaPlayer createPlayer(String aMediaSrc) {
        System.out.println("Creating player for: " + aMediaSrc);
        final MediaPlayer player = new MediaPlayer(new Media(aMediaSrc));
        player.setOnError(() -> System.out.println("Media error occurred: " + player.getError()));
        return player;
    }
}