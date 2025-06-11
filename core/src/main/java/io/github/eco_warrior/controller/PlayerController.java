package io.github.eco_warrior.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.sprite.Characters.Goblin;

public class PlayerController implements InputProcessor {

        private Goblin player;
        private boolean left;
        private boolean right;
        private boolean up;
        private boolean down;

    public PlayerController(Goblin player) {
        this.player = player;
    }
    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.A:
                left = true;
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                right = true;
                break;
            case Input.Keys.UP:
            case Input.Keys.W:
                up = true;
                break;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                down = true;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.A:
                left = false;
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                right = false;
                break;
            case Input.Keys.UP:
            case Input.Keys.W:
                up = false;
                break;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                down = false;
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void update(float delta) {
        float speed = 1f;
       float dx = 0, dy = 0;

        if (left) dx -= speed;
        if (right) dx += speed;
        if (up) dy += speed;
        if (down) dy -= speed;

        // Move goblin if there's movement input
        if (dx != 0 && dy != 0) {
            //normalize diagonal movement
            float length = (float)Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;
        }

        // Always call move to update velocity
        // This ensures velocity is set to 0 when no keys are pressed
        player.move(new Vector2(dx, dy));
    }
}
