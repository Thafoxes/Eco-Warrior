package io.github.eco_warrior.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.Enemy.WormController;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.sprite.Enemy.Worm;

public class WormTestScreen implements Screen {
    private Worm worm;
    private SpriteBatch batch;
    private WormController wormController;

    @Override
    public void show() {
        batch = new SpriteBatch();
        worm = new Worm(new Vector2(100, 100));
        wormController = new WormController(worm);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        wormController.update(delta);

        batch.begin();
        wormController.draw(batch);
        batch.end();

        HandleInput();
    }

    private void HandleInput() {
        // Test controls
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            worm.attack();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            worm.die();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            worm.setDirection(true);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            worm.setDirection(false);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            worm.setState(Enemies.EnemyState.MOVING);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        worm.dispose();
        wormController.dispose();
    }

    // Other Screen methods remain empty
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
