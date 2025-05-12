package io.github.eco_warrior;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.entity.tool;
import io.github.eco_warrior.sprite.tools.DuctTape;
import io.github.eco_warrior.sprite.tools.PipeWrench;
import io.github.eco_warrior.sprite.tools.WaterSpray;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.Gdx.gl;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;

public class LevelThreeScreen implements Screen {


    private Main game;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private SpriteBatch uiBatch;

    //map
    private TiledMap map;
    private OrthogonalTiledMapRenderer maprenderer;


    //delta time
    private float stateTime;

    //tools
    private Map<String, tool> tools = new HashMap<>();


    public LevelThreeScreen(Main main) {
        this.game = main;

    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();

        //setup camera to middle
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);
        camera.update();

        loadMap();
        initializeTools();
    }

    private void initializeTools() {
        int toolCount = 4;
        float spacing = WINDOW_WIDTH / (toolCount + 1);
        float toolScale = 5f;

        float toolWidth = new DuctTape(new Vector2(0, 0), toolScale).getSprite().getWidth();
        tools.put("duct_tape", new DuctTape(new Vector2(spacing - toolWidth, WINDOW_HEIGHT/10), toolScale));
        tools.put("water_spray", new WaterSpray(new Vector2(spacing * 2 - toolWidth, WINDOW_HEIGHT/10), toolScale));
        tools.put("pipe_wrench", new PipeWrench(new Vector2(spacing * 3 - toolWidth, WINDOW_HEIGHT/10), toolScale));
        tools.put("asda", new DuctTape(new Vector2(spacing * 4 - toolWidth, WINDOW_HEIGHT/10), toolScale));

    }

    private void loadMap(){
        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
        parameters.generateMipMaps = false;
        map = new TmxMapLoader().load("maps/water_map.tmx", parameters);

        maprenderer = new OrthogonalTiledMapRenderer(map, 4f);
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

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //display tools
        for (tool tool : tools.values()) {
            tool.draw(batch);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
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
        uiBatch.dispose();
        batch.dispose();
        maprenderer.dispose();
        map.dispose();
        for (tool tool : tools.values()) {
            tool.dispose();
        }
    }
}
