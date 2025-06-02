package io.github.eco_warrior.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.Main;
import io.github.eco_warrior.controller.buttonGenerator;
import io.github.eco_warrior.controller.fontGenerator;
import io.github.eco_warrior.enums.textEnum;
import io.github.eco_warrior.mainmenu.MainMenuScreen;

import static io.github.eco_warrior.constant.ConstantsVar.*;

public class ResultScreen implements Screen {

    private boolean isGameOver = false;
    private final Main game;
    private int score;

    private SpriteBatch batch;
    private fontGenerator uiFont;
    private SpriteBatch uiBatch;

    private Viewport viewport;
    private OrthographicCamera camera;

    //button setup
    private Stage stage;
    private Skin skin;

    private buttonGenerator buttonGenerator;

    private String additionalMessage = "";


    public ResultScreen(Main game, int score, buttonGenerator buttonGenerator) {
        this.game = game;
        this.score = score;
        this.buttonGenerator = buttonGenerator;
    }

    public ResultScreen(Main game, int score) {
        this.game = game;
        this.score = score;
        buttonGenerator = game.getButtonFactory();
    }

    public ResultScreen(Main game, int score, boolean isGameOver, String additionalMessage) {
        this.game = game;
        this.score = score;
        this.isGameOver = isGameOver;
        buttonGenerator = game.getButtonFactory();
        this.additionalMessage = additionalMessage;
    }

    public ResultScreen(Main game, int score, boolean isGameOver) {
        this.game = game;
        this.score = score;
        this.isGameOver = isGameOver;
        buttonGenerator = game.getButtonFactory();
        this.additionalMessage = "";
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        uiFont = new fontGenerator();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        uiBatch = new SpriteBatch();
        //Ui interaction
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        createReturnButton();
    }

    private void createReturnButton() {
       TextButton backButton = buttonGenerator.createDefaultButton(
           "Return to main menu",
           WINDOW_WIDTH,
           WINDOW_HEIGHT
       );
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen();
                //return back to main menu
                game.setScreen(new MainMenuScreen(game));
//                System.out.println("back button clicked");
            }
        });

//        //add custom button here check if custom button design works
//        buttonFactory.createCustomButton(
//            "modernista",
//            Color.BLUE,
//            Color.BROWN,
//            Color.CHARTREUSE);
//        TextButton exitButton = buttonFactory.createNewButton(
//            "Test button",
//            "modernista",
//            WINDOW_WIDTH/4,
//            WINDOW_HEIGHT
//
//        );
//        stage.addActor(exitButton);


        stage.addActor(backButton);
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
            stage.act(delta);
            stage.draw();

            String resultMessage = isGameOver ?  "Game Over! ": "Challenge completed! ";
            uiFont.fontDraw(uiBatch, resultMessage + additionalMessage, camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT ), textEnum.X_CENTER, textEnum.TOP);
            uiFont.fontDraw(uiBatch, "Your score is " + score + "\n", camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT ), textEnum.X_CENTER, textEnum.BOTTOM);

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
        stage.dispose();

    }
}
