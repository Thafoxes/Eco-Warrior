package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.controller.Enemy.EnemyController;
import io.github.eco_warrior.controller.Enemy.WormController;
import io.github.eco_warrior.controller.Manager.EnemyManager;
import io.github.eco_warrior.controller.Manager.ToolManager;
import io.github.eco_warrior.controller.Manager.TreeControllerManager;
import io.github.eco_warrior.controller.Pools.WormPool;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.controller.Trees.*;
import io.github.eco_warrior.entity.GameSprite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.github.eco_warrior.enums.GardeningEnums;
import io.github.eco_warrior.sprite.*;
import io.github.eco_warrior.sprite.gardening_equipments.*;
import io.github.eco_warrior.sprite.gardening_equipments.sapling_variant.*;
import io.github.eco_warrior.sprite.tree_variant.*;
import io.github.eco_warrior.sprite.UI.Currency;

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
    private ToolManager toolManager;
    private float manipulatorX;
    private float startY;


    private TreeControllerManager treeControllerManager;

    //entities declaration
    private WateringCan wateringCan;
    private WaterFountain waterFountain;

    //currency
    private Currency currency;

    //input selection
    private Vector2 currentTouchPos;
    private Vector2 lastTouchPos;
    private boolean isDragging = false;
    private boolean isReturning = false;
    private GameSprite draggingTool;

    //enemies
    private EnemyManager enemyManager;
    private WormPool wormPool;
    private ArrayList<EnemyController> enemyControllersToBeRemove;
    private float spawnTimer = 0f;
    private float spawnInterval = 4f;

    private final Random rand = new Random();

    private float stateTime = 0f;
    private Map<String, Vector2> treePositions = new HashMap<>();


    public LevelTwoScreen(Main main) {
        this.game = main;
        this.toolManager = new ToolManager();
        this.treeControllerManager = new TreeControllerManager();
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

        initializeTools();
        initializeTrees();
        initializeEnemyManager();

        currency = new Currency(new Vector2(20, WINDOW_HEIGHT - 60), 0.5f, camera);
    }

    private void initializeEnemyManager() {
        enemyManager = new EnemyManager();
        enemyControllersToBeRemove = new ArrayList<>();
        wormPool = new WormPool(enemyManager);

    }

    private void initializeTools() {
        int toolCount = 5;
        float spacing = WINDOW_WIDTH / (toolCount + 4); //make it 10 so it look from left to right
        float toolScale = 0.35f;
        float lakeScale = 1.3f;


        float toolWidth = 200f;
        startY = WINDOW_HEIGHT/30f;
        manipulatorX = toolWidth/2f;
        waterFountain = new WaterFountain(new Vector2(1, 180), lakeScale);


        RayGun rayGun = new RayGun(new Vector2(spacing - manipulatorX, startY), toolScale);
        wateringCan = new WateringCan(new Vector2(spacing * 2 - manipulatorX, startY), toolScale);
        Shovel shovel = new Shovel(new Vector2(spacing * 3 - manipulatorX, startY), toolScale);
        Fertilizer fertilizer = new Fertilizer(new Vector2(spacing * 4 - manipulatorX, startY), toolScale);
        toolManager.addTool(GardeningEnums.WATERING_CAN, wateringCan);
        toolManager.addTool(GardeningEnums.SHOVEL, shovel);
        toolManager.addTool(GardeningEnums.RAY_GUN, rayGun);
        toolManager.addTool(GardeningEnums.FERTILIZER, fertilizer);

        initializeSapling(spacing, toolScale);
    }

    private void initializeSapling(float spacing, float toolScale) {
        BaseSaplingController ordinarySapling = new OrdinarySapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        BaseSaplingController blazingSapling = new BlazingSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        BaseSaplingController breezingSapling = new BreezingSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        BaseSaplingController iceSapling = new IceSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        BaseSaplingController voltaicSapling = new VoltaicSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);


        //following teir list
        toolManager.addSaplingController(ordinarySapling);
        toolManager.addSaplingController(voltaicSapling);
        toolManager.addSaplingController(breezingSapling);
        toolManager.addSaplingController(iceSapling);
        toolManager.addSaplingController(blazingSapling);


    }

    private void initializeTrees() {
        float treeScale = 0.20f;

        treePositions.put("ordinary", new Vector2(763, 92));
        treePositions.put("blazing", new Vector2(1048, 256));
        treePositions.put("breezing", new Vector2(920, 183));
        treePositions.put("ice", new Vector2(1023, 25));
        treePositions.put("voltaic", new Vector2(781, 299));

        TreeController<OrdinaryTree> ordinaryTreeController = new OrdinaryTreeController(
            new OrdinaryTree(treePositions.get("ordinary"), treeScale),
            wateringCan
        );
        TreeController<BlazingTree> blazingTreeController = new BlazingTreeController(
            new BlazingTree(treePositions.get("blazing"), treeScale),
                wateringCan
        );
        TreeController<BreezingTree> breezingTreeController = new BreezingTreeController(
            new BreezingTree(treePositions.get("breezing"), treeScale),
            wateringCan
        );
        TreeController<IceTree> iceTreeController = new IceTreeController(
            new IceTree(treePositions.get("ice"), treeScale),
            wateringCan
        );
        TreeController<VoltaicTree> voltaicTreeController = new VoltaicTreeController(
            new VoltaicTree(treePositions.get("voltaic"), treeScale),
            wateringCan
        );

        treeControllerManager.addTreeController(ordinaryTreeController);
        treeControllerManager.addTreeController(blazingTreeController);
        treeControllerManager.addTreeController(breezingTreeController);
        treeControllerManager.addTreeController(iceTreeController);
        treeControllerManager.addTreeController(voltaicTreeController);

    }



    @Override
    public void render(float delta) {
        input();
        draw(delta);
        returnOriginalPosition();

        updateToolManager(delta);
        updateTreeManager(delta);
        updateEnemyManager(delta);
        updateEnemyTreeLogic(delta);
    }

    private void updateEnemyTreeLogic(float delta) {
        for(EnemyController enemy: enemyManager.getEnemies()){
            if(enemy instanceof WormController){
                WormController worm = (WormController) enemy;
                // Check if the worm is colliding with any tree
                for(TreeController<?> treeController : treeControllerManager.getTreeControllers()){
                    if(treeController instanceof OrdinaryTreeController){
                        if(treeController.getCollisionRect().overlaps(enemy.getCollisionRect())){
                            // If it collides, set the worm to idle state
                            worm.attack();
                            if(worm.isDoneAttacking()){
                                treeController.takeDamage(1); // Assuming the worm deals 1 damage
                            }
                        }
                    }

                }
            }
        }
    }

    private void updateEnemyManager(float delta) {
        spawnWorm(delta);
        enemyManager.update(delta);

        for(EnemyController enemy : enemyManager.getEnemies()) {
            if(enemy instanceof WormController){
                if(enemy.isDead()){
                    enemyControllersToBeRemove.add(enemy);
                }
            }
        }

        if(!enemyControllersToBeRemove.isEmpty()){
            for(EnemyController enemy : enemyControllersToBeRemove) {
                wormPool.returnEnemy((WormController) enemy);
            }
            enemyControllersToBeRemove.clear();
        }

    }

    private void spawnWorm(float delta) {
        Vector2 treePos = treePositions.get("ordinary");
        if(treePos == null){
            throw new RuntimeException("Tree position for 'ordinary' tree not found.");
        }

        spawnTimer += delta;
        if (spawnTimer >= spawnInterval && wormPool.getActiveCount() < 5) { // Limit to 5 worms at a time
            Vector2 spawnPos = new Vector2(WINDOW_WIDTH + 50f, treePos.y);
            WormController worm = wormPool.getWorm(spawnPos);
            if(worm != null){
                System.out.println("L2 - Worm spawned at: " + spawnPos);
                enemyManager.addEnemy(worm);
            }
            spawnTimer = 0; // Reset the timer after spawning an enemy

        }

    }



    private void updateTreeManager(float delta) {
        treeControllerManager.update(delta);
    }

    private void updateToolManager(float delta) {
        toolManager.update(delta);
        toolManager.setIsPlanting(treeControllerManager.isCurrentTreeMatured());
    }


    private void draw(float delta) {
        stateTime += delta;

        camera.update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        backgroundSprite.draw(batch);
        toolManager.render(batch);
        treeControllerManager.draw(batch);
        enemyManager.draw(batch);

        batch.end();
        debugSprite();

        uiBatch.setProjectionMatrix(camera.combined);
        uiBatch.begin();
        currency.update(delta);
        currency.draw(uiBatch);
        uiBatch.end();
    }


    private void input() {
        if (Gdx.input.isTouched()) {
            currentTouchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(currentTouchPos);

            if(!isDragging && !isReturning){
                // Only try to pick up a tool if we're not already dragging
                draggingTool = toolManager.getToolAt(currentTouchPos);
                if (draggingTool != null) {
                    isDragging = true;
                    isReturning = false;
                }
            }else if (isDragging && draggingTool != null){ //ensure the tool is not null
                // Update tool position while dragging
                float xPos = currentTouchPos.x - draggingTool.getSprite().getWidth() / 2;
                float yPos = currentTouchPos.y - draggingTool.getSprite().getHeight() / 2;
                draggingTool.setPosition(new Vector2(xPos, yPos));
            }
        }else if(isDragging){
            //on mouse release, check for interaction
            if(draggingTool != null){
                //handle tool interactions
                handleToolInteractions(draggingTool);
            }
            isDragging = false;
            isReturning = true;
        }
    }

    private void handleToolInteractions(GameSprite draggingTool) {
        // Check if the tool is a sapling and if it can be planted
        if (treeControllerManager.interactWithTrees(draggingTool)) {
            //because the is tree who change the image, so treeControllerManager is used to perform action
            // If a sapling was successfully planted, handle the planting logic
            System.out.println("L2Screen: Is planted");
            toolManager.handleSaplingPlanting(draggingTool);

        }

        //watercan from toolManager, so perform it on toolManager
        if(draggingTool instanceof WateringCan){
            //after interact with trees using watering can, empty the water can
            toolManager.emptyWaterCan();
        }
        if(draggingTool instanceof Shovel){
            for(EnemyController enemy : enemyManager.getEnemies()){
                if(enemy.getCollisionRect().overlaps(draggingTool.getCollisionRect())){
                    if(enemy instanceof WormController){
                        enemy.die();
                    }
                }
            }

        }
        // Check if the tool is a watering can and if it can water a fountain
        toolManager.isWaterCansCollideRefillWater(waterFountain);
    }

    private void returnOriginalPosition() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if(isReturning && draggingTool != null){
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
////                for (Worm worm : worms) {
////                    if (worm.getCollisionRect().overlaps(shovel.getCollisionRect())) {
////                        isShovelReleased = true; //set to true when shovel is used
////                    }
////                }
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

        toolManager.drawDebug(shapeRenderer);
        treeControllerManager.drawDebug(shapeRenderer);
        waterFountain.drawDebug(shapeRenderer);
        enemyManager.drawDebug(shapeRenderer);
//
//        for (Worm worm : worms) {
//            worm.drawDebug(shapeRenderer);
//        }


//        debugSpawnArea();

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        uiBatch.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        shapeRenderer.dispose();
        toolManager.dispose();
        treeControllerManager.dispose();
        enemyManager.dispose();

//
//        for(GameSprite worm: worms){
//            worm.dispose();
//        }
//
//        for (BaseTreeHealth treeHealth : treeHealths.values()) {
//            treeHealth.dispose();
//        }
//
//        wateringCan.dispose();
        waterFountain.dispose();
//        shovel.dispose();
        if (currency != null) currency.dispose();
    }
}
