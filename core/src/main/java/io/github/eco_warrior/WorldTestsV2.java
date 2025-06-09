package io.github.eco_warrior;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.MapLoader.MapLoader;
import io.github.eco_warrior.controller.PlayerController;
import io.github.eco_warrior.sprite.Characters.Goblin;

import static io.github.eco_warrior.constant.ConstantsVar.*;

public class WorldTestsV2 implements Screen {

    private MapLoader map;

    //camera
    private OrthographicCamera camera;
    private float viewportWidth = TILES_TO_SHOW * WORLD_MAP_PIXEL_SIZE;  // Show 10 tiles horizontally
    private float viewportHeight = TILES_TO_SHOW * WORLD_MAP_PIXEL_SIZE; // Show 10 tiles vertically
    private Game game;

    private SpriteBatch batch;

    //Goblin character
    private Goblin goblin;

    //debug rectangle
    private ShapeRenderer shapeRenderer;
    private Rectangle rect;

    //controller
    private PlayerController playerController;

    //Camera
    // At class level, define zoom constant
    private static final float CAMERA_ZOOM = 1f; // Adjust value between 0-1 (smaller = more zoomed in)

    public WorldTestsV2(Game game){
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();

        loadmap();
        createCharacter();
        setupCamera();
        batch = new SpriteBatch();
        playerController = new PlayerController(goblin);
        Gdx.input.setInputProcessor(playerController);

    }

    private void createCharacter() {
        try{
            createGoblin();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void createGoblin() throws Exception {
        // Get the tile dimensions from the map
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getMap().getLayers().get("main_space");
        int tileWidth = layer.getTileWidth();
        int tileHeight = layer.getTileHeight();


        // Default spawn position in case no spawn point is found
        Vector2 spawnPosition = new Vector2(0,0);

        // Find spawn point from object layer
        MapLayer objectLayer = map.getMap().getLayers().get("spawn_layer");
        if (objectLayer != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectangleObject = (RectangleMapObject) object;
                    if ("spawn_point".equals(object.getName())) {
                        rect = rectangleObject.getRectangle();
                        spawnPosition.x = rectangleObject.getProperties().get("x", Float.class) ;
                        spawnPosition.y = rectangleObject.getProperties().get("y", Float.class);
                        break;
                    }else{
                        throw new Exception("Spawn point not found in the map. Please ensure the map has a spawn point defined.");
                    }
                }
            }
        }

        // Create goblin at spawn position
        goblin = new Goblin(
            spawnPosition,
            tileWidth,
            tileHeight,
            (TiledMapTileLayer) map.getMap().getLayers().get("water background")
        );

        // Set the goblin's initial position based on the rectangle
        goblin.setPosition(
            goblin.getPosition().x + goblin.getHeight()/2,
            goblin.getPosition().y + goblin.getHeight()/2
            );


    }

    private void loadmap() {

        map = new MapLoader();
        try {
            map.loadMap("maps/Gladesv2.tmx");
        } catch (Exception e) {
            throw new RuntimeException("Error loading map: " + e.getMessage(), e);
        }
    }

    private void setupCamera() {
        //Create and setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.zoom = CAMERA_ZOOM;
        camera.update();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
       draw();

    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(rect.x, rect.y, 32, 32); // Adjust size as needed
        shapeRenderer.end();

    }

    private void draw() {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.position.set(goblin.getPosition().x, goblin.getPosition().y, 2f);
        camera.update();

        // Render map
        map.getRenderer().setView(camera);
        map.getRenderer().render();

        //update controller
        playerController.update();


        drawGoblin();
        drawDebug();

    }

    private void drawGoblin() {
        // Update goblin
        goblin.update(Gdx.graphics.getDeltaTime(), map.getMap());

        batch.setProjectionMatrix(camera.combined);
        // Draw goblin
        batch.begin();
        goblin.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float)width / (float)height;
        viewportWidth = TILES_TO_SHOW * WORLD_MAP_PIXEL_SIZE * aspectRatio;
        camera.viewportWidth = viewportWidth;
        camera.viewportHeight = viewportHeight;
        camera.update();
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
        map.dispose();
        batch.dispose();
        goblin.dispose();
        shapeRenderer.dispose();
    }
}
