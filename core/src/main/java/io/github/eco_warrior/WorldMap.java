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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Matrix4;
import io.github.eco_warrior.MapLoader.MapLoader;
import io.github.eco_warrior.animation.Screen.ScreenTransition;
import io.github.eco_warrior.controller.*;
import io.github.eco_warrior.screen.instructions.L1Instructions;
import io.github.eco_warrior.screen.instructions.L2Instructions;
import io.github.eco_warrior.screen.instructions.L3Instructions;
import io.github.eco_warrior.sprite.Characters.GameCharacter;
import io.github.eco_warrior.sprite.Characters.AdventurerGirl;

import java.util.Arrays;

import static io.github.eco_warrior.constant.ConstantsVar.*;

public class WorldMap implements Screen {

    public static final int LAYER_MAP = 4;
    private MapLoader map;
    private MapController mapController;
    private OrthographicCamera camera;
    private float viewportWidth = TILES_TO_SHOW * WORLD_MAP_PIXEL_SIZE;
    private float viewportHeight = TILES_TO_SHOW * WORLD_MAP_PIXEL_SIZE;
    private Game game;

    private SpriteBatch batch;
    private AdventurerGirl adventurerGirl;
    private ShapeRenderer shapeRenderer;
    private Rectangle rect;
    private PlayerController playerController;

    //NPC
    private NPCManager npcManager;

    // Dialog Box
    private DialogBox dialogBox;
    private FontGenerator dialogFont, speakerFont;

    private static final float CAMERA_ZOOM = 1f; // 1f = no zoom

    //map layers
    // Get total number of layers
    private int numLayers = 0 ;

    //level threshold
    private static int level=1;

    //fade
    private ScreenTransition transition;

    public WorldMap(Game game) {
        this.game = game;

    }

    private void initializeNPCs() {
        npcManager = new NPCManager(map.getMap(), mapController.tileWidth, mapController.tileHeight);

        npcManager.setMapController(mapController);
        mapController.setNPCManager(npcManager);
    }

