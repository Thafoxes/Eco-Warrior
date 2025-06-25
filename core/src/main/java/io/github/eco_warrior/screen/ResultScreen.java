package io.github.eco_warrior.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.Main;
import io.github.eco_warrior.WorldMap;
import io.github.eco_warrior.controller.buttonGenerator;
import io.github.eco_warrior.controller.FontGenerator;
import io.github.eco_warrior.enums.textEnum;
import io.github.eco_warrior.mainmenu.MainMenuScreen;

import static io.github.eco_warrior.constant.ConstantsVar.*;

public class ResultScreen implements Screen {

    private boolean isGameOver = false;
    private final Main game;
    private int score;

    private SpriteBatch batch;
    private FontGenerator uiFont;
    private SpriteBatch uiBatch;

    private Viewport viewport;
    private OrthographicCamera camera;

    //button setup
    private Stage stage;
    private Skin skin;

    private buttonGenerator buttonGenerator;

    private String additionalMessage = "";

    //sound
    private Sound winningSound;
    private Sound gameOverSound;

    private Texture backgroundCharacterImg;


    public ResultScreen(Main game, int score, buttonGenerator buttonGenerator) {
        this.game = game;
        this.score = score;
        this.buttonGenerator = buttonGenerator;
        soundSetup();
    }



    public ResultScreen(Main game, int score) {
        this.game = game;
        this.score = score;
        buttonGenerator = game.getButtonFactory();
        soundSetup();
    }

    public ResultScreen(Main game, int score, boolean isGameOver, String additionalMessage) {
        this.game = game; //11
        this.score = score;
        this.isGameOver = isGameOver;
        buttonGenerator = game.getButtonFactory();
        this.additionalMessage = additionalMessage;
        soundSetup();
    }

    public ResultScreen(Main game, int score, boolean isGameOver, String additionalMessage, Texture backgroundCharacterImg) {
        this.game = game;
        this.score = score;
        this.isGameOver = isGameOver;
        buttonGenerator = game.getButtonFactory();
        this.additionalMessage = additionalMessage;
        this.backgroundCharacterImg = backgroundCharacterImg;
        soundSetup();
    }

    public ResultScreen(Main game, int score, boolean isGameOver) {
        this.game = game;
        this.score = score;
        this.isGameOver = isGameOver;
        buttonGenerator = game.getButtonFactory();
        this.additionalMessage = "";
        soundSetup();
    }

    private void soundSetup() {
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/Game_over_sfx.mp3"));
        winningSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/game_finished_sfx.mp3"));

        if(isGameOver) {
            gameOverSound.play();
        } else {
            winningSound.play();
        }
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        uiFont = new FontGenerator();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        uiBatch = new SpriteBatch();
        //Ui interaction
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        setupButtons();
    }

    private void createReturnButton() {
       TextButton backButton = buttonGenerator.createDefaultButton(
           "Return to main menu",
           WINDOW_WIDTH,
           WINDOW_HEIGHT
       );
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //return back to main menu
                game.setScreen(new MainMenuScreen(game));
            }
        });


        stage.addActor(backButton);
    }

    private void createReturnWorldMap() {
        TextButton backButton = buttonGenerator.createDefaultButton(
            "Return to world map",
            WINDOW_WIDTH,
            WINDOW_HEIGHT
        );
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new WorldMap(game));
            }
        });


        stage.addActor(backButton);
    }

    private void setupButtons() {
        // Create buttons
        TextButton worldMapButton = createReturnWorldMapButton();
        TextButton mainMenuButton = createReturnMainMenuButton();

        // Position buttons in the center with equal spacing
        float buttonHeight = worldMapButton.getHeight();
        float spacing = 20; // Space between buttons

        // Calculate y positions for centered vertical alignment
        float totalHeight = (2 * buttonHeight) + spacing;
        float startY = (WINDOW_HEIGHT / 2) + (totalHeight / 2) - buttonHeight;

        // Position world map button
        worldMapButton.setPosition(
            (WINDOW_WIDTH / 2) - (worldMapButton.getWidth() / 2),
            startY
        );

        // Position main menu button
        mainMenuButton.setPosition(
            (WINDOW_WIDTH / 2) - (mainMenuButton.getWidth() / 2),
            startY - buttonHeight - spacing
        );

        // Add buttons to stage
        stage.addActor(worldMapButton);
        stage.addActor(mainMenuButton);
    }

    private TextButton createReturnWorldMapButton() {
        TextButton button = buttonGenerator.createDefaultButton(
            "Return to World Map",
            WINDOW_WIDTH,
            WINDOW_HEIGHT
        );
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new WorldMap(game));
            }
        });
        return button;
    }

    private TextButton createReturnMainMenuButton() {
        TextButton button = buttonGenerator.createDefaultButton(
            "Return to Main Menu",
            WINDOW_WIDTH,
            WINDOW_HEIGHT
        );
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        return button;
    }



    @Override
    public void render(float delta) {
        try{
            viewport.apply();
            Gdx.gl.glClearColor(192, 192, 192, 0f); // dark gray
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            camera.update();
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            if(backgroundCharacterImg != null){
                // Draw background image (scaled to fit screen)
                float scaleBGM = 3;
                batch.draw(backgroundCharacterImg, 0, 0, backgroundCharacterImg.getWidth() * scaleBGM, backgroundCharacterImg.getHeight() * scaleBGM);

            }

            batch.end();
            stage.act(delta);
            stage.draw();

            String resultMessage = isGameOver ?  "Game Over! ": "Challenge completed! ";
            uiFont.fontDraw(uiBatch, resultMessage + additionalMessage, camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT ), textEnum.X_CENTER, textEnum.TOP);
            uiFont.fontDraw(uiBatch, "Your score is " + score + "\n", camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT ), textEnum.X_CENTER, textEnum.BOTTOM);



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        uiFont.dispose();
        stage.dispose();

    }
}
