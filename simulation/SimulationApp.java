package simulation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.Map;

public class SimulationApp extends Application {
    private Simulation simulation;
    private Canvas canvas;
    private GraphicsContext gc;
    private boolean running = false;
    private Thread simulationThread;

    private Label healthyLabel, infectedLabel, asymptomaticLabel, immuneLabel, timeLabel;

    private double areaWidth = 10;
    private double areaHeight = 10;
    private int initialPopulation = 25;
    private double entryFrequency = 2.0;
    private boolean immunityEnabled = false;
    private double immunityPercentage = 30;

    @Override
    public void start(Stage primaryStage) {
        showConfigDialog(primaryStage);
    }

    private void showConfigDialog(Stage primaryStage) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Konfiguracja Symulacji");
        dialog.setHeaderText("Ustaw parametry symulacji");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField widthField = new TextField("10");
        TextField heightField = new TextField("10");
        TextField popField = new TextField("25");
        TextField freqField = new TextField("2.0");

        ComboBox<String> modeCombo = new ComboBox<>();
        modeCombo.getItems().addAll("Brak odporności", "Częściowa odporność");
        modeCombo.setValue("Brak odporności");

        TextField immunePctField = new TextField("30");
        immunePctField.setDisable(true);

        modeCombo.setOnAction(e -> {
            boolean immune = modeCombo.getValue().equals("Częściowa odporność");
            immunePctField.setDisable(!immune);
        });

        grid.add(new Label("Szerokość (n) [m]:"), 0, 0);
        grid.add(widthField, 1, 0);
        grid.add(new Label("Wysokość (m) [m]:"), 0, 1);
        grid.add(heightField, 1, 1);
        grid.add(new Label("Populacja początkowa (i):"), 0, 2);
        grid.add(popField, 1, 2);
        grid.add(new Label("Częstotliwość wejść [os/s]:"), 0, 3);
        grid.add(freqField, 1, 3);
        grid.add(new Label("Tryb symulacji:"), 0, 4);
        grid.add(modeCombo, 1, 4);
        grid.add(new Label("Odporność [%]:"), 0, 5);
        grid.add(immunePctField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                areaWidth = Double.parseDouble(widthField.getText());
                areaHeight = Double.parseDouble(heightField.getText());
                initialPopulation = Integer.parseInt(popField.getText());
                entryFrequency = Double.parseDouble(freqField.getText());
                immunityEnabled = modeCombo.getValue().equals("Częściowa odporność");
                immunityPercentage = Double.parseDouble(immunePctField.getText());

                initializeUI(primaryStage);
            }
        });
    }

    private void initializeUI(Stage primaryStage) {
        simulation = new Simulation(areaWidth, areaHeight, initialPopulation,
                entryFrequency, immunityEnabled, immunityPercentage);

        BorderPane root = new BorderPane();

        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        root.setCenter(canvas);

        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));

        Button startBtn = new Button("Start");
        Button pauseBtn = new Button("Pauza");
        Button resetBtn = new Button("Reset");
        Button saveBtn = new Button("Zapisz");
        Button loadBtn = new Button("Wczytaj");

        startBtn.setOnAction(e -> startSimulation());
        pauseBtn.setOnAction(e -> pauseSimulation());
        resetBtn.setOnAction(e -> resetSimulation());
        saveBtn.setOnAction(e -> saveSimulation(primaryStage));
        loadBtn.setOnAction(e -> loadSimulation(primaryStage));

        controls.getChildren().addAll(startBtn, pauseBtn, resetBtn, saveBtn, loadBtn);
        root.setTop(controls);

        VBox stats = new VBox(10);
        stats.setPadding(new Insets(10));
        stats.setPrefWidth(200);

        healthyLabel = new Label("Zdrowi: 0");
        infectedLabel = new Label("Zakażeni: 0");
        asymptomaticLabel = new Label("Bezobjawowi: 0");
        immuneLabel = new Label("Odporni: 0");
        timeLabel = new Label("Czas: 0.0s");

        stats.getChildren().addAll(
                new Label("STATYSTYKI"), new Separator(),
                healthyLabel, infectedLabel, asymptomaticLabel, immuneLabel,
                new Separator(), timeLabel
        );
        root.setRight(stats);

        Scene scene = new Scene(root, 1024, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Symulacja Wirusa");
        primaryStage.show();

        render();
    }

    private void startSimulation() {
        if (!running) {
            running = true;
            simulationThread = new Thread(() -> {
                double FRAME = 1.0 / 25.0;

                while (running) {
                    long start = System.nanoTime();

                    simulation.step();
                    Platform.runLater(this::render);

                    long logicTime = System.nanoTime() - start;
                    double sleepTime = FRAME - (logicTime / 1e9);

                    if (sleepTime > 0) {
                        try {
                            Thread.sleep((long)(sleepTime * 1000));
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            });
            simulationThread.setDaemon(true);
            simulationThread.start();
        }
    }

    private void pauseSimulation() {
        running = false;
    }

    private void resetSimulation() {
        running = false;
        simulation = new Simulation(areaWidth, areaHeight, initialPopulation,
                entryFrequency, immunityEnabled, immunityPercentage);
        render();
    }

    private void saveSimulation(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Zapisz stan symulacji");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Simulation", "*.sim"));
        File file = fc.showSaveDialog(stage);

        if (file != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(simulation.saveState());
                showAlert("Sukces", "Stan zapisany!");
            } catch (IOException e) {
                showAlert("Błąd", "Nie udało się zapisać: " + e.getMessage());
            }
        }
    }

    private void loadSimulation(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Wczytaj stan symulacji");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Simulation", "*.sim"));
        File file = fc.showOpenDialog(stage);

        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                SimulationMemento memento = (SimulationMemento) ois.readObject();
                simulation.restoreState(memento);
                render();
                showAlert("Sukces", "Stan wczytany!");
            } catch (IOException | ClassNotFoundException e) {
                showAlert("Błąd", "Nie udało się wczytać: " + e.getMessage());
            }
        }
    }

    private void render() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double scaleX = canvas.getWidth() / areaWidth;
        double scaleY = canvas.getHeight() / areaHeight;

        for (Person p : simulation.getPeople()) {
            double x = p.getPosition().getX() * scaleX;
            double y = p.getPosition().getY() * scaleY;

            String state = p.getState().getStateName();
            if (state.equals("Healthy")) gc.setFill(Color.GREEN);
            else if (state.equals("Infected-Symptomatic")) gc.setFill(Color.RED);
            else if (state.equals("Infected-Asymptomatic")) gc.setFill(Color.ORANGE);
            else gc.setFill(Color.BLUE);

            gc.fillOval(x - 3, y - 3, 6, 6);
        }

        Map<String, Integer> stats = simulation.getStatistics();
        healthyLabel.setText("Zdrowi: " + stats.get("Healthy"));
        infectedLabel.setText("Zakażeni: " + stats.get("Infected"));
        asymptomaticLabel.setText("Bezobjawowi: " + stats.get("Asymptomatic"));
        immuneLabel.setText("Odporni: " + stats.get("Immune"));
        timeLabel.setText(String.format("Czas: %.1fs", simulation.getSimulationTime()));
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}