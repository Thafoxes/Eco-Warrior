package io.github.eco_warrior;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import static io.github.eco_warrior.constant.ConstantsVar.*;

/** First screen of the application. Displayed after the application is created. */
public class FirstLevelScreen implements Screen {

    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer maprenderer;

    @Override
    public void show() {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);

        //load the map
        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
        parameters.generateMipMaps = false;
        map = new TmxMapLoader().load("maps/recycle_map.tmx", parameters);


        //create the renderer with 1 unit = 1 tile
        maprenderer = new OrthogonalTiledMapRenderer(map, 4f);

    }

    @Override
    public void render(float delta) {

        camera.update();
        maprenderer.setView(camera);
        maprenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
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
    }
}
