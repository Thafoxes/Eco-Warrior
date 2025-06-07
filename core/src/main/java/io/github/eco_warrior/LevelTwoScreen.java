package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.sprite.WaterFountain;
import io.github.eco_warrior.sprite.gardening_equipments.*;
import io.github.eco_warrior.sprite.tree_variant.*;

import java.util.HashMap;
import java.util.Map;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;

public class LevelTwoScreen implements Screen {


    private Main game;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private SpriteBatch uiBatch;

    //map
//    private TiledMap map;
//    private OrthogonalTiledMapRenderer maprenderer;
    private Texture backgroundTexture;
    private Sprite backgroundSprite;

    //debug method
    private ShapeRenderer shapeRenderer;

    //delta time
    private float stateTime;

    //tools
    private Map<String, gameSprite> tools = new HashMap<>();
    private float startY;
    private float XMover;

    //lake filler
    private Map<String, gameSprite> lake = new HashMap<>();

    //tree filler
    private Map<String, gameSprite> tree = new HashMap<>();
    private Map<String, gameSprite> blazingTree = new HashMap<>();

    //entity logics
    private WateringCan wateringCanLogic;
    private WaterFountain waterFountainLogic;
    private OrdinaryTree ordinaryTreeLogic;
    private BlazingTree blazingTreeLogic;
    private Shovel shovelLogic;
    private Sapling saplingLogic;

    //input selection
    private Vector2 currentTouchPos;
    private Vector2 lastTouchPos;
    private boolean isDragging = false;
    private boolean isReturning = false;
    private gameSprite draggingTool;


    public LevelTwoScreen(Main main) {
        this.game = main;

    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();
        backgroundTexture = new Texture("textures/greenland.png");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        lastTouchPos = new Vector2();
        currentTouchPos = new Vector2();

        shapeRenderer = new ShapeRenderer();

        //setup camera to middle
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);
        camera.update();

        initializeTools();

//        loadMap();
    }

//    private void loadMap(){
//        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
//        parameters.generateMipMaps = false;
//        map = new TmxMapLoader().load("maps/soil_map.tmx", parameters);
//
//        maprenderer = new OrthogonalTiledMapRenderer(map, 1f);
//    }

    private void initializeTools() {
        int toolCount = 5;
        float spacing = WINDOW_WIDTH / (toolCount + 4);
        float toolScale = 0.35f;
        float lakeScale = 1.3f;
        float treeScale = 0.3f;

//        float toolWidth = new DuctTape(new Vector2(0, 0), toolScale).getSprite().getWidth();
        float toolWidth = 200f;
        startY = WINDOW_HEIGHT/30f;
        XMover = toolWidth/2f;

        wateringCanLogic = new WateringCan(new Vector2(spacing * 2 - XMover, startY), toolScale);
        waterFountainLogic = new WaterFountain(new Vector2(1, 180), lakeScale);
        ordinaryTreeLogic = new OrdinaryTree(new Vector2(800, 250), treeScale);
        blazingTreeLogic = new BlazingTree(new Vector2(1000, 250), treeScale);
        shovelLogic = new Shovel(new Vector2(spacing * 3 - XMover, startY), toolScale);
        saplingLogic = new Sapling(new Vector2(spacing * 5 - XMover, startY), toolScale);

        tools.put("shovel", shovelLogic);
        tools.put("watering_can", wateringCanLogic);
        tools.put("ray_gun", new RayGun(new Vector2(spacing * 1 - XMover, startY), toolScale));
        tools.put("fertilizer", new Fertilizer(new Vector2(spacing * 4 - XMover, startY), toolScale));
        tools.put("sapling", saplingLogic);

        lake.put("lake_hitbox", waterFountainLogic);
        tree.put("tree", ordinaryTreeLogic);
        blazingTree.put("fire_tree", blazingTreeLogic);
    }

    @Override
    public void render(float delta) {
        input();
        draw();
        updateWateringCan();
        updateTree();
    }

    private void draw() {
        camera.update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        backgroundSprite.draw(batch);
        for (gameSprite tool : tools.values()) {
            tool.draw(batch);
        }

        tree.values().iterator().next().draw(batch);
        blazingTree.values().iterator().next().draw(batch);
        batch.end();

        debugSprite();
//        maprenderer.setView(camera);
//        maprenderer.render();
    }

    private void input(){
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            currentTouchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(currentTouchPos);

            //check if pressed the tools
            if (Gdx.input.justTouched()) {
                for (gameSprite tool : tools.values()) {
                    //if is touched the tools
                    if (tool.getCollisionRect().contains(currentTouchPos)) {
                        draggingTool = tool;
                        isDragging = true;
                        lastTouchPos.set(currentTouchPos);
                        break;

                    }
                }
            }
        }

        if(isDragging && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && draggingTool != null){
            currentTouchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(currentTouchPos);

            float dx = currentTouchPos.x - draggingTool.getMidX();
            float dy = currentTouchPos.y - draggingTool.getMidY();

            draggingTool.getSprite().setPosition(dx,dy);

            //sync the collision rectangle with the new sprite location
            draggingTool.getCollisionRect().setPosition(
                draggingTool.getSprite().getX(),
                draggingTool.getSprite().getY());

            lastTouchPos.set(currentTouchPos);

        }else if(!Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            //if the left click is released
            if(draggingTool != null){
                try{
                    onMouseRelease();
//                        returnOriginalPosition();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else if(draggingTool != null){
            isDragging = false;
            isReturning = true;

        }

        if(isReturning){
            returnOriginalPosition();
        }

    }

    private void returnOriginalPosition() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if(draggingTool != null){
            float dy = startY;
            Vector2 current = new Vector2(draggingTool.getSprite().getX(), draggingTool.getSprite().getY());
            Vector2 target = draggingTool.getInitPosition();

            Vector2 learped = current.lerp(target, deltaTime * 10f);

            draggingTool.getSprite().setPosition(learped.x, learped.y);
            draggingTool.getCollisionRect().setPosition(
                draggingTool.getSprite().getX(),
                draggingTool.getSprite().getY());

            if(current.dst(target) < 2f){
                draggingTool.getSprite().setPosition(target.x, target.y);
                draggingTool.getCollisionRect().setPosition(
                    draggingTool.getSprite().getX(),
                    draggingTool.getSprite().getY());
                isReturning = false;
                draggingTool = null;
            }

        }
    }

    private void onMouseRelease() {
        isDragging = false;
        isReturning = true;

    }

    private void updateWateringCan() {
        wateringCanLogic.updateWateringCanStatus(waterFountainLogic);
    }

    private void updateTree() {
        ordinaryTreeLogic.updateTreeStatus(shovelLogic, saplingLogic, wateringCanLogic);
        blazingTreeLogic.updateBlazingTreeStatus(shovelLogic, saplingLogic, wateringCanLogic);
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

    private void debugSprite() {
        //debug mode start
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);

        for(gameSprite tool: tools.values()){
            tool.drawDebug(shapeRenderer);
        }

        lake.values().iterator().next().drawDebug(shapeRenderer);
        tree.values().iterator().next().drawDebug(shapeRenderer);
        blazingTree.values().iterator().next().drawDebug(shapeRenderer);

//        debugSpawnArea();

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        uiBatch.dispose();
        batch.dispose();
//        maprenderer.dispose();
//        map.dispose();
        backgroundTexture.dispose();
        backgroundSprite.getTexture().dispose();

    }
}
