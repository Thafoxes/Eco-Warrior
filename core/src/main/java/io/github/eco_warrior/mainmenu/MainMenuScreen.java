package io.github.eco_warrior.mainmenu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.eco_warrior.Main;

import static io.github.eco_warrior.constant.ConstantsVar.*;

public class MainMenuScreen implements Screen {
    private Stage stage;
    private Texture[] frames;
    private float frameDuration = 0.1f; // Time per frame in seconds
    private float elapsedTime = 0;
    private int currentFrame = 0;
    private SpriteBatch batch;
    private Sound clickSound; // Click sound instance
    private TextButton startButton; // Start button instance
    private TextButton settingsButton; // Settings button instance

    //game
    private Main game;


    public MainMenuScreen(Main main) {

        this.game = main;

        stage = new Stage(new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Use the global MusicManager to play the music
        MusicManager.getInstance().playMusic();

        // Load the background textures
        frames = new Texture[]{
            new Texture(Gdx.files.internal("Background_Image/frame_00_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_01_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_02_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_03_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_04_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_05_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_06_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_07_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_08_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_09_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_10_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_11_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_12_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_13_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_14_delay-0.09s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_15_delay-0.09s.gif")),
            // Add more frames as needed
        };
        batch = new SpriteBatch();

        // Load click sound
        clickSound = Gdx.audio.newSound(Gdx.files.internal(BUTTON_CLICK_SFX)); // Replace with your sound file

        // Load the skin
        Skin skin = new Skin(Gdx.files.internal(UI_SKIN_PATH));

        // Generate a custom font using FreeTypeFontGenerator
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_TYPE));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36; // Set desired font size
        BitmapFont customFont = generator.generateFont(parameter);
        generator.dispose(); // Dispose of the generator after use

        // Add the custom font to the skin
        skin.add("custom-font", customFont, BitmapFont.class);

        // Apply the custom font to the button style
        TextButtonStyle buttonStyle = skin.get(TextButtonStyle.class);
        buttonStyle.font = customFont;

        // Create the Start button
        startButton = new TextButton("Start", buttonStyle);
        startButton.setSize(200, 80);

        // Add a click listener to the Start button
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                clickSound.play(SettingsManager.getInstance().getClickSoundVolume()); // Play click sound with saved volume
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new NextScreen(main));
            }
        });

        // Add the Start button to the stage
        stage.addActor(startButton);

        // Create the Settings button
        settingsButton = new TextButton("Settings", buttonStyle);
        settingsButton.setSize(200, 80);

        // Add a click listener to the Settings button
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                clickSound.play(SettingsManager.getInstance().getClickSoundVolume()); // Play click sound with saved volume
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new SettingsScreen(game));
            }
        });

        // Add the Settings button to the stage
        stage.addActor(settingsButton);

        // Position the buttons dynamically
        updateButtonPositions();
    }

    private void updateButtonPositions() {
        float viewportWidth = stage.getViewport().getWorldWidth();
        float viewportHeight = stage.getViewport().getWorldHeight();

        // Position the Start button
        startButton.setPosition(
            viewportWidth / 2 - startButton.getWidth() / 2,
            viewportHeight / 2 - startButton.getHeight() / 2 + 60 // Adjust Y position for spacing
        );

        // Position the Settings button below the Start button
        settingsButton.setPosition(
            viewportWidth / 2 - settingsButton.getWidth() / 2,
            startButton.getY() - settingsButton.getHeight() - 20 // 20px spacing below Start button
        );
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Update the elapsed time and current frame for animation
        elapsedTime += delta;
        if (elapsedTime >= frameDuration) {
            elapsedTime = 0;
            currentFrame = (currentFrame + 1) % frames.length;
        }

        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        // Ensure the batch uses the viewport's projection matrix
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);

        // Get the current viewport dimensions
        float viewportWidth = stage.getViewport().getWorldWidth();
        float viewportHeight = stage.getViewport().getWorldHeight();

        // Draw the background to fit the current viewport dimensions
        batch.begin();
        batch.draw(frames[currentFrame], 0, 0, viewportWidth, viewportHeight);
        batch.end();

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport with new dimensions
        stage.getViewport().update(width, height, true);

        // Dynamically update button positions
        updateButtonPositions();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // Dispose of resources
        stage.dispose();
        batch.dispose();
        for (Texture frame : frames) {
            frame.dispose();
        }
        clickSound.dispose(); // Dispose of the click sound
    }
}
