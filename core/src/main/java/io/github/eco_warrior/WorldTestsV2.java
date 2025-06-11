package io.github.eco_warrior;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Matrix4;
import io.github.eco_warrior.MapLoader.MapLoader;
import io.github.eco_warrior.controller.PlayerController;
import io.github.eco_warrior.sprite.Characters.Goblin;
import io.github.eco_warrior.controller.DialogBox;

import static io.github.eco_warrior.constant.ConstantsVar.*;

public class WorldTestsV2 implements Screen {

    private MapLoader map;
    private OrthographicCamera camera;
    private float viewportWidth = TILES_TO_SHOW * WORLD_MAP_PIXEL_SIZE;
    private float viewportHeight = TILES_TO_SHOW * WORLD_MAP_PIXEL_SIZE;
    private Game game;

    private SpriteBatch batch;
    private Goblin goblin;
    private ShapeRenderer shapeRenderer;
    private Rectangle rect;
    private PlayerController playerController;

    // Dialog Box
    private DialogBox dialogBox;

    private static final float CAMERA_ZOOM = 1f; // 1f = no zoom

    public WorldTestsV2(Game game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();

        loadmap();
        createCharacter();
        setupCamera();
        batch = new SpriteBatch();
        playerController = new PlayerController(goblin);
        Gdx.input.setInputProcessor(playerController);

        // --- Dialog Box Setup (auto-sizing) ---
        BitmapFont font = new BitmapFont(); // Default font for testing
        dialogBox = new DialogBox(font, font);
    }

    private void createCharacter() {
        try {
            createGoblin();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void createGoblin() throws Exception {
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

        goblin = new Goblin(
            spawnPosition,
            tileWidth,
            tileHeight,
            (TiledMapTileLayer) map.getMap().getLayers().get("water background"),
            collisionObjectLayer.getObjects()
        );

        goblin.setPosition(
            goblin.getPosition().x + goblin.getHeight() / 2,
            goblin.getPosition().y + goblin.getHeight() / 2
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
            dialogBox.startDialog("Rival", new String[]{
                "Hi!",
                "This is a longer sentence to demonstrate the auto-sizing of the dialog box.",
                "Done."
            });
        }

        dialogBox.update();
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw world/game using world camera
        map.getRenderer().setView(camera);
        map.getRenderer().render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        goblin.draw(batch);
        batch.end();

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
            goblin.update(delta, map.getMap());
        }

        // --- Camera follows goblin, but clamps at map edges ---
        int mapPixelWidth = map.getMap().getProperties().get("width", Integer.class) * WORLD_MAP_PIXEL_SIZE;
        int mapPixelHeight = map.getMap().getProperties().get("height", Integer.class) * WORLD_MAP_PIXEL_SIZE;

        float halfViewportWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfViewportHeight = camera.viewportHeight * camera.zoom / 2f;

        float cameraX = Math.max(halfViewportWidth, Math.min(goblin.getPosition().x, mapPixelWidth - halfViewportWidth));
        float cameraY = Math.max(halfViewportHeight, Math.min(goblin.getPosition().y, mapPixelHeight - halfViewportHeight));

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

        MapLayer collisionObjectLayer = map.getMap().getLayers().get("Collision");
        if (collisionObjectLayer != null) {
            shapeRenderer.setColor(Color.BLUE);
            for (MapObject object : collisionObjectLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle r = ((RectangleMapObject) object).getRectangle();
                    shapeRenderer.rect(r.x, r.y, r.width, r.height);
                } else if (object instanceof EllipseMapObject) {
                    Ellipse e = ((EllipseMapObject) object).getEllipse();
                    shapeRenderer.ellipse(e.x, e.y, e.width, e.height);
                } else if (object instanceof PolygonMapObject) {
                    Polygon p = ((PolygonMapObject) object).getPolygon();
                    shapeRenderer.polygon(p.getTransformedVertices());
                }
            }
        }

        shapeRenderer.setColor(Color.GREEN);
        Rectangle goblinBox = new Rectangle(
            goblin.getPosition().x - goblin.getCurrentFrame().getRegionWidth() / 2f,
            goblin.getPosition().y - goblin.getCurrentFrame().getRegionHeight() / 2f,
            goblin.getCurrentFrame().getRegionWidth(),
            goblin.getCurrentFrame().getRegionHeight()
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
        goblin.dispose();
        shapeRenderer.dispose();
        dialogBox.dispose();
    }
}
