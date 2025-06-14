package io.github.eco_warrior;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Matrix4;
import io.github.eco_warrior.MapLoader.MapLoader;
import io.github.eco_warrior.controller.MapController;
import io.github.eco_warrior.controller.PlayerController;
import io.github.eco_warrior.controller.FontGenerator;
import io.github.eco_warrior.sprite.Characters.Adventurer_Girl;
import io.github.eco_warrior.controller.DialogBox;

import java.util.Arrays;

import static io.github.eco_warrior.constant.ConstantsVar.*;

public class WorldTestsV2 implements Screen {

    public static final int LAYER_MAP = 4;
    private MapLoader map;
    private MapController mapController;
    private OrthographicCamera camera;
    private float viewportWidth = TILES_TO_SHOW * WORLD_MAP_PIXEL_SIZE;
    private float viewportHeight = TILES_TO_SHOW * WORLD_MAP_PIXEL_SIZE;
    private Game game;

    private SpriteBatch batch;
    private Adventurer_Girl adventurerGirl;
    private ShapeRenderer shapeRenderer;
    private Rectangle rect;
    private PlayerController playerController;


    // Dialog Box
    private DialogBox dialogBox;
    private FontGenerator dialogFont, speakerFont;

    private static final float CAMERA_ZOOM = 1f; // 1f = no zoom

    //map layers
    // Get total number of layers
    private int numLayers = 0 ;

    public WorldTestsV2(Game game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();

        LoadMap();
        createCharacter();
        setupCamera();
        batch = new SpriteBatch();
        playerController = new PlayerController(adventurerGirl);
        Gdx.input.setInputProcessor(playerController);

        // --- Dialog Box Setup (auto-sizing) ---
        dialogFont = new FontGenerator(16, Color.WHITE, Color.BLACK);
        speakerFont = new FontGenerator(20, Color.YELLOW, Color.BLACK);
        dialogBox = new DialogBox(dialogFont.getFont(), speakerFont.getFont());
    }

    private void createCharacter(){
        try {
            CreateCharacter();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    private void CreateCharacter() throws Exception {
        MapController result = getGetMapInfo();


        adventurerGirl = new Adventurer_Girl(
            result.spawnPosition,
            result.tileWidth,
            result.tileHeight,
            (TiledMapTileLayer) map.getMap().getLayers().get("water background"),
            result.allCollisionObjects,
            mapController
        );

        adventurerGirl.setPosition(
            adventurerGirl.getPosition().x + adventurerGirl.getHeight() / 2,
            adventurerGirl.getPosition().y + adventurerGirl.getHeight() / 2
        );

    }


    //Overriding Map Controller
    public MapController getGetMapInfo() throws Exception {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getMap().getLayers().get("main_space");
        int tileWidth = layer.getTileWidth();
        int tileHeight = layer.getTileHeight();

        Vector2 spawnPosition = new Vector2(0, 0);
        MapLayer objectLayer = map.getMap().getLayers().get("spawn_layer");
        boolean foundSpawn = false;
        if (objectLayer != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectangleObject = (RectangleMapObject) object;
                    if ("spawn_point".equals(object.getName())) {
                        rect = rectangleObject.getRectangle();
                        spawnPosition.x = rectangleObject.getProperties().get("x", Float.class);
                        spawnPosition.y = rectangleObject.getProperties().get("y", Float.class);
                        foundSpawn = true;
                        break;
                    }
                }
            }
        }
        if (!foundSpawn) {
            throw new Exception("Spawn point not found in the map. Please ensure the map has a spawn point defined.");
        }

        MapLayer collisionObjectLayer = map.getMap().getLayers().get("Collision");
        if (collisionObjectLayer == null) {
            throw new Exception("Collision object layer not found in the map.");
        }

        return new MapController(tileWidth, tileHeight, spawnPosition);
    }


    private void LoadMap() {
        map = new MapLoader();
        try {
            map.loadMap("maps/Gladesv2.tmx");
            this.mapController = getGetMapInfo();

            this.mapController.loadCollisionTiles(map.getMap());
            this.mapController.loadCollisionObjects(map.getMap(), "Collision");
        } catch (Exception e) {
            throw new RuntimeException("Error loading map: " + e.getMessage(), e);
        }
        numLayers = map.getMap().getLayers().size();
    }

    private void setupCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.zoom = CAMERA_ZOOM;
        camera.update();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // For demo: trigger dialog with D key
        if (!dialogBox.isVisible() && Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            dialogBox.startDialog("Rival", Arrays.asList(
                "Hi!",
                "This is a longer sentence to demonstrate the auto-sizing of the dialog box.",
                "Done."
            ));
        }

        dialogBox.update();
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw world/game using world camera
        map.getRenderer().setView(camera);
        int[] backgroundLayers = new int[numLayers - LAYER_MAP];
        for (int i = 0; i < numLayers - LAYER_MAP; i++) {
            backgroundLayers[i] = i;
        }
        map.getRenderer().render(backgroundLayers);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        adventurerGirl.draw(batch);
        batch.end();

        // Draw the top 3 layers above the character
        int[] foregroundLayers = new int[LAYER_MAP];
        for (int i = 0; i < LAYER_MAP; i++) {
            foregroundLayers[i] = numLayers - LAYER_MAP + i;
        }
        map.getRenderer().render(foregroundLayers);

        // Draw DialogBox in screen coordinates so it is always visible
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.begin();
        dialogBox.render(batch);
        batch.end();

        drawDebug();
    }

    private void update(float delta) {
        // Pause game logic while dialog is visible
        if (!dialogBox.isVisible()) {
            playerController.update(delta);
            adventurerGirl.update(delta, map.getMap());
        }

        // --- Camera follows goblin, but clamps at map edges ---
        int mapPixelWidth = map.getMap().getProperties().get("width", Integer.class) * WORLD_MAP_PIXEL_SIZE;
        int mapPixelHeight = map.getMap().getProperties().get("height", Integer.class) * WORLD_MAP_PIXEL_SIZE;

        float halfViewportWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfViewportHeight = camera.viewportHeight * camera.zoom / 2f;

        float cameraX = Math.max(halfViewportWidth, Math.min(adventurerGirl.getPosition().x, mapPixelWidth - halfViewportWidth));
        float cameraY = Math.max(halfViewportHeight, Math.min(adventurerGirl.getPosition().y, mapPixelHeight - halfViewportHeight));

        camera.position.set(cameraX, cameraY, 0f); // z=0 for 2D
        camera.update();
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);


        if (rect != null) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }


        mapController.drawDebug(shapeRenderer, Color.BLUE);
        shapeRenderer.setColor(Color.GREEN);
        Rectangle goblinBox = new Rectangle(
            adventurerGirl.getPosition().x - adventurerGirl.getCurrentFrame().getRegionWidth() / 2f,
            adventurerGirl.getPosition().y - adventurerGirl.getCurrentFrame().getRegionHeight() / 2f,
            adventurerGirl.getCurrentFrame().getRegionWidth(),
            adventurerGirl.getCurrentFrame().getRegionHeight()
        );
        shapeRenderer.rect(goblinBox.x, goblinBox.y, goblinBox.width, goblinBox.height);



        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        viewportWidth = TILES_TO_SHOW * WORLD_MAP_PIXEL_SIZE * aspectRatio;
        camera.viewportWidth = viewportWidth;
        camera.viewportHeight = viewportHeight;
        camera.update();
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        map.dispose();
        batch.dispose();
        adventurerGirl.dispose();
        shapeRenderer.dispose();
        dialogBox.dispose();
        dialogFont.dispose();
        speakerFont.dispose();
    }
}
