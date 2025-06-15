package io.github.eco_warrior.animation.Screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class ScreenTransition {
    private boolean isActive = false;
    private float alpha = 0f;
    private float duration;
    private Game game;
    private Screen targetScreen;
    private ShapeRenderer shapeRenderer;

    public ScreenTransition(Game game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.duration = 1.0f; // Default duration
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void startTransition(Screen targetScreen) {
        this.targetScreen = targetScreen;
        this.isActive = true;
        this.alpha = 0f;
    }

    public boolean isActive() {
        return isActive;
    }

    public void update(float delta) {
        if (!isActive) return;

        alpha += delta / duration;
        if (alpha >= 1f) {
            // Transition is complete, change screen
            game.setScreen(targetScreen);
            isActive = false;
            alpha = 0f;
        }
    }

    public void render() {
        if (!isActive) return;

        // Draw fade overlay
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, alpha);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
