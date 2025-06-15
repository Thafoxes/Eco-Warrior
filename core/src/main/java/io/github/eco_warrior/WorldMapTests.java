package io.github.eco_warrior;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.eco_warrior.sprite.Characters.Goblin;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;

/**
 * Test class for the world map with dead-zone camera implementation.
 * This provides a test environment where a character can move around the map.
 * The camera follows the character using a dead-zone approach, but can be
 * switched to center-lock mode.
 */
public class WorldMapTests implements Screen {


    // Camera settings
    private static final float CAMERA_SPEED = 5.0f;
    private static final float DEAD_ZONE_WIDTH = 200;
    private static final float DEAD_ZONE_HEIGHT = 150;
    private static final float FIXED_ZOOM = 0.3f; // Fixed zoom level

    // Map and rendering
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    // Character variables
    private Goblin goblin;
    private static final float CHARACTER_SPEED = 120.0f;

    // Map objects and layers
    private int tileWidth, tileHeight; // Tile dimensions from the map
    private TiledMapTileLayer collisionLayer; // Layer containing collision tiles

    // Camera modes
    private boolean centerLockMode = false;
    private Rectangle deadZone;

    // Reference to the game
    private final Game game;
    private InputProcessor inputProcessor;

    public WorldMapTests(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Initialize batch for rendering
        batch = new SpriteBatch();

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set fixed zoom level
        camera.zoom = FIXED_ZOOM;

        // Set up simple input processor for character control
        inputProcessor = new InputAdapter() {
            // Basic input handling if needed
        };
        Gdx.input.setInputProcessor(inputProcessor);

        // load map with error handling
        try {
            loadMap();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadMap() throws Exception {
        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
        parameters.generateMipMaps = false;
        tiledMap = new TmxMapLoader().load("maps/Gladesv2.tmx", parameters);

        // Get map dimensions
        MapProperties properties = tiledMap.getProperties();
        int mapTileWidth = properties.get("width", Integer.class);
        int mapTileHeight = properties.get("height", Integer.class);
        tileWidth = properties.get("tilewidth", Integer.class);
        tileHeight = properties.get("tileheight", Integer.class);

        int mapWidth = mapTileWidth * tileWidth;
        int mapHeight = mapTileHeight * tileHeight;

        // Get collision layer (name water background in Tiled)
        collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("water background");

        if (collisionLayer == null) {
            // Use the first layer as collision if no specific collision layer exists
            collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
            System.out.println("No collision layer found. Using first layer for collisions.");
        }

        // Calculate the scale factor to fit the map to the screen
        float scaleX = WINDOW_WIDTH / mapWidth;
        float scaleY = WINDOW_HEIGHT / mapHeight;
        float scale = Math.min(scaleX, scaleY); // Use the smaller scale to ensure the entire map fits

        // Initialize renderer with the calculated scale
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, scale);

        // Initialize default position for goblin
        float goblinX = WINDOW_WIDTH / 2;
        float goblinY = WINDOW_HEIGHT / 2;

        // Try to find spawn point from object layer named "spawn_point"
        MapLayer objectLayer = tiledMap.getLayers().get("spawn_point");
        if (objectLayer != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if (object instanceof RectangleMapObject && "spawn_point".equals(object.getName())) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    goblinX = rect.x;
                    goblinY = rect.y;
                    System.out.println("Found spawn point at: " + goblinX + ", " + goblinY);
                    break; // Use the first spawn point found
                }
            }
        }

        // Get the collision object layer (object layer with shapes and collidable=true)
        MapLayer collisionObjectLayer = tiledMap.getLayers().get("Collision");

        initializeGoblin(goblinX, goblinY, collisionObjectLayer);

        // Initialize dead zone for camera
        deadZone = new Rectangle(
            WINDOW_WIDTH / 2 - DEAD_ZONE_WIDTH / 2,
            WINDOW_HEIGHT / 2 - DEAD_ZONE_HEIGHT / 2,
            DEAD_ZONE_WIDTH,
            DEAD_ZONE_HEIGHT
        );

