package apps;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sfe.os.CPU;

import java.util.HashMap;
import java.util.Map;

public class Calc {

    private static final String[][] template = {
            {"7", "8", "9", "/"},
            {"4", "5", "6", "*"},
            {"1", "2", "3", "-"},
            {"0", "c", "=", "+"}
    };

    private final Map<String, Button> accelerators = new HashMap<>();

    private DoubleProperty stackValue = new SimpleDoubleProperty();
    private DoubleProperty value = new SimpleDoubleProperty();

    private enum Op {NOOP, ADD, SUBTRACT, MULTIPLY, DIVIDE}

    private Op curOp = Op.NOOP;
    private Op stackOp = Op.NOOP;
int id;
static CPU cpu=new CPU();
    public Calc(CPU cpu,int id) {
        this.id=id;
        this.cpu=cpu;
        Stage stage = new Stage();
        final TextField screen = createScreen();
        final TilePane buttons = createButtons();

        stage.setTitle("Calculator");
        stage.setOnCloseRequest(event -> {
            System.out.println("Calculator with id : "+id+" Is removed");
            cpu.RemoveProcess(id);
        });
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setScene(new Scene(createLayout(screen, buttons)));
        stage.show();
    }

    private VBox createLayout(TextField screen, TilePane buttons) {
        final VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: dodgerblue; -fx-padding: 20; -fx-font-size: 20;");
        layout.getChildren().setAll(screen, buttons);
        handleAccelerators(layout);
        screen.prefWidthProperty().bind(buttons.widthProperty());
        return layout;
    }

    private void handleAccelerators(VBox layout) {
        layout.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                Button activated = accelerators.get(keyEvent.getText());
                if (activated != null) {
                    activated.fire();
                }
            }
        });
    }

    private TextField createScreen() {
        final TextField screen = new TextField();
        screen.setStyle("-fx-background-color: lightblue;");
        screen.setAlignment(Pos.CENTER_RIGHT);
        screen.setEditable(false);
        screen.textProperty().bind(Bindings.format("%.0f", value));
        return screen;
    }

    private TilePane createButtons() {
        TilePane buttons = new TilePane();
        buttons.setVgap(7);
        buttons.setHgap(7);
        buttons.setPrefColumns(template[0].length);
        for (String[] r : template) {
            for (String s : r) {
                buttons.getChildren().add(createButton(s));
            }
        }
        return buttons;
    }

    private Button createButton(final String s) {
        Button button = makeStandardButton(s);

        if (s.matches("[0-9]")) {
            makeNumericButton(s, button);
        } else {
            final ObjectProperty<Op> triggerOp = determineOperand(s);
            if (triggerOp.get() != Op.NOOP) {
                makeOperandButton(button, triggerOp);
            } else if ("c".equals(s)) {
                makeClearButton(button);
            } else if ("=".equals(s)) {
                makeEqualsButton(button);
            }
        }

        return button;
    }

    private ObjectProperty<Op> determineOperand(String s) {
        final ObjectProperty<Op> triggerOp = new SimpleObjectProperty<>(Op.NOOP);
        switch (s) {
            case "+":
                triggerOp.set(Op.ADD);
                break;
            case "-":
                triggerOp.set(Op.SUBTRACT);
                break;
            case "*":
                triggerOp.set(Op.MULTIPLY);
                break;
            case "/":
                triggerOp.set(Op.DIVIDE);
                break;
        }
        return triggerOp;
    }

    private void makeOperandButton(Button button, final ObjectProperty<Op> triggerOp) {
        button.setStyle("-fx-base: lightgray;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                curOp = triggerOp.get();
            }
        });
    }

    private Button makeStandardButton(String s) {
        Button button = new Button(s);
        button.setStyle("-fx-base: beige;");
        accelerators.put(s, button);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return button;
    }

    private void makeNumericButton(final String s, Button button) {
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (curOp == Op.NOOP) {
                    value.set(value.get() * 10 + Integer.parseInt(s));
                } else {
                    stackValue.set(value.get());
                    value.set(Integer.parseInt(s));
                    stackOp = curOp;
                    curOp = Op.NOOP;
                }
            }
        });
    }

    private void makeClearButton(Button button) {
        button.setStyle("-fx-base: mistyrose;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                value.set(0);
            }
        });
    }

    private void makeEqualsButton(Button button) {
        button.setStyle("-fx-base: ghostwhite;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switch (stackOp) {
                    case ADD:
                        value.set(stackValue.get() + value.get());
                        break;
                    case SUBTRACT:
                        value.set(stackValue.get() - value.get());
                        break;
                    case MULTIPLY:
                        value.set(stackValue.get() * value.get());
                        break;
                    case DIVIDE:
                        value.set(stackValue.get() / value.get());
                        break;
                }
            }
        });
    }
}