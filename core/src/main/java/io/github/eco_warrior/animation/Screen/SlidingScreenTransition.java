package io.github.eco_warrior.animation.Screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SlidingScreenTransition {
    private boolean isActive = false;
    private float progress = 0f;
    private float duration;
    private Game game;
    private Screen currentScreen;
    private Screen targetScreen;
    private SpriteBatch batch;

    public SlidingScreenTransition(Game game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.duration = 1.0f; // Default duration
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void startTransition(Screen currentScreen, Screen targetScreen) {
        this.currentScreen = currentScreen;
        this.targetScreen = targetScreen;
        this.isActive = true;
        this.progress = 0f;
    }

    public boolean isActive() {
        return isActive;
    }

    public void update(float delta) {
        if (!isActive) return;

        progress += delta / duration;
        if (progress >= 1f) {
            // Transition is complete, switch to the target screen
            game.setScreen(targetScreen);
            isActive = false;
            progress = 0f;
        }
    }

    public void render(float delta) {
        if (!isActive) return;

        float width = Gdx.graphics.getWidth();
        float offset = width * progress;

        // Render the current screen sliding out
        batch.begin();
        currentScreen.render(delta);
        batch.end();

        // Save the original transform matrix
        com.badlogic.gdx.math.Matrix4 originalMatrix = new com.badlogic.gdx.math.Matrix4(batch.getTransformMatrix());

        // Apply translation for the target screen
        batch.setTransformMatrix(batch.getTransformMatrix().trn(-width + offset, 0, 0));
        batch.begin();
        targetScreen.render(delta);
        batch.end();

        // Restore the original transform matrix
        batch.setTransformMatrix(originalMatrix);
    }
}
