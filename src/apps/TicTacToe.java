package apps;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sfe.os.CPU;

import java.util.HashMap;
import java.util.Map;

public class TicTacToe {

    int id;
    static CPU cpu=new CPU();

    public TicTacToe(CPU cpu, int id) {
        Stage stage = new Stage();
        GameManager gameManager = new GameManager();

        Scene scene = gameManager.getGameScene();
        scene.getStylesheets().add(
                getResource(
                        "tictactoe-blueskin.css"
                )
        );

        stage.setOnCloseRequest(event -> {
            System.out.println("XO Game with id : "+id+" Is removed");
            cpu.RemoveProcess(id);
        });

        stage.setTitle("Tic-Tac-Toe");
        stage.getIcons().add(SquareSkin.crossImage);
        stage.setScene(scene);
        stage.show();
    }

    private String getResource(String resourceName) {
        return getClass().getResource(resourceName).toExternalForm();
    }

}

class GameManager {
    private Scene gameScene;
    private Game game;

    GameManager() {
        newGame();
    }

    public void newGame() {
        game = new Game(this);

        if (gameScene == null) {
            gameScene = new Scene(game.getSkin());
        } else {
            gameScene.setRoot(game.getSkin());
        }
    }

    public void quit() {
        gameScene.getWindow().hide();
    }

    public Game getGame() {
        return game;
    }

    public Scene getGameScene() {
        return gameScene;
    }
}

class GameControls extends HBox {
    GameControls(final GameManager gameManager, final Game game) {
        getStyleClass().add("game-controls");

        visibleProperty().bind(game.gameOverProperty());

        Label playAgainLabel = new Label("Play Again?");
        playAgainLabel.getStyleClass().add("info");

        Button playAgainButton = new Button("Yes");
        playAgainButton.getStyleClass().add("play-again");
        playAgainButton.setDefaultButton(true);
        playAgainButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gameManager.newGame();
            }
        });

        Button exitButton = new Button("No");
        playAgainButton.getStyleClass().add("exit");
        exitButton.setCancelButton(true);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                gameManager.quit();
            }
        });

        getChildren().setAll(
                playAgainLabel,
                playAgainButton,
                exitButton
        );
    }
}

class StatusIndicator extends HBox {
    private final ImageView playerToken = new ImageView();
    private final Label playerLabel = new Label("Current Player: ");

    StatusIndicator(Game game) {
        getStyleClass().add("status-indicator");

        bindIndicatorFieldsToGame(game);

        playerToken.setFitHeight(32);
        playerToken.setPreserveRatio(true);

        playerLabel.getStyleClass().add("info");

        getChildren().addAll(playerLabel, playerToken);
    }

    private void bindIndicatorFieldsToGame(Game game) {
        playerToken.imageProperty().bind(
                Bindings.when(
                        game.currentPlayerProperty().isEqualTo(Square.State.NOUGHT)
                )
                        .then(SquareSkin.noughtImage)
                        .otherwise(
                                Bindings.when(
                                        game.currentPlayerProperty().isEqualTo(Square.State.CROSS)
                                )
                                        .then(SquareSkin.crossImage)
                                        .otherwise((Image) null)
                        )
        );

        playerLabel.textProperty().bind(
                Bindings.when(
                        game.gameOverProperty().not()
                )
                        .then("Current Player: ")
                        .otherwise(
                                Bindings.when(
                                        game.winnerProperty().isEqualTo(Square.State.EMPTY)
                                )
                                        .then("Draw")
                                        .otherwise("Winning Player: ")
                        )
        );
    }
}

class Game {
    private GameSkin skin;
    private Board board = new Board(this);
    private WinningStrategy winningStrategy = new WinningStrategy(board);

    private ReadOnlyObjectWrapper<Square.State> currentPlayer = new ReadOnlyObjectWrapper<>(Square.State.CROSS);

    public ReadOnlyObjectProperty<Square.State> currentPlayerProperty() {
        return currentPlayer.getReadOnlyProperty();
    }

    public Square.State getCurrentPlayer() {
        return currentPlayer.get();
    }

    private ReadOnlyObjectWrapper<Square.State> winner = new ReadOnlyObjectWrapper<>(Square.State.EMPTY);

    public ReadOnlyObjectProperty<Square.State> winnerProperty() {
        return winner.getReadOnlyProperty();
    }

    private ReadOnlyBooleanWrapper drawn = new ReadOnlyBooleanWrapper(false);

    public ReadOnlyBooleanProperty drawnProperty() {
        return drawn.getReadOnlyProperty();
    }

    public boolean isDrawn() {
        return drawn.get();
    }

    private ReadOnlyBooleanWrapper gameOver = new ReadOnlyBooleanWrapper(false);

    public ReadOnlyBooleanProperty gameOverProperty() {
        return gameOver.getReadOnlyProperty();
    }

    public boolean isGameOver() {
        return gameOver.get();
    }

    public Game(GameManager gameManager) {
        gameOver.bind(
                winnerProperty().isNotEqualTo(Square.State.EMPTY)
                        .or(drawnProperty())
        );

        skin = new GameSkin(gameManager, this);
    }

    public Board getBoard() {
        return board;
    }

