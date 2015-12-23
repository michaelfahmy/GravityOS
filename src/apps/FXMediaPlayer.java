package apps;


import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import sfe.os.FileChooser;

import java.io.File;

public class FXMediaPlayer {

    private Stage stage;
    private MediaView mediaView;
    private Label currentlyPlaying = new Label();
    private ProgressBar progress = new ProgressBar();
    private ChangeListener<Duration> progressChangeListener;
    boolean playing = false;
    private File f = null;
    private String type = "mp3";

    public FXMediaPlayer(File file) {
        stage = new Stage();
        stage.setTitle("Media Player");

        if (file != null) {
            System.out.println(file.getPath());
            if (!file.isFile() || !file.exists() || !(file.getPath().endsWith(".mp3") || file.getPath().endsWith(".mp4"))) {
                System.out.println("3\'alat keda");
                stage.close();
            }
            this.f = file;
            this.type = f.getName().substring(f.getName().length() - 3, f.getName().length());
        }

        stage.setScene(createScene());
        stage.setOnCloseRequest(event -> {
            stage.close();
            if (mediaView != null) {
                mediaView.getMediaPlayer().stop();
                mediaView.getMediaPlayer().dispose();
            }
        });
        stage.show();
    }

    public Scene createScene() {
        BorderPane border = new BorderPane();
        border.setTop(menuBar());
        if (f != null)
            border.setCenter(mediaScene());
        border.setBottom(controlsBar());
        if (type.equals("mp3"))
            return new Scene(border, 300, 150);
        else
            return new Scene(border, 720, 480);
    }

    public MenuBar menuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem open = new MenuItem("Open...");
        open.setOnAction(event -> {
            new FileChooser("", "open");
            stage.close();
        } );
        MenuItem close = new MenuItem("Exit");
        close.setOnAction(event -> stage.close());
        fileMenu.getItems().addAll(open, close);

        menuBar.getMenus().addAll(fileMenu);
        return menuBar;
    }

    private MediaView mediaScene() {
        mediaView = new MediaView(new MediaPlayer(new Media(f.getPath().replace(" ","%20"))));
        mediaView.getMediaPlayer().setOnError(() -> System.out.println("Media error occurred: " + mediaView.getMediaPlayer().getError()));
        mediaView.getMediaPlayer().play();
        setCurrentlyPlaying(mediaView.getMediaPlayer());
        return mediaView;
    }

    private void setCurrentlyPlaying(final MediaPlayer newPlayer) {
        progress.setProgress(0);
        progressChangeListener = (observableValue, oldValue, newValue) -> progress.setProgress(1.0 * newPlayer.getCurrentTime().toMillis() / newPlayer.getTotalDuration().toMillis());
        newPlayer.currentTimeProperty().addListener(progressChangeListener);

        String source = newPlayer.getMedia().getSource();
        source = source.substring(0, source.length() - ".mp4".length());
        source = source.substring(source.lastIndexOf("/") + 1).replaceAll("%20", " ");
        currentlyPlaying.setText("Now Playing: " + source);
    }

    public VBox controlsBar() {

        VBox vBox = new VBox();
        HBox barControl = new HBox();
        HBox controls = new HBox();

        vBox.setPadding(new Insets(3, 10, 3, 10));
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);

        currentlyPlaying = new Label();
        currentlyPlaying.setAlignment(Pos.CENTER);

        Label stop = new Label(null, new ImageView(new Image("res/MediaIcons/stop.png")));
        Label play = new Label(null, new ImageView(new Image("res/MediaIcons/pause.png")));
        Label forward = new Label(null, new ImageView(new Image("res/MediaIcons/forward.png")));
        Label backward = new Label(null, new ImageView(new Image("res/MediaIcons/backward.png")));

        {
            stop.setOnMouseEntered(event2 -> stop.setEffect(new Glow(0.2)));
            stop.setOnMouseExited(event2 -> stop.setEffect(null));
            stop.setOnMouseClicked(event -> {
                play.setEffect(new DropShadow());
                mediaView.getMediaPlayer().stop();
                play.setGraphic(new ImageView("res/MediaIcons/play.png"));
                stop.setDisable(true);
                playing = false;
            });

            progress = new ProgressBar();
            progress.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(progress, Priority.ALWAYS);

            barControl.setAlignment(Pos.CENTER);
            barControl.setSpacing(10);
            barControl.getChildren().addAll(stop, progress);
        }

        {
            play.setOnMouseEntered(event2 -> play.setEffect(new Glow(0.2)));
            play.setOnMouseExited(event2 -> play.setEffect(null));

            forward.setOnMouseEntered(event2 -> forward.setEffect(new Glow(0.2)));
            forward.setOnMouseExited(event2 -> forward.setEffect(null));

            backward.setOnMouseEntered(event2 -> backward.setEffect(new Glow(0.2)));
            backward.setOnMouseExited(event2 -> backward.setEffect(null));

            play.setOnMouseClicked(event1 -> {
                play.setEffect(new DropShadow());
                if (!playing) {
                    mediaView.getMediaPlayer().play();
                    play.setGraphic(new ImageView("res/MediaIcons/pause.png"));
                    stop.setDisable(false);
                    playing = true;
                } else {
                    mediaView.getMediaPlayer().pause();
                    play.setGraphic(new ImageView("res/MediaIcons/play.png"));
                    playing = false;
                }
            });

            forward.setOnMouseClicked(event -> {
                forward.setEffect(new DropShadow());
                mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getCurrentTime().multiply(1.5));
            });

            backward.setOnMouseClicked(event -> {
                backward.setEffect(new DropShadow());
                mediaView.getMediaPlayer().seek(mediaView.getMediaPlayer().getCurrentTime().divide(1.5));
            });

            controls.setAlignment(Pos.CENTER);
            controls.setSpacing(10);
            controls.getChildren().addAll(backward, play, forward);
        }

        vBox.getChildren().addAll(currentlyPlaying, barControl, controls);

        return vBox;
    }
}
