package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.controller.fontGenerator;
import io.github.eco_warrior.entity.ConveyorBelt;
import io.github.eco_warrior.entity.LevelMaker;
import io.github.eco_warrior.enums.textEnum;
import io.github.eco_warrior.sprite.RedBin;
import io.github.eco_warrior.sprite.TrashPile;

import static com.badlogic.gdx.Gdx.gl;
import static io.github.eco_warrior.constant.ConstantsVar.*;

/** First screen of the application. Displayed after the application is created. */
public class FirstLevelScreen extends LevelMaker implements Screen {

    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer maprenderer;
    private Viewport viewport;
    private Animation<TextureRegion> conveyorAnimation;

    private SpriteBatch batch;

    //conveyor
    private ConveyorBelt conveyorBelt;
    private float stateTime;
    private final int CONVEYOR_SCALE = 10;

    //uiBatch
    private fontGenerator scoreFont;
    private fontGenerator timerFont;
    private SpriteBatch uiBatch;

    //score
    private int score = 0;
    private float timerSeconds = 60f;
    private boolean timerEnded = false;

    //Sprites
    RedBin redBin;
    TrashPile trashPile;

    @Override
    public void show() {
        //init setting

        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();


        //setup camera to middle
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);
        camera.update();


        scoreFont = new fontGenerator();
        timerFont = new fontGenerator();
        loadMap();

        loadingConveyorAnimation();

        initSprites();

    }

    private void initSprites() {
        redBin = new RedBin(new Vector2(WINDOW_WIDTH / 2 , WINDOW_HEIGHT / 2 + 50f));
        trashPile = new TrashPile(new Vector2(WINDOW_WIDTH - 10f , WINDOW_HEIGHT / 5));

    }

    private void loadingConveyorAnimation() {
        conveyorBelt = new ConveyorBelt("atlas/conveyor/conveyor.atlas",
            CONVEYOR_SCALE,
            viewport.getWorldHeight()/10f + 50f, //some calculation only
            viewport
            );

        stateTime = 0f;
    }

    private void loadMap() {
        //load the map
        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
        parameters.generateMipMaps = false;
        map = new TmxMapLoader().load("maps/recycle_center_map.tmx", parameters);
        //create the renderer with 1 unit = 1 tile
        maprenderer = new OrthogonalTiledMapRenderer(map, 2f);
    }

    @Override
    public void render(float delta) {
        draw();
        countdownTimer();
        input();
    }

    private void input() {

        //if touch key to test my score up
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            redBin.correctSound();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            redBin.wrongSound();
        }
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            if(Gdx.input.justTouched()) redBin.isPressed(new Vector2(touch.x, touch.y));
        }

    }

    private void countdownTimer() {
        if (!timerEnded) {
            timerSeconds -= Gdx.graphics.getDeltaTime();
            if (timerSeconds <= 0) {
                //do something here
                isFinished();
            }
        }
    }


    private void draw() {
        camera.update();
        gl.glClear(GL32.GL_COLOR_BUFFER_BIT);

        maprenderer.setView(camera);
        maprenderer.render();

        stateTime = Gdx.graphics.getDeltaTime();


        batch.setProjectionMatrix(camera.combined);
        redBin.update(stateTime);
        trashPile.update(stateTime);
        conveyorBelt.update(stateTime);


        batch.begin();



        //recyclables here

        redBin.draw(batch);

        conveyorBelt.draw(batch);
        trashPile.draw(batch);




        batch.end();
        scoreFont.fontDraw(uiBatch, "Score: " + score , camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT - 10f), textEnum.LEFT, textEnum.TOP);
        timerFont.fontDraw(uiBatch, displayTimer(timerSeconds) , camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT - 10f), textEnum.RIGHT , textEnum.TOP);
    }


    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {

    }


    @Override
    public void dispose() {
        // Destroy screen's assets here.
        map.dispose();
        maprenderer.dispose();
        batch.dispose();
        conveyorBelt.dispose();
        scoreFont.dispose();
        timerFont.dispose();
        redBin.dispose();
        trashPile.dispose();
    }

    @Override
    protected void winningDisplay() {

    }

    @Override
    protected void losingDisplay() {

    }
}
