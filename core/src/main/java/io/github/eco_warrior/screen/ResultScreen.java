package io.github.eco_warrior.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.Main;
import io.github.eco_warrior.controller.fontGenerator;
import io.github.eco_warrior.enums.textEnum;

import java.awt.*;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;
import static java.awt.Color.LIGHT_GRAY;

public class ResultScreen implements Screen {

    private Main game;
    private int score;

    private SpriteBatch batch;
    private fontGenerator uiFont;
    private SpriteBatch uiBatch;

    private Viewport viewport;
    private OrthographicCamera camera;

    public ResultScreen(Main game, int score) {
        this.game = game;
        this.score = score;

    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        uiFont = new fontGenerator();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        uiBatch = new SpriteBatch();
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

            batch.end();
            uiFont.fontDraw(uiBatch, "Your score is " + score + "\nBack to main menu", camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT - 10f), textEnum.X_CENTER, textEnum.Y_MIDDLE);
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
    }
}