    private void CreateCharacter(){
        try {
            createAdventurerGirl();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    private void createAdventurerGirl() throws Exception {
        MapController result = getGetMapInfo();


        adventurerGirl = new AdventurerGirl(
            result.spawnPosition,
            result.tileWidth,
            result.tileHeight,
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


            this.mapController.loadCollisionTiles(map.getMap(), "columns");
            this.mapController.loadCollisionTiles(map.getMap(), "objects3");
            this.mapController.loadCollisionTiles(map.getMap(), "objects1");
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
    public void show() {
        this.shapeRenderer = new ShapeRenderer();
        this.transition = new ScreenTransition(game);

        LoadMap();
        CreateCharacter();
        setupCamera();
        initializeNPCs();
        batch = new SpriteBatch();
        playerController = new PlayerController(adventurerGirl);
        Gdx.input.setInputProcessor(playerController);

        // --- Dialog Box Setup (auto-sizing) ---
        dialogFont = new FontGenerator(DIALOG_FONT_SIZE, Color.WHITE, Color.BLACK);
        speakerFont = new FontGenerator(SPEAKER_FONT_SIZE, Color.YELLOW, Color.BLACK);
        dialogBox = new DialogBox(dialogFont.getFont(), speakerFont.getFont());
    }

    @Override
    public void render(float delta) {

        input();
        draw(delta);
        update(delta);
    }

    private void draw(float delta) {
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
        batch.begin();
        npcManager.render(batch, camera);
        batch.end();

        // Draw DialogBox in screen coordinates so it is always visible
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        //dialog box so the character will look next to the character and smaller
        batch.begin();
        dialogBox.render(batch);
        batch.end();

        transition.update(delta);
        transition.render();

//        drawDebug();
    }

    private void input() {
        // For demo: trigger dialog with F key
        if (dialogTrigger()) return;
        dialogBox.update();
    }

    private boolean dialogTrigger() {
        if (!dialogBox.isVisible() && Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            GameCharacter character =  npcManager.checkInteraction(adventurerGirl.getBoundingRectangle());
            if(character == null) {
                return true;
            }
            npcManager.setInteracting(true);

            int currentLevel = ((Main)game).getLevel();

            switch (character.getName()) {
                case "Goblin Warrior":
                        dialogBox.startDialog(character.getName(), Arrays.asList(
                            "Help!",
                            "Please help us to reduce waste!"
                        ), () -> {
                            //trigger after dialog is completed
                            npcManager.setInteracting(false);
                            triggerLevel(currentLevel);
                        });
                    break;
                case "Goblin Warrior 2":
                    if(currentLevel <= 2) {
                        levelNotHighEnough(character.getName());
                        break;
                    }
                    dialogBox.startDialog(character.getName(), Arrays.asList(
                        "Our forest is polluted!",
                        "Can you help clean it?"
                    ), () -> {
                        npcManager.setInteracting(false);
                        triggerLevel(currentLevel);
                    });
                    break;
                case "Goblin Warrior King":
                    if(currentLevel <= 1) {
                        levelNotHighEnough(character.getName());
                        break;
                    }
                    dialogBox.startDialog(character.getName(), Arrays.asList(
                        "I need help on saving the clean water!",
                        "The monsters are polluting it!"
                    ), () -> {
                        npcManager.setInteracting(false);
                        triggerLevel(currentLevel);
                    });
                    break;
                default:
                    dialogBox.startDialog(character.getName(), Arrays.asList(
                        "Hello adventurer!",
                        "Welcome to our forest."
                    ), () -> {
                        npcManager.setInteracting(false);
                    });
                    break;

            }

        }
        return false;
    }

    private void levelNotHighEnough(String characterName) {
        dialogBox.startDialog(characterName, Arrays.asList(
            "You need to complete the previous level before talking to me!",
            "Please come back after you have completed the previous level."
        ), () -> {
            npcManager.setInteracting(false);
        });
    }

    private void triggerLevel(int level) {
        System.out.println("Dialog completed, triggering level transition");
        if (level == 1) {
            transition.startTransition(new L1Instructions((Main)game));
        }
        if( level <= 3) {
            transition.startTransition(new L2Instructions((Main)game));
        }
        if( level <= 2) {
            transition.startTransition(new L3Instructions((Main)game));
        }
    }


    private void update(float delta) {
        npcManager.update(delta, map.getMap());
        // Pause game logic while dialog is visible
        if (!dialogBox.isVisible()) {
            playerController.update(delta);
            adventurerGirl.update(delta, map.getMap());
        }

        updateInteraction(delta);

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

    private void updateInteraction(float delta) {
        // Update NPCs
        npcManager.update(delta, map.getMap());



        // Create player bounding box
        Rectangle playerBox = new Rectangle(
            adventurerGirl.getPosition().x - adventurerGirl.getCurrentFrame().getRegionWidth() / 2f,
            adventurerGirl.getPosition().y - adventurerGirl.getCurrentFrame().getRegionHeight() / 2f,
            adventurerGirl.getCurrentFrame().getRegionWidth(),
            adventurerGirl.getCurrentFrame().getRegionHeight()
        );

    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);


        if (rect != null) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }


        mapController.drawDebug(shapeRenderer, Color.RED);
        shapeRenderer.setColor(Color.GREEN);
        Rectangle goblinBox = new Rectangle(
            adventurerGirl.getPosition().x - adventurerGirl.getCurrentFrame().getRegionWidth() / 2f,
            adventurerGirl.getPosition().y - adventurerGirl.getCurrentFrame().getRegionHeight() / 2f,
            adventurerGirl.getCurrentFrame().getRegionWidth(),
            adventurerGirl.getCurrentFrame().getRegionHeight()
        );
        shapeRenderer.rect(goblinBox.x, goblinBox.y, goblinBox.width, goblinBox.height);

        npcManager.drawDebug(shapeRenderer);


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
        npcManager.dispose();
        transition.dispose();
    }
}
