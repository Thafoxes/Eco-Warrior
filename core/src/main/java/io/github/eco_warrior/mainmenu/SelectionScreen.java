package io.github.eco_warrior.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.eco_warrior.*;
import io.github.eco_warrior.screen.instructions.L1Instructions;
import io.github.eco_warrior.screen.instructions.L2Instructions;
import io.github.eco_warrior.screen.instructions.L3Instructions;

import java.util.ArrayList;

import static io.github.eco_warrior.constant.ConstantsVar.*;

public class SelectionScreen implements Screen {
    private Stage stage;
    private Texture[] frames;
    private float frameDuration = 0.1f; // Time per frame in seconds
    private float elapsedTime = 0;
    private int currentFrame = 0;
    private SpriteBatch batch;
    private Sound clickSound;
    private TextButton closeButton;
    private ArrayList<TextButton> buttons;

    private Main game;

    public SelectionScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Use the global MusicManager to play the music
        MusicManager.getInstance().playMusic();

        // Load the background textures
        frames = new Texture[]{
            new Texture(Gdx.files.internal("Background_Image/frame_000_delay-0.08s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_001_delay-0.08s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_002_delay-0.08s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_003_delay-0.08s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_004_delay-0.08s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_005_delay-0.08s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_006_delay-0.08s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_007_delay-0.08s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_008_delay-0.08s.gif")),
            new Texture(Gdx.files.internal("Background_Image/frame_009_delay-0.08s.gif"))
        };
        batch = new SpriteBatch();

        // Load the skin
        Skin skin = new Skin(Gdx.files.internal(UI_SKIN_PATH));

        // Generate the custom font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_TYPE));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36;
        BitmapFont customFont = generator.generateFont(parameter);
        generator.dispose();

        // Add the custom font to the skin
        skin.add("custom-font", customFont, BitmapFont.class);

        // Ensure TextButtonStyle exists in skin
        TextButton.TextButtonStyle buttonStyle = skin.get(TextButton.TextButtonStyle.class);
        if (buttonStyle == null) {
            buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = customFont;
            skin.add("default", buttonStyle);
        } else {
            buttonStyle.font = customFont;
        }

        // Load the click sound
        clickSound = Gdx.audio.newSound(Gdx.files.internal(BUTTON_CLICK_SFX));

        // Create the "<" button
        closeButton = new TextButton("<", buttonStyle);
        closeButton.setSize(50, 50);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                clickSound.play(SettingsManager.getInstance().getClickSoundVolume());
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(closeButton);

        // Create buttons for levels
        buttons = new ArrayList<>();
        buttons.add(createLevelButton(buttonStyle, "Begin Story Mode", new WorldMap(game)));
        buttons.add(createLevelButton(buttonStyle, "Level 1", new L1Instructions(game))); // Navigate to Level1Screen
        buttons.add(createLevelButton(buttonStyle, "Level 2", new L2Instructions(game))); // Navigate to Level2Screen
        buttons.add(createLevelButton(buttonStyle, "Level 3", new L3Instructions(game))); // Navigate to L3Instructions

        for (TextButton button : buttons) {
            stage.addActor(button);
        }

        // Position the buttons dynamically
        updateButtonPositions();
    }

    /**
     * Creates a level button that first navigates to a LoadingScreen and then redirects to the target screen.
     *
     * @param buttonStyle The style of the button.
     * @param buttonName  The text to display on the button.
     * @param targetScreen The screen to navigate to after the loading screen.
     * @return A TextButton instance.
     */
    private TextButton createLevelButton(TextButton.TextButtonStyle buttonStyle, String buttonName, Screen targetScreen) {
        // Create a level button
        TextButton levelButton = new TextButton(buttonName, buttonStyle);
        levelButton.setSize(400, 80);
        levelButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                System.out.println("Button clicked: " + buttonName); // Add debug output
                MusicManager.getInstance().stopMusic();
                clickSound.play(SettingsManager.getInstance().getClickSoundVolume());
                // Navigate to LoadingScreen first, and then to targetScreen
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new LoadingScreen(targetScreen));
            }
        });

        return levelButton;
    }

    private void updateButtonPositions() {
        float viewportWidth = stage.getViewport().getWorldWidth();
        float viewportHeight = stage.getViewport().getWorldHeight();

        // Calculate spacing between buttons
        float buttonHeight = buttons.get(0).getHeight();
        float totalButtonsHeight = buttons.size() * buttonHeight;
        float spacing = Math.min(30, (viewportHeight - totalButtonsHeight) / (buttons.size() + 1));

        float startY = viewportHeight / 2 + totalButtonsHeight /2;

        for(int i = 0; i < buttons.size(); i++) {
            TextButton button = buttons.get(i);
            button.setPosition(
                viewportWidth / 2 - button.getWidth() / 2,
                startY - i * (buttonHeight + spacing)
            );
        }

        // Position the closeButton at the bottom-left corner
        closeButton.setPosition(10, 10);
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;
        if (elapsedTime >= frameDuration) {
            elapsedTime = 0;
            currentFrame = (currentFrame + 1) % frames.length;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Ensure the batch uses the viewport's projection matrix
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);

        // Get the current viewport dimensions
        float viewportWidth = stage.getViewport().getWorldWidth();
        float viewportHeight = stage.getViewport().getWorldHeight();

        // Draw the background to fit the current viewport dimensions
        batch.begin();
        batch.draw(frames[currentFrame], 0, 0, viewportWidth, viewportHeight);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        batch.dispose();
        for (Texture frame : frames) {
            frame.dispose();
        }
        clickSound.dispose();
    }
}