        camera.update();
    }

    private void initializeGoblin(float goblinX, float goblinY, MapLayer collisionObjectLayer) {
        goblin = new Goblin(
            new Vector2(goblinX, goblinY),
            tileWidth,
            tileHeight,
            collisionLayer,
            collisionObjectLayer != null ? collisionObjectLayer.getObjects() : null
        );
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle input and update character position
        handleInput();
        goblin.update(delta, tiledMap); // Add this line to update goblin position

        // Update camera based on character position
        updateCamera();

        // Render the map
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        // Render the character
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        goblin.draw(batch);
        batch.end();
    }

    /**
     * Handles keyboard input for character movement.
     */
    private void handleInput() {
        Vector2 velocity = new Vector2(0, 0);

        // Handle movement input
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.y = CHARACTER_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y = -CHARACTER_SPEED;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = -CHARACTER_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x = CHARACTER_SPEED;
        }

        // Normalize diagonal movement
        if (velocity.len() > 0) {
            velocity.nor().scl(CHARACTER_SPEED);
        }

        // Set the velocity on the goblin
        goblin.setVelocity(velocity.x, velocity.y);
    }

    @Override
    public void resize(int width, int height) {
        // Calculate the correct aspect ratio
        float aspectRatio = (float) WINDOW_WIDTH / (float) WINDOW_HEIGHT;
        float screenWidth = width;
        float screenHeight = height;

        // Maintain aspect ratio
        if (width / (float) height >= aspectRatio) {
            screenWidth = height * aspectRatio;
        } else {
            screenHeight = width / aspectRatio;
        }

        // Center the viewport
        int viewportX = (int) ((width - screenWidth) / 2);
        int viewportY = (int) ((height - screenHeight) / 2);

        // Apply viewport
        Gdx.gl.glViewport(viewportX, viewportY, (int) screenWidth, (int) screenHeight);

        // Update camera viewport
        camera.viewportWidth = WINDOW_WIDTH;
        camera.viewportHeight = WINDOW_HEIGHT;

        // Maintain fixed zoom level
        camera.zoom = FIXED_ZOOM;

        camera.update();

        // If the map is already loaded, update the renderer view
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
        }
    }

    @Override
    public void pause() {
        // Pause game logic if needed
    }

    @Override
    public void resume() {
        // Resume game logic if needed
    }

    @Override
    public void hide() {
        // Screen is being hidden - clean up input processor
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        // Dispose of resources to prevent memory leaks
        if (tiledMap != null) tiledMap.dispose();
        if (goblin != null) goblin.dispose();
        if (batch != null) batch.dispose();
    }

    /**
     * Updates camera position based on character position.
     * Uses dead-zone or center-lock approach depending on the current mode.
     */
    private void updateCamera() {
        Vector2 position = goblin.getPosition();

        // Add this line to round the character position to prevent micro-movements
        Vector2 roundedPosition = new Vector2(
            Math.round(position.x * 10) / 10f,
            Math.round(position.y * 10) / 10f
        );

        if (centerLockMode) {
            // Center-lock mode: camera directly follows the character
            camera.position.x = MathUtils.lerp(camera.position.x, position.x, CAMERA_SPEED * Gdx.graphics.getDeltaTime());
            camera.position.y = MathUtils.lerp(camera.position.y, position.y, CAMERA_SPEED * Gdx.graphics.getDeltaTime());
        } else {
            // Dead-zone mode: camera only moves when character exits the dead zone
            Vector3 cameraPosition = camera.position;

            // Calculate world coordinates of dead zone edges, considering zoom
            float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
            float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

            // Calculate world coordinates of dead zone edges
            float leftEdge = cameraPosition.x - effectiveViewportWidth / 2f + (deadZone.x * camera.zoom);
            float rightEdge = leftEdge + (deadZone.width * camera.zoom);
            float bottomEdge = cameraPosition.y - effectiveViewportHeight / 2f + (deadZone.y * camera.zoom);
            float topEdge = bottomEdge + (deadZone.height * camera.zoom);

            float targetX = cameraPosition.x;
            float targetY = cameraPosition.y;

            // Move camera if character is outside the dead zone
            if (roundedPosition.x < leftEdge) {
                targetX -= leftEdge - roundedPosition.x;
            } else if (roundedPosition.x > rightEdge) {
                targetX += roundedPosition.x - rightEdge;
            }

            if (roundedPosition.y < bottomEdge) {
                targetY -= bottomEdge - roundedPosition.y;
            } else if (roundedPosition.y > topEdge) {
                targetY += roundedPosition.y - topEdge;
            }

            // Apply smoothed camera movement with increased smoothing factor
            cameraPosition.x = MathUtils.lerp(cameraPosition.x, targetX, (CAMERA_SPEED * 0.8f) * Gdx.graphics.getDeltaTime());
            cameraPosition.y = MathUtils.lerp(cameraPosition.y, targetY, (CAMERA_SPEED * 0.8f) * Gdx.graphics.getDeltaTime());
        }

        // Apply map boundaries to the camera
        if (tiledMap != null) {
            // Get map dimensions
            MapProperties mapProps = tiledMap.getProperties();
            int mapWidth = mapProps.get("width", Integer.class) * tileWidth;
            int mapHeight = mapProps.get("height", Integer.class) * tileHeight;

            // Adjust camera bounds for zoom level
            float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
            float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

            // Calculate the camera boundaries (half width/height to account for camera center)
            float cameraHalfWidth = effectiveViewportWidth / 2f;
            float cameraHalfHeight = effectiveViewportHeight / 2f;

            float mapPixelWidth = mapWidth * tileWidth;
            float mapPixelHeight = mapHeight * tileHeight;

            float offsetX = 32f; // from Tiled
            float offsetY = 32f; // from Tiled

            // Clamp camera position within map boundaries
            camera.position.x = MathUtils.clamp(camera.position.x, cameraHalfWidth + offsetX, mapPixelWidth - cameraHalfWidth + offsetX);
            camera.position.y = MathUtils.clamp(camera.position.y, cameraHalfHeight + offsetY, mapPixelHeight - cameraHalfHeight + offsetY);
        }

        // Add this to round camera position to prevent sub-pixel rendering issues
        camera.position.x = Math.round(camera.position.x);
        camera.position.y = Math.round(camera.position.y);

        camera.update();
    }
}
