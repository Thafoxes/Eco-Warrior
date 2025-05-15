package io.github.eco_warrior.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SettingsScreen implements Screen {
    private SpriteBatch batch;
    private Stage stage;
    private Sound clickSound; // Click sound instance
    private TextButton closeButton; // "X" button instance
    private TextButton saveButton; // "Save" button instance
    private Label settingsLabel; // "Setting" label
    private Label volumeLabel; // "Volume" label
    private Slider volumeSlider; // Volume control slider
    private Label musicLabel; // "Music" label
    private Slider musicSlider; // Music volume control slider
    private BitmapFont customFont; // Custom font generated using FreeType
    private Texture backgroundTexture; // Background image

    public SettingsScreen() {
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        // Use the global MusicManager to play the music
        MusicManager.getInstance().playMusic();

        // Load the skin
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Load the click sound
        clickSound = Gdx.audio.newSound(Gdx.files.internal("Button_Click.mp3")); // Replace with your sound file

        // Load the background image
        backgroundTexture = new Texture(Gdx.files.internal("SettingPage.png")); // Replace with your image file

        // Create a FreeType font generator
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("cubic.ttf")); // Replace with your .ttf file
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36; // Set the font size
        customFont = generator.generateFont(parameter);
        generator.dispose(); // Dispose of the generator after use

        // Create the "Setting" label with the custom font
        settingsLabel = new Label("Setting", new Label.LabelStyle(customFont, null));
        stage.addActor(settingsLabel);

        // Create the "Volume" label
        volumeLabel = new Label("Background Music", new Label.LabelStyle(customFont, null));
        stage.addActor(volumeLabel);

        // Create the volume slider
        volumeSlider = new Slider(0, 1, 0.01f, false, skin);
        volumeSlider.setValue(MusicManager.getInstance().getMusicVolume()); // Set saved volume
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                MusicManager.getInstance().setMusicVolume(volume); // Update background music volume
            }
        });
        stage.addActor(volumeSlider);

        // Create the "Music" label
        musicLabel = new Label("Volume", new Label.LabelStyle(customFont, null));
        stage.addActor(musicLabel);

        // Create the music slider
        musicSlider = new Slider(0, 1, 0.01f, false, skin);
        musicSlider.setValue(SettingsManager.getInstance().getClickSoundVolume()); // Set saved click sound volume
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SettingsManager.getInstance().setClickSoundVolume(musicSlider.getValue());
            }
        });
        stage.addActor(musicSlider);

        // Create the "<" button
        TextButtonStyle buttonStyle = skin.get(TextButtonStyle.class);
        buttonStyle.font = customFont; // Set the font to the custom font
        closeButton = new TextButton("<", buttonStyle);
        closeButton.setSize(50, 50);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                clickSound.play(SettingsManager.getInstance().getClickSoundVolume());
                ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
            }
        });
        stage.addActor(closeButton);

        // Create the "Save" button
        saveButton = new TextButton("Save", buttonStyle);
        saveButton.setSize(100, 50);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                clickSound.play(SettingsManager.getInstance().getClickSoundVolume());
                SettingsManager.getInstance().setMusicVolume(volumeSlider.getValue());
                SettingsManager.getInstance().setClickSoundVolume(musicSlider.getValue());
                System.out.println("Settings saved!");
            }
        });
        stage.addActor(saveButton);

        // Update UI element positions
        updateElementPositions();
    }

    private void updateElementPositions() {
        float viewportWidth = stage.getViewport().getWorldWidth();
        float viewportHeight = stage.getViewport().getWorldHeight();

        settingsLabel.setPosition(viewportWidth / 2 - settingsLabel.getWidth() / 2, viewportHeight - settingsLabel.getHeight() - 20);
        volumeLabel.setPosition(viewportWidth / 2 - volumeLabel.getWidth() / 2, viewportHeight / 2 + 50);
        volumeSlider.setPosition(viewportWidth / 2 - volumeSlider.getWidth() / 2, volumeLabel.getY() - 40);
        musicLabel.setPosition(viewportWidth / 2 - musicLabel.getWidth() / 2, volumeSlider.getY() - 60);
        musicSlider.setPosition(viewportWidth / 2 - musicSlider.getWidth() / 2, musicLabel.getY() - 40);
        closeButton.setPosition(10, 10);
        saveButton.setPosition(closeButton.getX() + closeButton.getWidth() + 10, closeButton.getY());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        // Ensure the batch uses the viewport's projection matrix
        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);

        // Draw the background image
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        updateElementPositions();
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        customFont.dispose();
        clickSound.dispose();
        backgroundTexture.dispose();
    }
}
