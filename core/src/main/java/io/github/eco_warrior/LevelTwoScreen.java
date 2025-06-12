package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.entity.tool;
import io.github.eco_warrior.sprite.*;
import io.github.eco_warrior.sprite.Enemy.Worm;
import io.github.eco_warrior.sprite.gardening_equipments.*;
import io.github.eco_warrior.sprite.gardening_equipments.sapling_variant.*;
import io.github.eco_warrior.sprite.tree_variant.*;

import java.util.*;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;

public class LevelTwoScreen implements Screen {


    private Main game;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private SpriteBatch uiBatch;

    private Texture backgroundTexture;
    private Sprite backgroundSprite;

    //debug method
    private ShapeRenderer shapeRenderer;

    //tools
    private Map<gameSpriteType, gameSprite> tools = new HashMap<>();
    private float manipulatorX;
    private float startY;

    //water fountain
    private Map<String, gameSprite> liquids;

    //trees
    private Map<treesType, Trees> trees;

    //entities declaration
    private WateringCan wateringCan;
    private WaterFountain waterFountain;
    private Shovel shovel;

    private OrdinaryTree ordinaryTree;
    private BlazingTree blazingTree;
    private BreezingTree breezingTree;
    private IceTree iceTree;
    private VoltaicTree voltaicTree;

    private OrdinarySapling ordinarySapling;
    private BlazingSapling blazingSapling;
    private BreezingSapling breezingSapling;
    private IceSapling iceSapling;
    private VoltaicSapling voltaicSapling;

    //boolean flags to check if saplings are used
    private boolean isVoltaicSaplingUsed = false;
    private boolean isBreezingSaplingUsed = false;
    private boolean isIceSaplingUsed = false;
    private boolean isBlazingSaplingUsed = false;

    //input selection
    private Vector2 currentTouchPos;
    private Vector2 lastTouchPos;
    private boolean isDragging = false;
    private boolean isReturning = false;
    private gameSprite draggingTool;

    //enemies
    private Array<Worm> worms; //check this later
    private Array<Worm> wormPool; //check this later
    private static final float wormStartX = WINDOW_WIDTH + 50f; //check this later
    private static final float wormStartY = 100f; //check this later
    private float wormSpawnTimer; //check this later
    private float stateTime; //check this later
    private static final int WORM_BUFFER_CAPACITY = 20;


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

        worms = new Array<>();
        liquids = new HashMap<>();
        trees = new HashMap<>();


