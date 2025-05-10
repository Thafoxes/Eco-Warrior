package io.github.eco_warrior.screen;

import com.badlogic.gdx.Screen;
import io.github.eco_warrior.Main;

public class ResultScreen implements Screen {

    private Main game;
    private int score;
    boolean result = false;


    public ResultScreen(Main game, int score, boolean result) {
        this.game = game;
        this.score = score;
        this.result = result;

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

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

    }
}
