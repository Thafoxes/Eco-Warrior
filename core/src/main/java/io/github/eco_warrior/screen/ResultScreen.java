package io.github.eco_warrior.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.Main;
import io.github.eco_warrior.controller.fontGenerator;
import io.github.eco_warrior.enums.textEnum;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import static io.github.eco_warrior.constant.ConstantsVar.*;

public class ResultScreen implements Screen {

    private final Main game;
    private int score;

    private SpriteBatch batch;
    private fontGenerator uiFont;
    private SpriteBatch uiBatch;

    private Viewport viewport;
    private OrthographicCamera camera;

    private Stage stage;
    private Skin skin;



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
        //Ui interaction
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        createSkin();
    }

    private void createSkin() {
        skin = new Skin();
        skin.add("default-font", uiFont.getFont());

//        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
//        textButtonStyle.font = uiFont.getFont();
//        skin.add("default", textButtonStyle);

        //pixmap for button background
        Pixmap pixmap = new Pixmap(BUTTON_WIDTH, BUTTON_HEIGHT, Pixmap.Format.RGBA8888);

        //draw border
        pixmap.setColor(Color.BLACK);
        pixmap.fillRectangle(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);

        //draw button background, basically this is just recolour but smaller size to show this is background.
        pixmap.setColor(Color.LIGHT_GRAY);
        pixmap.fillRectangle(2,2, BUTTON_WIDTH - 4, BUTTON_HEIGHT - 4);

        TextureRegionDrawable buttonBackground = new TextureRegionDrawable(
            new TextureRegion(
                new Texture(pixmap)
            )
        );
        skin.add("button-bg", buttonBackground, Drawable.class);



        //hover pixmap
        Pixmap hoverPixmap = new Pixmap(BUTTON_WIDTH, BUTTON_HEIGHT, Pixmap.Format.RGBA8888);
        hoverPixmap.setColor(Color.BLACK);
        hoverPixmap.fillRectangle(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);

        hoverPixmap.setColor(Color.DARK_GRAY);
        hoverPixmap.fillRectangle(2,2, BUTTON_WIDTH - 4, BUTTON_HEIGHT - 4);

        TextureRegionDrawable hoverBackground = new TextureRegionDrawable(
            new TextureRegion(
                new Texture(hoverPixmap)
            )
        );
        skin.add("button-hover-bg", hoverBackground, Drawable.class);





        //setup font
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = uiFont.getFont();
        buttonStyle.up = skin.getDrawable("button-bg"); // default
        buttonStyle.over = skin.getDrawable("button-hover-bg"); //hover effect
        buttonStyle.down = skin.getDrawable("button-bg");

        skin.add("button-style", buttonStyle);

        pixmap.dispose();
        hoverPixmap.dispose();

        createButton();
    }

    private void createButton() {
        //create button
        float horizontalPadding = 20f;
        TextButton backButton = new TextButton("Return to main menu", skin, "button-style");
        backButton.pack();
        backButton.setSize(backButton.getWidth() + horizontalPadding, backButton.getHeight());
        backButton.setPosition((WINDOW_WIDTH - backButton.getWidth()) / 2, (WINDOW_HEIGHT - backButton.getHeight()) / 2);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen();
                //return back to main menu
                System.out.println("back button clicked");
            }
        });

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

            uiFont.fontDraw(uiBatch, "Your score is " + score + "\n", camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT - 10f), textEnum.X_CENTER, textEnum.Y_MIDDLE);

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
