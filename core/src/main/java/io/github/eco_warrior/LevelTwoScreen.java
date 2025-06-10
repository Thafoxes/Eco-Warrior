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
import io.github.eco_warrior.sprite.*;
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
    private Map<String, gameSprite> tools = new HashMap<>();
    private float startY;
    private float XMover;

    //lake
    private Map<String, gameSprite> waterFountain = new HashMap<>();

    //trees
    private Map<String, gameSprite> trees = new HashMap<>();

    //entity logics
    private WateringCan wateringCanLogic;
    private WaterFountain waterFountainLogic;
    private Shovel shovelLogic;

    private OrdinaryTree ordinaryTreeLogic;
    private BlazingTree blazingTreeLogic;
    private BreezingTree breezingTreeLogic;
    private IceTree iceTreeLogic;
    private VoltaicTree voltaicTreeLogic;

    private OrdinarySapling ordinarySaplingLogic;
    private BlazingSapling blazingSaplingLogic;
    private BreezingSapling breezingSaplingLogic;
    private IceSapling iceSaplingLogic;
    private VoltaicSapling voltaicSaplingLogic;

    //boolean flags
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
        XMover = toolWidth/2f;

        wateringCanLogic = new WateringCan(new Vector2(spacing * 2 - XMover, startY), toolScale);
        waterFountainLogic = new WaterFountain(new Vector2(1, 180), lakeScale);
        shovelLogic = new Shovel(new Vector2(spacing * 3 - XMover, startY), toolScale);

        ordinaryTreeLogic = new OrdinaryTree(new Vector2(763, 92), treeScale);
        blazingTreeLogic = new BlazingTree(new Vector2(1048, 256), treeScale);
        breezingTreeLogic = new BreezingTree(new Vector2(920, 183), treeScale);
        iceTreeLogic = new IceTree(new Vector2(1023, 25), treeScale);
        voltaicTreeLogic = new VoltaicTree(new Vector2(781, 299), treeScale);

        ordinarySaplingLogic = new OrdinarySapling(new Vector2(spacing * 5 - XMover, startY), toolScale);
        blazingSaplingLogic = new BlazingSapling(new Vector2(spacing * 5 - XMover, startY), toolScale);
        breezingSaplingLogic = new BreezingSapling(new Vector2(spacing * 5 - XMover, startY), toolScale);
        iceSaplingLogic = new IceSapling(new Vector2(spacing * 5 - XMover, startY), toolScale);
        voltaicSaplingLogic = new VoltaicSapling(new Vector2(spacing * 5 - XMover, startY), toolScale);

        waterFountain.put("water_fountain_hitbox", waterFountainLogic);

        tools.put("shovel", shovelLogic);
        tools.put("watering_can", wateringCanLogic);
        tools.put("ray_gun", new RayGun(new Vector2(spacing * 1 - XMover, startY), toolScale));
        tools.put("fertilizer", new Fertilizer(new Vector2(spacing * 4 - XMover, startY), toolScale));

        tools.put("ordinary_sapling", ordinarySaplingLogic);
        tools.put("blazing_sapling", blazingSaplingLogic);
        tools.put("ice_sapling", iceSaplingLogic);
        tools.put("breezing_sapling", breezingSaplingLogic);
        tools.put("voltaic_sapling", voltaicSaplingLogic);

        trees.put("ordinary_tree", ordinaryTreeLogic);
        trees.put("blazing_tree", blazingTreeLogic);
        trees.put("breezing_tree", breezingTreeLogic);
        trees.put("ice_tree", iceTreeLogic);
        trees.put("voltaic_tree", voltaicTreeLogic);
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
            if(!(tool == tools.get("blazing_sapling")
                || tool == tools.get("ice_sapling")
                || tool == tools.get("voltaic_sapling")
                || tool == tools.get("breezing_sapling"))) {
                tool.draw(batch);
            }
        }

        for (gameSprite tree : trees.values()) {
            tree.draw(batch);
        }

        //remove sapling upon planting
        if ((ordinaryTreeLogic.treeLevel == OrdinaryTree.TreeStage.HOLE.ordinal())
            && ordinaryTreeLogic.getCollisionRect().overlaps(ordinarySaplingLogic.getCollisionRect())) {
            tools.remove("ordinary_sapling");
        }

        //draw voltaic sapling when the ordinary tree reaches adult phase
        if(!isVoltaicSaplingUsed) {
            if (ordinaryTreeLogic.treeLevel == OrdinaryTree.TreeStage.MATURE_TREE.ordinal()) {
                tools.get("voltaic_sapling").draw(batch);
            }
        }

        //remove voltaic sapling upon planting
        if ((voltaicTreeLogic.treeLevel == VoltaicTree.TreeStage.HOLE.ordinal())
            && voltaicTreeLogic.getCollisionRect().overlaps(voltaicSaplingLogic.getCollisionRect())) {
            tools.remove("voltaic_sapling");
            isVoltaicSaplingUsed = true; //set to true when voltaic sapling is used
        }

        //draw breezing sapling when the voltaic tree reaches adult phase
        if(!isBreezingSaplingUsed) {
            if (voltaicTreeLogic.treeLevel == VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
                || voltaicTreeLogic.treeLevel == VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_2.ordinal()
                || voltaicTreeLogic.treeLevel == VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_3.ordinal()
                || voltaicTreeLogic.treeLevel == VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_4.ordinal()) {
                tools.get("breezing_sapling").draw(batch);
            }
        }

        //remove voltaic sapling upon planting
        if ((breezingTreeLogic.treeLevel == BreezingTree.TreeStage.HOLE.ordinal())
            && breezingTreeLogic.getCollisionRect().overlaps(breezingSaplingLogic.getCollisionRect())) {
            tools.remove("breezing_sapling");
            isBreezingSaplingUsed = true; //set to true when breezing sapling is used
        }

        //draw ice sapling when the breezing tree reaches adult phase
        if(!isIceSaplingUsed) {
            if (breezingTreeLogic.treeLevel == BreezingTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
                || breezingTreeLogic.treeLevel == BreezingTree.TreeStage.ANIMATED_MATURE_TREE_2.ordinal()
                || breezingTreeLogic.treeLevel == BreezingTree.TreeStage.ANIMATED_MATURE_TREE_3.ordinal()) {
                tools.get("ice_sapling").draw(batch);
            }
        }

        //remove ice sapling upon planting
        if ((iceTreeLogic.treeLevel == IceTree.TreeStage.HOLE.ordinal())
            && iceTreeLogic.getCollisionRect().overlaps(iceSaplingLogic.getCollisionRect())) {
            tools.remove("ice_sapling");
            isIceSaplingUsed = true; //set to true when ice sapling is used
        }

        //draw blazing sapling when the breezing tree reaches adult phase
        if(!isBlazingSaplingUsed) {
            if (iceTreeLogic.treeLevel == IceTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
                || iceTreeLogic.treeLevel == IceTree.TreeStage.ANIMATED_MATURE_TREE_2.ordinal()
                || iceTreeLogic.treeLevel == IceTree.TreeStage.ANIMATED_MATURE_TREE_3.ordinal()
                || iceTreeLogic.treeLevel == IceTree.TreeStage.ANIMATED_MATURE_TREE_4.ordinal()) {
                tools.get("blazing_sapling").draw(batch);
            }
        }

        //remove blazing sapling upon planting
        if ((blazingTreeLogic.treeLevel == BlazingTree.TreeStage.HOLE.ordinal())
            && blazingTreeLogic.getCollisionRect().overlaps(blazingSaplingLogic.getCollisionRect())) {
            tools.remove("blazing_sapling");
            isBlazingSaplingUsed = true; //set to true when blazing sapling is used
        }

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
                    if (tool == tools.get("voltaic_sapling")) {
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && ordinaryTreeLogic.treeLevel == OrdinaryTree.TreeStage.MATURE_TREE.ordinal()) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    }
                    else if (tool == tools.get("breezing_sapling")) {
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && (voltaicTreeLogic.treeLevel == VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
                            || voltaicTreeLogic.treeLevel == VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_2.ordinal()
                            || voltaicTreeLogic.treeLevel == VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_3.ordinal()
                            || voltaicTreeLogic.treeLevel == VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_4.ordinal())) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    }
                    else if (tool == tools.get("ice_sapling")) {
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && (breezingTreeLogic.treeLevel == BreezingTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
                            || breezingTreeLogic.treeLevel == BreezingTree.TreeStage.ANIMATED_MATURE_TREE_2.ordinal()
                            || breezingTreeLogic.treeLevel == BreezingTree.TreeStage.ANIMATED_MATURE_TREE_3.ordinal())) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    }
                    else if (tool == tools.get("blazing_sapling")) {
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && (iceTreeLogic.treeLevel == IceTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal())
                            || iceTreeLogic.treeLevel == IceTree.TreeStage.ANIMATED_MATURE_TREE_2.ordinal()
                            || iceTreeLogic.treeLevel == IceTree.TreeStage.ANIMATED_MATURE_TREE_3.ordinal()
                            || iceTreeLogic.treeLevel == IceTree.TreeStage.ANIMATED_MATURE_TREE_4.ordinal()) {
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
        ordinaryTreeLogic.updateTreeStatus(shovelLogic, ordinarySaplingLogic, wateringCanLogic);
        blazingTreeLogic.updateTreeStatus(shovelLogic, blazingSaplingLogic, wateringCanLogic);
        breezingTreeLogic.updateTreeStatus(shovelLogic, breezingSaplingLogic, wateringCanLogic);
        iceTreeLogic.updateTreeStatus(shovelLogic, iceSaplingLogic, wateringCanLogic);
        voltaicTreeLogic.updateTreeStatus(shovelLogic, voltaicSaplingLogic, wateringCanLogic);
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

        waterFountain.get("water_fountain_hitbox").drawDebug(shapeRenderer);

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

        waterFountain.get("water_fountain_hitbox").dispose();
    }
}
