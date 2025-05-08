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
import io.github.eco_warrior.entity.ConveyorBelt;

import java.util.ArrayList;

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
    private ConveyorBelt conveyorBelt;
    private float stateTime;
    private final int CONVEYOR_SCALE = 10;
    // add more conveyor to extend whole screen
//    private ArrayList<Sprite> conveyorSprites;

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
        conveyorBelt = new ConveyorBelt("atlas/conveyor/conveyor.atlas",
            CONVEYOR_SCALE,
            viewport.getWorldHeight()/10f + 50f,
            viewport
            );
//        //get the sprite width first
//        TextureRegion firstFrame = conveyorAnimation.getKeyFrame(0);
//        float scaledWidth = firstFrame.getRegionWidth() * CONVEYOR_SCALE;
//        float y = viewport.getWorldHeight()/CONVEYOR_SCALE + 50;
//
//
//        //fill in all the conveyor length
//        for(float x = 0f; x < viewport.getWorldWidth() + scaledWidth; x += scaledWidth) {
//            Sprite conveyor = new Sprite(firstFrame);
//            conveyor.setScale(CONVEYOR_SCALE);
//            conveyor.setPosition(x,y);
//            conveyorSprites.add(conveyor);
//
//        }
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
    }

    private void draw() {
        camera.update();
        gl.glClear(GL32.GL_COLOR_BUFFER_BIT);

        maprenderer.setView(camera);
        maprenderer.render();

        stateTime = Gdx.graphics.getDeltaTime();
        conveyorBelt.update(stateTime);

//        TextureRegion region = conveyorAnimation.getKeyFrame(stateTime, true);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

//        //conveyors moving
//        for(Sprite conveyor: conveyorSprites) {
////            conveyor.flip(true, false);
//            conveyor.setRegion(region);
//            conveyor.flip(true, false); // Flip the conveyor
//            conveyor.draw(batch);
//        }
        conveyorBelt.draw(batch);

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
        conveyorBelt.dispose();

    }
}
