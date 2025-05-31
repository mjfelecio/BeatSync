package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.core.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // UI setup
        Canvas canvas = new Canvas(GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        SceneManager.initialize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, stage);

        GameState.getInstance().setCurrentScene(GameScene.TITLE_SCREEN);

        Scene scene = new Scene(new StackPane(canvas), GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        stage.setTitle("Beat Sync: VSRG made with Java");
        stage.show();

        // Game setup
        gameEngine = new GameEngine();
        try {
            String beatmapOszFolderPath = new File("src/main/resources/com/mjfelecio/beatsync/beatmaps/1301440 TrySail - Utsuroi (Short Ver.) (another copy).osz_FILES/").toURI().toString();
            gameEngine.initialize(beatmapOszFolderPath);
            gameEngine.start();
        } catch (RuntimeException e) {
            System.err.println("Failed to start game: " + e.getMessage());
            return;
        }

        // Input Setup
        InputHandler inputHandler = gameEngine.getInputHandler();
        scene.setOnKeyPressed(event ->
                inputHandler.handleKeyPress(event.getCode(), gameEngine.getCurrentTime()));
        scene.setOnKeyReleased(event ->
                inputHandler.handleKeyRelease(event.getCode(), gameEngine.getCurrentTime()));

        // Game Loop
        new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                long deltaTime = now - lastTime;
                lastTime = now;

                gameEngine.update(deltaTime);
                gameEngine.render(gc);
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