    public void nextTurn() {
        if (isGameOver()) return;

        switch (currentPlayer.get()) {
            case EMPTY:
            case NOUGHT:
                currentPlayer.set(Square.State.CROSS);
                break;
            case CROSS:
                currentPlayer.set(Square.State.NOUGHT);
                break;
        }
    }

    private void checkForWinner() {
        winner.set(winningStrategy.getWinner());
        drawn.set(winningStrategy.isDrawn());

        if (isDrawn()) {
            currentPlayer.set(Square.State.EMPTY);
        }
    }

    public void boardUpdated() {
        checkForWinner();
    }

    public Parent getSkin() {
        return skin;
    }
}

class GameSkin extends VBox {
    GameSkin(GameManager gameManager, Game game) {
        getChildren().addAll(
                game.getBoard().getSkin(),
                new StatusIndicator(game),
                new GameControls(gameManager, game)
        );
    }
}

class WinningStrategy {
    private final Board board;

    private static final int NOUGHT_WON = 3;
    private static final int CROSS_WON = 30;

    private static final Map<Square.State, Integer> values = new HashMap<>();

    static {
        values.put(Square.State.EMPTY, 0);
        values.put(Square.State.NOUGHT, 1);
        values.put(Square.State.CROSS, 10);
    }

    public WinningStrategy(Board board) {
        this.board = board;
    }

    public Square.State getWinner() {
        for (int i = 0; i < 3; i++) {
            int score = 0;
            for (int j = 0; j < 3; j++) {
                score += valueOf(i, j);
            }
            if (isWinning(score)) {
                return winner(score);
            }
        }

        for (int i = 0; i < 3; i++) {
            int score = 0;
            for (int j = 0; j < 3; j++) {
                score += valueOf(j, i);
            }
            if (isWinning(score)) {
                return winner(score);
            }
        }

        int score = 0;
        score += valueOf(0, 0);
        score += valueOf(1, 1);
        score += valueOf(2, 2);
        if (isWinning(score)) {
            return winner(score);
        }

        score = 0;
        score += valueOf(2, 0);
        score += valueOf(1, 1);
        score += valueOf(0, 2);
        if (isWinning(score)) {
            return winner(score);
        }

        return Square.State.EMPTY;
    }

    public boolean isDrawn() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getSquare(i, j).getState() == Square.State.EMPTY) {
                    return false;
                }
            }
        }

        return getWinner() == Square.State.EMPTY;
    }

    private Integer valueOf(int i, int j) {
        return values.get(board.getSquare(i, j).getState());
    }

    private boolean isWinning(int score) {
        return score == NOUGHT_WON || score == CROSS_WON;
    }

    private Square.State winner(int score) {
        if (score == NOUGHT_WON) return Square.State.NOUGHT;
        if (score == CROSS_WON) return Square.State.CROSS;

        return Square.State.EMPTY;
    }
}

class Board {
    private final BoardSkin skin;

    private final Square[][] squares = new Square[3][3];

    public Board(Game game) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squares[i][j] = new Square(game);
            }
        }

        skin = new BoardSkin(this);
    }

    public Square getSquare(int i, int j) {
        return squares[i][j];
    }

    public Node getSkin() {
        return skin;
    }
}

class BoardSkin extends GridPane {
    BoardSkin(Board board) {
        getStyleClass().add("board");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                add(board.getSquare(i, j).getSkin(), i, j);
            }
        }
    }
}

class Square {
    enum State {EMPTY, NOUGHT, CROSS}

    private final SquareSkin skin;

    private ReadOnlyObjectWrapper<State> state = new ReadOnlyObjectWrapper<>(State.EMPTY);

    public ReadOnlyObjectProperty<State> stateProperty() {
        return state.getReadOnlyProperty();
    }

    public State getState() {
        return state.get();
    }

    private final Game game;

    public Square(Game game) {
        this.game = game;

        skin = new SquareSkin(this);
    }

    public void pressed() {
        if (!game.isGameOver() && state.get() == State.EMPTY) {
            state.set(game.getCurrentPlayer());
            game.boardUpdated();
            game.nextTurn();
        }
    }

    public Node getSkin() {
        return skin;
    }
}

class SquareSkin extends StackPane {
    static final Image noughtImage = new Image(
            "http://icons.iconarchive.com/icons/double-j-design/origami-colored-pencil/128/green-cd-icon.png"
    );
    static final Image crossImage = new Image(
            "http://icons.iconarchive.com/icons/double-j-design/origami-colored-pencil/128/blue-cross-icon.png"
    );

    private final ImageView imageView = new ImageView();

    SquareSkin(final Square square) {
        getStyleClass().add("square");

        imageView.setMouseTransparent(true);

        getChildren().setAll(imageView);
        setPrefSize(crossImage.getHeight() + 20, crossImage.getHeight() + 20);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                square.pressed();
            }
        });

        square.stateProperty().addListener(new ChangeListener<Square.State>() {
            @Override
            public void changed(ObservableValue<? extends Square.State> observableValue, Square.State oldState, Square.State state) {
                switch (state) {
                    case EMPTY:
                        imageView.setImage(null);
                        break;
                    case NOUGHT:
                        imageView.setImage(noughtImage);
                        break;
                    case CROSS:
                        imageView.setImage(crossImage);
                        break;
                }
            }
        });
    }
}