        initializeEntities();
    }

    private void initializeEntities() {
        int toolCount = 5;
        float spacing = WINDOW_WIDTH / (toolCount + 4);
        float toolScale = 0.35f;
        float lakeScale = 1.3f;
        float treeScale = 0.26f;

        float toolWidth = 200f;
        startY = WINDOW_HEIGHT/30f;
        manipulatorX = toolWidth/2f;

        wateringCan = new WateringCan(new Vector2(spacing * 2 - manipulatorX, startY), toolScale);
        waterFountain = new WaterFountain(new Vector2(1, 180), lakeScale);
        shovel = new Shovel(new Vector2(spacing * 3 - manipulatorX, startY), toolScale);

        ordinaryTree = new OrdinaryTree(new Vector2(763, 92), treeScale);
        blazingTree = new BlazingTree(new Vector2(1048, 256), treeScale);
        breezingTree = new BreezingTree(new Vector2(920, 183), treeScale);
        iceTree = new IceTree(new Vector2(1023, 25), treeScale);
        voltaicTree = new VoltaicTree(new Vector2(781, 299), treeScale);

        ordinarySapling = new OrdinarySapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        blazingSapling = new BlazingSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        breezingSapling = new BreezingSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        iceSapling = new IceSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        voltaicSapling = new VoltaicSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);

        liquids.put("water_fountain_hitbox", waterFountain);

        tools.put(gameSpriteType.SHOVEL, shovel);
        tools.put(gameSpriteType.WATERING_CAN, wateringCan);
        tools.put(gameSpriteType.RAY_GUN, new RayGun(new Vector2(spacing * 1 - manipulatorX, startY), toolScale));
        tools.put(gameSpriteType.FERTILIZER, new Fertilizer(new Vector2(spacing * 4 - manipulatorX, startY), toolScale));

        tools.put(gameSpriteType.ORDINARY_SAPLING, ordinarySapling);
        tools.put(gameSpriteType.BLAZING_SAPLING, blazingSapling);
        tools.put(gameSpriteType.ICE_SAPLING, iceSapling);
        tools.put(gameSpriteType.BREEZING_SAPLING, breezingSapling);
        tools.put(gameSpriteType.VOLTAIC_SAPLING, voltaicSapling);

        trees.put(treesType.ORDINARY_TREE, ordinaryTree);
        trees.put(treesType.BLAZING_TREE, blazingTree);
        trees.put(treesType.BREEZING_TREE, breezingTree);
        trees.put(treesType.ICE_TREE, iceTree);
        trees.put(treesType.VOLTAIC_TREE, voltaicTree);

        wormPool = new Array<>(WORM_BUFFER_CAPACITY);
        for (int i = 0; i < WORM_BUFFER_CAPACITY; i++) {
            wormPool.add(new Worm(new Vector2(wormStartX, wormStartY)));
        }
    }

    private enum gameSpriteType {
        SHOVEL,
        WATERING_CAN,
        RAY_GUN,
        FERTILIZER,
        ORDINARY_SAPLING,
        BLAZING_SAPLING,
        ICE_SAPLING,
        BREEZING_SAPLING,
        VOLTAIC_SAPLING
    }

    private enum treesType{
        ORDINARY_TREE,
        BLAZING_TREE,
        BREEZING_TREE,
        ICE_TREE,
        VOLTAIC_TREE
    }

    @Override
    public void render(float delta) {
        input();
        draw();
        updateWateringCan();
        updateTrees();
        spawnWorm(delta);
    }

    private void spawnWorm(float delta) {
        wormSpawnTimer += delta; // Adds the current delta to the timer
        if (wormSpawnTimer > 3f) { // Check if it has been more than a second\
            Worm worm;
            long startTime = System.nanoTime();

            if(wormPool.size > 0) {
                worm = wormPool.pop();
                worm.getSprite().setPosition(wormStartX, wormStartY);
            }else {
                worm = new Worm(new Vector2(wormStartX, wormStartY));
            }
            worms.add(worm);
            wormSpawnTimer = 0; // Reset the timer
            long endTime = System.nanoTime();
            //System.out.println("Worm creation time: " + (endTime - startTime) / 1000000.0 + " ms");
        }
    }

    private void draw() {
        camera.update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        stateTime = Gdx.graphics.getDeltaTime(); //check this later

        batch.begin();
        backgroundSprite.draw(batch);

        //draw tools that are not saplings to be unlocked
        for (gameSprite tool : tools.values()) {

            if(!(tool instanceof BaseSapling) || tool == tools.get(gameSpriteType.ORDINARY_SAPLING)) {
                tool.draw(batch);
            }
        }

        for (gameSprite tree : trees.values()) {
            tree.draw(batch);
        }

        //remove ordinary sapling upon planting
        if ((ordinaryTree.treeLevel == OrdinaryTree.TreeStage.HOLE.ordinal())
            && ordinaryTree.getCollisionRect().overlaps(ordinarySapling.getCollisionRect())) {
            tools.remove(gameSpriteType.ORDINARY_SAPLING);
        }

        //draw voltaic sapling when the ordinary tree reaches adult phase
        if(!isVoltaicSaplingUsed) {
            if (ordinaryTree.treeLevel == OrdinaryTree.TreeStage.MATURE_TREE.ordinal()) {
                tools.get(gameSpriteType.VOLTAIC_SAPLING).draw(batch);
            }
        }

        //remove voltaic sapling upon planting
        if ((voltaicTree.treeLevel == VoltaicTree.TreeStage.HOLE.ordinal())
            && voltaicTree.getCollisionRect().overlaps(voltaicSapling.getCollisionRect())) {
            tools.remove(gameSpriteType.VOLTAIC_SAPLING);
            isVoltaicSaplingUsed = true; //set to true when voltaic sapling is used
        }

        //draw breezing sapling when the voltaic tree reaches adult phase
        if(!isBreezingSaplingUsed) {
            //VoltaicTree.TreeStage.YOUNG_TREE.ordinal() means after the sapling stage
            if (voltaicTree.treeLevel > VoltaicTree.TreeStage.YOUNG_TREE.ordinal()) {
                tools.get(gameSpriteType.BREEZING_SAPLING).draw(batch);
            }
        }

        //remove breezing sapling upon planting
        if ((breezingTree.treeLevel == BreezingTree.TreeStage.HOLE.ordinal())
            && breezingTree.getCollisionRect().overlaps(breezingSapling.getCollisionRect())) {
            tools.remove(gameSpriteType.BREEZING_SAPLING);
            isBreezingSaplingUsed = true; //set to true when breezing sapling is used
        }

        //draw ice sapling when the breezing tree reaches adult phase
        if(!isIceSaplingUsed) {
            //included ANIMATED_MATURE_TREE_1 - 3
            if (breezingTree.treeLevel >= BreezingTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()) {
                tools.get(gameSpriteType.ICE_SAPLING).draw(batch);
            }
        }

        //remove ice sapling upon planting
        if ((iceTree.treeLevel == IceTree.TreeStage.HOLE.ordinal())
            && iceTree.getCollisionRect().overlaps(iceSapling.getCollisionRect())) {
            tools.remove(gameSpriteType.ICE_SAPLING);
            isIceSaplingUsed = true; //set to true when ice sapling is used
        }

        //draw blazing sapling when the ice tree reaches adult phase
        if(!isBlazingSaplingUsed) {
            //included ANIMATED_MATURE_TREE_1 - 4
            if (iceTree.treeLevel >= IceTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()) {
                tools.get(gameSpriteType.BLAZING_SAPLING).draw(batch);
            }
        }

        //remove blazing sapling upon planting
        if ((blazingTree.treeLevel == BlazingTree.TreeStage.HOLE.ordinal())
            && blazingTree.getCollisionRect().overlaps(blazingSapling.getCollisionRect())) {
            tools.remove(gameSpriteType.BLAZING_SAPLING);
            isBlazingSaplingUsed = true; //set to true when blazing sapling is used
        }

        Iterator<Worm> iterator = worms.iterator();
        while(iterator.hasNext()) {
            Worm worm = iterator.next();
            worm.update(stateTime);
            worm.draw(batch);
            if(worm.isOffScreen()) {
                worm.reset();
                iterator.remove();
                break;
            }
        } //check this later

        batch.end();

        debugSprite();
    }

    private void input(){
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            currentTouchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(currentTouchPos);

            //check if pressed the tools
            if (Gdx.input.justTouched()) {
                for (gameSprite tool : tools.values()) {
                    //if is touched the tools
                    if (tool == tools.get(gameSpriteType.BLAZING_SAPLING)) {
                        //voltaic sapling can only be dragged when ordinary tree is mature
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && ordinaryTree.treeLevel == OrdinaryTree.TreeStage.MATURE_TREE.ordinal()) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    }
                    else if (tool == tools.get(gameSpriteType.BREEZING_SAPLING)) {
                        //breezing sapling can only be dragged when voltaic tree is mature
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && (voltaicTree.treeLevel >= VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal())) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    }
                    else if (tool == tools.get(gameSpriteType.ICE_SAPLING)) {
                        //ice sapling can only be dragged when breezing tree is mature
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && (breezingTree.treeLevel >= BreezingTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal())) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    }
                    else if (tool == tools.get(gameSpriteType.BLAZING_SAPLING)) {
                        //blazing sapling can only be dragged when ice tree is mature
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && (iceTree.treeLevel >= IceTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal())) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    } else {
                        if (tool.getCollisionRect().contains(currentTouchPos)) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;

                        }
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
        wateringCan.updateWateringCan(waterFountain);
    }

    private void updateTrees() {
        ordinaryTree.updateTree(shovel, ordinarySapling, wateringCan);
        blazingTree.updateTree(shovel, blazingSapling, wateringCan);
        breezingTree.updateTree(shovel, breezingSapling, wateringCan);
        iceTree.updateTree(shovel, iceSapling, wateringCan);
        voltaicTree.updateTree(shovel, voltaicSapling, wateringCan);
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

        for(gameSprite tree: trees.values()){
            tree.drawDebug(shapeRenderer);
        }

        for (gameSprite worm : worms) {
            worm.drawDebug(shapeRenderer);
        } //check this later

        liquids.get("water_fountain_hitbox").drawDebug(shapeRenderer);

//        debugSpawnArea();

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        uiBatch.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        shapeRenderer.dispose();

        for (gameSprite tool : tools.values()) {
            tool.dispose();
        }

        for (gameSprite tree : trees.values()) {
            tree.dispose();
        }

        for(gameSprite worm: worms){
            worm.dispose();
        }
        wateringCan.dispose();
        waterFountain.dispose();
        shovel.dispose();

        liquids.get("water_fountain_hitbox").dispose();
    }
}
