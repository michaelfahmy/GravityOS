package apps;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Created by michael on 12/12/15.
 */


public class WebBrowser {

    public WebBrowser() {
        Stage stage = new Stage();
        stage.setTitle("Browser");
        stage.setScene(new Scene(new WebViewPane()));
        stage.show();
    }

    public class WebViewPane extends Pane {
        WebView browser;
        WebEngine engine;
        WebHistory history;
        Button backButton;
        Button forwardButton;
        Button goButton;
        Button reloadButton;
        ComboBox webHistoryComboBox;
        TextField url;

        public WebViewPane() {
            browser = new WebView();
            engine = browser.getEngine();
            engine.load("http://www.google.com");
            history = browser.getEngine().getHistory();

            backButton = new Button(null, new ImageView(new Image("res/browserIcons/back.png")));
            backButton.setOnAction((ActionEvent e) -> {
                browser.getEngine().load(goBack());
            });
            backButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> backButton.setEffect(new DropShadow()));
            backButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> backButton.setEffect(null));

            forwardButton = new Button(null, new ImageView(new Image("res/browserIcons/forward.png")));
            forwardButton.setOnAction((ActionEvent e) -> {
                browser.getEngine().load(goForward());
            });
            forwardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> forwardButton.setEffect(new DropShadow()));
            forwardButton.addEventHandler(MouseEvent.MOUSE_EXITED, event-> forwardButton.setEffect(null));

            reloadButton = new Button(null, new ImageView(new Image("res/browserIcons/reload.png")));
            reloadButton.setOnAction(event -> {
                browser.getEngine().reload();
            });
            reloadButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> reloadButton.setEffect(new DropShadow()));
            reloadButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> reloadButton.setEffect(null));

            goButton = new Button(null, new ImageView(new Image("res/browserIcons/go.jpg")));
            goButton.setOnAction(event -> {
                // adding the http or https prefix if user didn't type it
                if ( url.getText().length() > 7 && (url.getText(0, 7).equals("http://") || url.getText(0, 8).equals("https://"))) {
                    browser.getEngine().load(url.getText());
                } else if (url.getText().length() > 7 && !url.getText(0, 7).equals("http://")) {
                    browser.getEngine().load("http://" + url.getText());
                } else {
                    browser.getEngine().load("https://" + url.getText());
                }
            });
            goButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> goButton.setEffect(new DropShadow()));
            goButton.addEventHandler(MouseEvent.MOUSE_EXITED, event -> goButton.setEffect(null));

            webHistoryComboBox = new ComboBox();
            webHistoryComboBox.setPromptText("Web History");
            webHistoryComboBox.setPrefWidth(200);
            webHistoryComboBox.setOnAction(event -> {
                //Navigate to the link in the history index
                int offset = webHistoryComboBox.getSelectionModel().getSelectedIndex() - history.getCurrentIndex();
                history.go(offset);
            });

            //Displying browse history in a combo box
            history.getEntries().addListener((ListChangeListener<WebHistory.Entry>) c -> {
                c.next();
                for (WebHistory.Entry e : c.getRemoved())
                    webHistoryComboBox.getItems().remove(e.getUrl());
                for (WebHistory.Entry e : c.getAddedSubList())
                    webHistoryComboBox.getItems().add(e.getUrl());
            });

            url = new TextField();
            url.setPrefWidth(800);

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

            BorderPane border = new BorderPane();
            HBox topBar = new HBox();
            topBar.setAlignment(Pos.CENTER);
            topBar.setSpacing(10);
            topBar.setPadding(new Insets(10, 10, 10, 10));
            topBar.getChildren().addAll(reloadButton, url, goButton, backButton, forwardButton, webHistoryComboBox);

            border.setTop(topBar);
            border.setCenter(browser);
            border.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight());
            border.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth());


            getChildren().addAll(border);
        }

        //Adding functionality to the back button
        public String goBack() {
            final WebHistory history = browser.getEngine().getHistory();
            ObservableList<WebHistory.Entry> entryList = history.getEntries();
            int currentIndex = history.getCurrentIndex();

            Platform.runLater(() -> history.go(- 1));

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

    }


}