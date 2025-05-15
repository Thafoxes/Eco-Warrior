package io.github.eco_warrior.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LoadingScreen implements Screen {
    private Stage stage;
    private Texture[] frames;
    private float frameDuration = 0.1f; // Time per frame in seconds
    private float frameElapsedTime = 0; // Time elapsed for the current frame
    private float totalElapsedTime = 0; // Total time elapsed for the loading screen
    private int currentFrame = 0;
    private SpriteBatch batch;
    private float maxLoadingTime = 5f; // Total time to display the loading screen
    private Screen nextScreen; // The screen to navigate to after loading

    // Constructor to accept the target screen
    public LoadingScreen(Screen nextScreen) {
        this.nextScreen = nextScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load the background texture
        frames = new Texture[]{
            new Texture(Gdx.files.internal("frame_0_delay-0.22s.gif")),
            new Texture(Gdx.files.internal("frame_1_delay-0.22s.gif")),
            new Texture(Gdx.files.internal("frame_2_delay-0.22s.gif")),
            new Texture(Gdx.files.internal("frame_3_delay-0.22s.gif")),
            // Add more frames as needed
        };
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        // Update the total elapsed time
        totalElapsedTime += delta;

        // Update the frame elapsed time and current frame
        frameElapsedTime += delta;
        if (frameElapsedTime >= frameDuration) {
            frameElapsedTime = 0;
            currentFrame = (currentFrame + 1) % frames.length;
        }

        // Check if max loading time is reached
        if (totalElapsedTime >= maxLoadingTime) {
            ((com.badlogic.gdx.Game) Gdx.app.getApplicationListener()).setScreen(nextScreen); // Navigate to the next screen
            return;
        }

        // Clear the screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
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

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
    }
}
