package apps;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sfe.os.CPU;

import java.net.InetSocketAddress;
import java.net.Socket;

import java.net.InetSocketAddress;
import java.net.Socket;


public class WebBrowser {

    public static final String defaultUrl = "www.google.com";
    private String fileUrl = null;
    WebView browser;
    WebEngine engine;
    WebHistory history;
    Label backButton;
    Label forwardButton;
    Label goButton;
    Label reloadButton;
    Label home;
    ComboBox webHistoryComboBox;
    TextField url;
    ProgressIndicator progress = new ProgressIndicator();
    int id;

    public WebBrowser(String fileUrl, int id, CPU cpu) {
        this.id = id;
        this.fileUrl = fileUrl;
        Stage stage = new Stage();
        stage.setTitle("Browser");
        stage.show();
        stage.setScene(new Scene(webViewScene()));
        if (!checkIntConnection(defaultUrl)) {
            alert();
        }
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("WebBrowser with id :" + id + " Is removed");
                cpu.RemoveProcess(id);
            }
        });

    }

    public boolean checkIntConnection(String url) {
        boolean status = false;
        Socket sock = new Socket();
        InetSocketAddress address = new InetSocketAddress(url, 80);
        try {
            sock.connect(address);
            if (sock.isConnected()) {
                status = true;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                sock.close();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return status;
    }

    public void alert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning");
        alert.setHeaderText("Internet Connection");
        alert.setContentText("No Internet Connection!");
        alert.showAndWait();
    }

    private BorderPane webViewScene() {
        url = new TextField();
        url.setPrefWidth(800);
        browser = new WebView();
        engine = browser.getEngine();
        backButton = new Label(null, new ImageView(new Image("res/BrowserIcons/back.png")));
        backButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> backButton.setEffect(new Glow(0.5)));
        backButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> backButton.setEffect(null));
        backButton.setOnMouseClicked(e -> {
            if (!checkIntConnection(defaultUrl)) {
                alert();
            } else {
                browser.getEngine().load(goBack());
            }
        });

        forwardButton = new Label(null, new ImageView(new Image("res/BrowserIcons/forward.png")));
        forwardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> forwardButton.setEffect(new Glow(0.5)));
        forwardButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> forwardButton.setEffect(null));
        forwardButton.setOnMouseClicked(e -> {
            if (!checkIntConnection(defaultUrl)) {
                alert();
            } else {
                browser.getEngine().load(goForward());
            }
        });

        reloadButton = new Label(null, new ImageView(new Image("res/BrowserIcons/reload.png")));
        reloadButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> reloadButton.setEffect(new Glow(0.5)));
        reloadButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> reloadButton.setEffect(null));
        reloadButton.setOnMouseClicked(e -> {
            if (!checkIntConnection(defaultUrl)) {
                alert();
            } else {
                browser.getEngine().reload();
            }
        });

        home = new Label(null, new ImageView("res/BrowserIcons/home.png"));
        home.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> home.setEffect(new Glow(0.5)));
        home.addEventHandler(MouseEvent.MOUSE_EXITED, event -> home.setEffect(null));
        home.setOnMouseClicked(e -> {
            if (!checkIntConnection(defaultUrl)) {
                alert();
            } else {
                browser.getEngine().load(defaultUrl);
            }
        });

        goButton = new Label(null, new ImageView(new Image("res/BrowserIcons/go.png")));
        goButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> goButton.setEffect(new Glow(0.5)));
        goButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> goButton.setEffect(null));
        goButton.setOnMouseClicked(e -> goUrl());


        url.setText(fileUrl);
        goUrl();


        webHistoryComboBox = new ComboBox();
        webHistoryComboBox.setPromptText("Web History");
        webHistoryComboBox.setPrefWidth(200);
        webHistoryComboBox.setOnAction(event -> {
            //Navigate to the link in the history index
            int offset = webHistoryComboBox.getSelectionModel().getSelectedIndex() - history.getCurrentIndex();
            history.go(offset);
        });


        history = browser.getEngine().getHistory();

        //Displying browser history in a combo box
        history.getEntries().addListener((ListChangeListener<WebHistory.Entry>) c -> {
            c.next();
            for (WebHistory.Entry e : c.getRemoved())
                webHistoryComboBox.getItems().remove(e.getUrl());
            for (WebHistory.Entry e : c.getAddedSubList())
                webHistoryComboBox.getItems().add(e.getUrl());
        });

        //Enabling and Disabling back and forward buttons
        browser.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (!browser.getEngine().getHistory().getEntries().isEmpty()) {
                int index = browser.getEngine().getHistory().getCurrentIndex();
                url.setText(browser.getEngine().getHistory().getEntries().get(index).getUrl());
                if (index == 0) {
                    backButton.setDisable(true);
                } else {
                    backButton.setDisable(false);
                }
                if (browser.getEngine().getHistory().getEntries().size() == 1) {
                    forwardButton.setDisable(true);
                } else if (index < browser.getEngine().getHistory().getEntries().size() - 2) {
                    forwardButton.setDisable(false);
                } else if (index < browser.getEngine().getHistory().getEntries().size() - 1) {
                    forwardButton.setDisable(false);
                } else if (index == browser.getEngine().getHistory().getEntries().size() - 1) {
                    forwardButton.setDisable(true);
                }
            }
        });

        progress.setPrefSize(25, 25);
        progress.visibleProperty().bind(browser.getEngine().getLoadWorker().runningProperty());

        BorderPane border = new BorderPane();
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setSpacing(10);
        topBar.setPadding(new Insets(10, 10, 10, 10));
        topBar.getChildren().addAll(home, backButton, forwardButton, reloadButton, url, goButton, progress, webHistoryComboBox);

        border.setTop(topBar);
        border.setCenter(browser);
        border.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight() - 1);
        border.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth());

        return border;
    }

    //Adding functionality to the back button
    public String goBack() {
        final WebHistory history = browser.getEngine().getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();

        Platform.runLater(() -> history.go(-1));

        if (currentIndex > 1) {
            entryList.get(currentIndex - 1);
            backButton.setDisable(false);
        } else {
            entryList.get(history.getCurrentIndex());
            backButton.setDisable(true);
            forwardButton.setDisable(false);
        }
        return entryList.get(currentIndex).getUrl();
    }

    // Adding functionality to the forward button
    public String goForward() {
        final WebHistory history = browser.getEngine().getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();

        Platform.runLater(() -> history.go(1));
        if (currentIndex < entryList.size() - 2) {
            entryList.get(currentIndex + 1);
            forwardButton.setDisable(false);
        } else {
            entryList.get(history.getCurrentIndex());
            forwardButton.setDisable(true);
            backButton.setDisable(false);
        }
        return entryList.get(currentIndex).getUrl();
    }

    private void goUrl() {
        // adding the http or https prefix if user didn't type it
        if (!checkIntConnection(defaultUrl)) {
            alert();
        } else {
            if (url.getText().length() > 7 && (url.getText(0, 7).equals("http://") || url.getText(0, 8).equals("https://"))) {
                browser.getEngine().load(url.getText());
            } else if (url.getText().length() > 7 && !url.getText(0, 7).equals("http://")) {
                browser.getEngine().load("http://" + url.getText());
            } else {
                browser.getEngine().load("https://" + url.getText());
            }
        }
    }

}
