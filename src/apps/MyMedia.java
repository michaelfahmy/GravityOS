package apps;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/** Example of playing all mp3 audio files in a given directory 
 * using a JavaFX MediaView launched from Swing 
 */
public class MyMedia {

    private String url;
    private MediaView mediaView;

    public MyMedia(String url){
        initAndShowGUI();
        this.url = url;
    }

    private void initAndShowGUI() {
        // This method is invoked on Swing thread
        JFrame frame = new JFrame("FX");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setBounds(200, 100, 800, 250);
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    this.finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        };
        frame.addWindowListener(exitListener);
        frame.setVisible(true);

        Platform.runLater(new Runnable() {
            @Override public void run() {
                initFX(fxPanel);
            }
        });
    }

    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on JavaFX thread
        Scene scene = new SceneGenerator2().createScene(url);
        fxPanel.setScene(scene);
    }

    class SceneGenerator2 {
        final Label currentlyPlaying = new Label();
        final ProgressBar progress = new ProgressBar();
        private ChangeListener<Duration> progressChangeListener;

        public Scene createScene(String url) {
            final StackPane layout = new StackPane();

            //dummy data to check the player status
            // url = "/media/ahmadz/AhMeDz/SafeZone/Intellij/OSProject/src/res/files/vid.mp4";
            // url = "/home/michael/song.mp3";

            // determine the source directory
            final File dir = new File(url.replace("file:", ""));
            System.out.println(dir);

            //System.out.println(!dir.isFile() + " " + !dir.exists() + " " + !dir.getPath().endsWith(".mp3") + " " + !dir.getPath().endsWith(".mp4"));
            if (!dir.isFile() || !dir.exists() ||  !(dir.getPath().endsWith(".mp3") || dir.getPath().endsWith(".mp4"))) {
                System.out.println("8alat keda");
                System.exit(0);
            }
            // create some media players.
            final List<MediaPlayer> players = new ArrayList<>();
            try {
                URI U = new URI(url);
                players.add(createPlayer(U.toString()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }


            // create a view to show the mediaplayers.
            mediaView = new MediaView(players.get(0));
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
            skip.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    final MediaPlayer curPlayer = mediaView.getMediaPlayer();
                    MediaPlayer nextPlayer = players.get((players.indexOf(curPlayer) + 1) % players.size());
                    mediaView.setMediaPlayer(nextPlayer);
                    curPlayer.currentTimeProperty().removeListener(progressChangeListener);
                    curPlayer.stop();
                    nextPlayer.play();
                }
            });

            // allow the user to play or pause a track.
            play.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    if ("Pause".equals(play.getText())) {
                        mediaView.getMediaPlayer().pause();
                        play.setText("Play");
                    } else {
                        mediaView.getMediaPlayer().play();
                        play.setText("Pause");
                    }
                }
            });

            // display the name of the currently playing track.
            mediaView.mediaPlayerProperty().addListener(new ChangeListener<MediaPlayer>() {
                @Override public void changed(ObservableValue<? extends MediaPlayer> observableValue, MediaPlayer oldPlayer, MediaPlayer newPlayer) {
                    setCurrentlyPlaying(newPlayer);
                }
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
            layout.getChildren().addAll(
                    invisiblePause,
                    VBoxBuilder.create().spacing(10).alignment(Pos.CENTER).children(
                            currentlyPlaying,
                            mediaView,
                            HBoxBuilder.create().spacing(10).alignment(Pos.CENTER).children(skip, play, progress).build()
                    ).build()
            );
            progress.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(progress, Priority.ALWAYS);
            return new Scene(layout, 800, 600);
        }

        /** sets the currently playing label to the label of the new media player and updates the progress monitor. */
        private void setCurrentlyPlaying(final MediaPlayer newPlayer) {
            progress.setProgress(0);
            progressChangeListener = new ChangeListener<Duration>() {
                @Override public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                    progress.setProgress(1.0 * newPlayer.getCurrentTime().toMillis() / newPlayer.getTotalDuration().toMillis());
                }
            };
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
            player.setOnError(new Runnable() {
                @Override public void run() {
                    System.out.println("Media error occurred: " + player.getError());
                }
            });
            return player;
        }
    }
}

