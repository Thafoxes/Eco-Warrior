package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.Gdx.gl;
import static io.github.eco_warrior.constant.ConstantsVar.*;

/** First screen of the application. Displayed after the application is created. */
public class FirstLevelScreen implements Screen {

    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer maprenderer;
    private Viewport viewport;
    private Animation<TextureRegion> conveyorAnimation;

    private SpriteBatch batch;

    //conveyor
    private TextureAtlas conveyorAtlas;
    private Sprite conveyor;
    private float stateTime;
    private final int CONVEYOR_SCALE = 10;



    @Override
    public void show() {
        //init setting

        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        batch = new SpriteBatch();


        //setup camera to middle
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);
        camera.update();

        loadMap();

        loadingConveyorAnimation();

    }

    private void loadingConveyorAnimation() {
        conveyorAtlas = new TextureAtlas("atlas/conveyor/conveyor.atlas");
        conveyorAnimation = new Animation(0.1f, conveyorAtlas.findRegions("image"),
            Animation.PlayMode.LOOP);

        conveyor = new Sprite(conveyorAnimation.getKeyFrame(0));
        conveyor.scale(CONVEYOR_SCALE);
        Vector2 conveyorPosition = new Vector2(viewport.getWorldWidth()/CONVEYOR_SCALE + 1, viewport.getWorldHeight()/CONVEYOR_SCALE + 50);

        conveyor.setPosition(conveyorPosition.x,conveyorPosition.y);
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

        camera.update();
        gl.glClear(GL32.GL_COLOR_BUFFER_BIT);

        maprenderer.setView(camera);
        maprenderer.render();

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion region = conveyorAnimation.getKeyFrame(stateTime, true);
        conveyor.setRegion(region);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        conveyor.flip(true, false);
        conveyor.draw(batch);

        batch.end();
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
    }
}
