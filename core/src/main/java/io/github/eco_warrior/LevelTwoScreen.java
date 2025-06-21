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
import io.github.eco_warrior.controller.Enemy.MetalChuckController;
import io.github.eco_warrior.controller.Enemy.WormController;
import io.github.eco_warrior.controller.Manager.EnemyManager;
import io.github.eco_warrior.controller.FertilizerController;
import io.github.eco_warrior.controller.GroundTrashController;
import io.github.eco_warrior.controller.Manager.ButtonManager;
import io.github.eco_warrior.controller.Manager.ToolManager;
import io.github.eco_warrior.controller.Manager.TreeControllerManager;
import io.github.eco_warrior.controller.Pools.EnemyPool;
import io.github.eco_warrior.controller.Pools.MetalChuckPool;
import io.github.eco_warrior.controller.Pools.WormPool;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.controller.Trees.*;
import io.github.eco_warrior.entity.GameSprite;

import java.util.*;

import io.github.eco_warrior.enums.ButtonEnums;
import io.github.eco_warrior.enums.GardeningEnums;
import io.github.eco_warrior.enums.TreeType;
import io.github.eco_warrior.sprite.*;
import io.github.eco_warrior.sprite.buttons.FertilizerButton;
import io.github.eco_warrior.sprite.buttons.UpgradePotionButton;
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

    private GroundTrashController groundTrashController;

    private FertilizerController fertilizerController;

    //buttons
    private ButtonManager buttonManager;
    private float manipulatorY;
    private float startX;

    //entities declaration
    private WateringCan wateringCan;
    private WaterFountain waterFountain;

    //currency
    private Currency currency;

    //input selection
    private Vector2 currentTouchPos;
    private Vector2 lastTouchPos;
    private boolean isDragging = false;
    private boolean isReleased = true;
    private boolean isReturning = false;
    private GameSprite draggingTool;

    //enemies
    private EnemyManager enemyManager;
    private WormPool wormPool;
    private MetalChuckPool metalChuckPool;
    private ArrayList<EnemyController> enemyControllersToBeRemove;
    private float spawnTimer = 0f;
    private float spawnInterval = 4f;

    private final Random rand = new Random();

    private Map<TreeType, Vector2> treePositions = new HashMap<>();


    public LevelTwoScreen(Main main) {
        this.game = main;
        this.toolManager = new ToolManager();
        this.treeControllerManager = new TreeControllerManager();
        this.buttonManager = new ButtonManager();
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
        initializeTrashArea();
        initializeEnemyManager();

        initializeCurrencyUI();
        initializeButtons();
    }

    private void initializeCurrencyUI() {
        currency = new Currency(new Vector2(20, WINDOW_HEIGHT - 60), 0.2f, camera);
    }

    private void initializeTrashArea() {
        groundTrashController = new GroundTrashController(
            0,
            WINDOW_WIDTH - 100f,
            20f,
            WINDOW_HEIGHT/2
        );

    }

    private void initializeEnemyManager() {
        enemyManager = new EnemyManager();
        enemyControllersToBeRemove = new ArrayList<>();
        wormPool = new WormPool(enemyManager);
        metalChuckPool = new MetalChuckPool(enemyManager);

        wormPool.setAttackTreeType(
            new ArrayList<>(
                Arrays.asList(TreeType.ORDINARY, TreeType.ICE)
            )
        );

        metalChuckPool.setAttackTreeType(
            new ArrayList<>(
                Arrays.asList(TreeType.BLAZING, TreeType.BREEZING)
            )
        );

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
        fertilizerController = new FertilizerController(new Vector2(spacing * 4 - manipulatorX, startY), toolScale);
        DebugStick debugStick = new DebugStick(new Vector2(spacing * 6 - manipulatorX, startY), .25f);
        toolManager.addTool(GardeningEnums.WATERING_CAN, wateringCan);
        toolManager.addTool(GardeningEnums.SHOVEL, shovel);
        toolManager.addTool(GardeningEnums.RAY_GUN, rayGun);
//        toolManager.addTool(GardeningEnums.DEBUG_STICK, debugStick);
        toolManager.addFertilizerController(fertilizerController);


        initializeSapling(spacing, toolScale);
    }

    private void initializeButtons() {
        int buttonCount = 2;
        float spacing = WINDOW_HEIGHT / (buttonCount + 4); //make it 10 so it look from left to right
        float buttonScale = .09f;

        startX = WINDOW_WIDTH - 100;
        manipulatorY = 350;

        FertilizerButton fertilizerButton = new FertilizerButton(new Vector2(startX, spacing + manipulatorY), buttonScale);
        UpgradePotionButton upgradePotionButton = new UpgradePotionButton(new Vector2(startX, spacing * 2 + manipulatorY), buttonScale);
        buttonManager.addButton(ButtonEnums.FERTILIZER_BUTTON, fertilizerButton);
        buttonManager.addButton(ButtonEnums.UPGRADE_POTION_BUTTON, upgradePotionButton);
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

        treePositions.put(TreeType.ORDINARY, new Vector2(763, 122));
        treePositions.put(TreeType.BLAZING, new Vector2(1048, 256));
        treePositions.put(TreeType.BREEZING, new Vector2(920, 183));
        treePositions.put(TreeType.ICE, new Vector2(1023, 25));
        treePositions.put(TreeType.VOLTAIC, new Vector2(781, 299));

        TreeController<OrdinaryTree> ordinaryTreeController = new OrdinaryTreeController(
            new OrdinaryTree(treePositions.get(TreeType.ORDINARY), treeScale),
            wateringCan
        );
        TreeController<BlazingTree> blazingTreeController = new BlazingTreeController(
            new BlazingTree(treePositions.get(TreeType.BLAZING), treeScale),
                wateringCan
        );
        TreeController<BreezingTree> breezingTreeController = new BreezingTreeController(
            new BreezingTree(treePositions.get(TreeType.BREEZING), treeScale),
            wateringCan
        );
        TreeController<IceTree> iceTreeController = new IceTreeController(
            new IceTree(treePositions.get(TreeType.ICE), treeScale),
            wateringCan
        );
        TreeController<VoltaicTree> voltaicTreeController = new VoltaicTreeController(
            new VoltaicTree(treePositions.get(TreeType.VOLTAIC), treeScale),
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


//        updateTrashController(delta);
//        updateButtonManager(delta);

    }

    private void updateEnemyTreeLogic(float delta) {

        for(EnemyController enemy: enemyManager.getEnemies()){
            checkTreeCollisionsAndAttack(enemy);
        }
    }

    private void checkTreeCollisionsAndAttack(EnemyController enemy) {
        for (TreeController<?> treeController : treeControllerManager.getTreeControllers()) {
            TreeType currentTreeType = treeController.getTreeType();

            if (enemy.getCurrentAttackTreeType() == currentTreeType &&
                treeController.getCollisionRect().overlaps(enemy.getCollisionRect())) {

                if(!enemy.isAttacking()){
                    enemy.attack();
                    //System.out.println("L2 - Enemy " + enemy.getClass().getSimpleName() + " is attacking tree: " + currentTreeType);
                }
                //TODO - solve the issue of enemy done attack then take damage
                if ( enemy.isAttacking()) {
                    System.out.println("L2 - Enemy " + enemy.getClass().getSimpleName() + " has finished attacking tree: " + currentTreeType);
                    treeController.takeDamage(1);
                    enemy.resetAttackState();
                    // Reset the attack state after attacking
                }
            }
        }
    }

    private void updateEnemyManager(float delta) {
        spawnWorm(delta);
        spawnMetalChuck(delta);
        enemyManager.update(delta);

        for(EnemyController enemy : enemyManager.getEnemies()) {
            if(enemy.isDead()){
                addBackEnemyToPool(enemy);
                enemyControllersToBeRemove.add(enemy);
            }
        }

        if(!enemyControllersToBeRemove.isEmpty()){
            System.out.println("L2 - Removing enemies from enemy manager: " + enemyControllersToBeRemove.size());
            for(EnemyController enemy : enemyControllersToBeRemove) {
                enemyManager.getEnemies().remove(enemy);
            }
            enemyControllersToBeRemove.clear();
        }

    }

    private <T extends EnemyPool> void addBackEnemyToPool(EnemyController enemy) {
        if (enemy instanceof WormController) {
            wormPool.returnEnemy((WormController) enemy);
        } else if (enemy instanceof MetalChuckController) {
            metalChuckPool.returnEnemy((MetalChuckController) enemy);
        } else {
            throw new IllegalArgumentException("Unknown enemy type: " + enemy.getClass().getSimpleName());
        }
    }

    private void spawnMetalChuck(float delta) {
        spawnEnemy(delta, metalChuckPool);
    }

    private void spawnWorm(float delta) {
        spawnEnemy(delta, wormPool);

    }

    private <T extends EnemyController> void spawnEnemy(float delta, EnemyPool<T> pool) {
        spawnTimer += delta;
        if (spawnTimer >= spawnInterval && pool.getActiveCount() < 5) { // Limit to 5 enemies at a time
            ArrayList<TreeType> treeTypes = pool.getAttackTreeType();

            int randomIndex = rand.nextInt(0, treeTypes.size());
            float ypos = treePositions.get(treeTypes.get(randomIndex)).y;

            Vector2 spawnPos = new Vector2(WINDOW_WIDTH + 50f, ypos);
            T enemy = pool.getEnemy(spawnPos, treeTypes.get(randomIndex));
            if(enemy != null){
                enemyManager.addEnemy(enemy);
            }
            spawnTimer = 0; // Reset the timer after spawning an enemy
        }
    }

    private void updateTrashController(float delta) {
        groundTrashController.update(delta);
    }

    private void updateTreeManager(float delta) {
        treeControllerManager.update(delta);
    }

    private void updateToolManager(float delta) {
        toolManager.update(delta);
        toolManager.setIsPlanting(treeControllerManager.isCurrentTreeMatured());
    }

    private void updateButtonManager(float delta) {
        buttonManager.update(delta);
    }


    private void draw(float delta) {

        camera.update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        backgroundSprite.draw(batch);
        toolManager.render(batch);
        treeControllerManager.draw(batch);
        groundTrashController.draw(batch);
        buttonManager.draw(batch);
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
            isReleased = false;
            currentTouchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(currentTouchPos);

            if(!isDragging && !isReturning){
                // Only try to pick up a tool if we're not already dragging
                draggingTool = toolManager.getToolAt(currentTouchPos);
                if (draggingTool != null) {
                    isDragging = true;
                    isReturning = false;
                }
            } else if (isDragging && draggingTool != null){ //ensure the tool is not null
                // Update tool position while dragging
                float xPos = currentTouchPos.x - draggingTool.getSprite().getWidth() / 2;
                float yPos = currentTouchPos.y - draggingTool.getSprite().getHeight() / 2;
                draggingTool.setPosition(new Vector2(xPos, yPos));
            }



        } else if(isDragging){
            //on mouse release, check for interaction
            if(draggingTool != null){
                //handle tool interactions
                handleToolInteractions(draggingTool);
            }
            isDragging = false;
            isReturning = true;
        }else if (!isReleased && !Gdx.input.isTouched()){
            //if click released
            collectTrashLogic();
            clickButtonLogic();
        }
    }

    private void collectTrashLogic() {
        //trash is removed when clicked
        if(groundTrashController.isCollected(currentTouchPos)){
            currency.addMoney(1);
            isReleased = true;
        }
    }

    private void clickButtonLogic() {

        if(buttonManager.purchase(currentTouchPos, currency)) { //check if a purchase is successful

            if (buttonManager.buttonType == ButtonManager.ButtonType.FERTILIZER_BUTTON) {
                toolManager.addFertilizerController(fertilizerController);

            } else if (buttonManager.buttonType == ButtonManager.ButtonType.UPGRADE_POTION_BUTTON){
                // add potion function here
            }
            isReleased = true;


        }
    }

    private void handleToolInteractions(GameSprite draggingTool) {
        // Check if the tool is a sapling and if it can be planted
        if (treeControllerManager.interactWithTrees(draggingTool)) {
            //because the is tree who change the image, so treeControllerManager is used to perform action
            toolManager.handleSaplingPlanting(draggingTool);
            //not my code
            toolManager.handleFertilizerUsing(draggingTool);

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
                        if(!enemy.isDead()){
                            enemy.die();
                            draggingTool.playSound();
                        }
                    }
                    if(enemy instanceof MetalChuckController){
                        if(!enemy.isDead()){
                            enemy.die();
                            draggingTool.playSound();
                        }
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
        buttonManager.drawDebug(shapeRenderer);
        waterFountain.drawDebug(shapeRenderer);
        groundTrashController.drawDebug(shapeRenderer);
        enemyManager.drawDebug(shapeRenderer);

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
        groundTrashController.dispose();
        buttonManager.dispose();
        enemyManager.dispose();
        wateringCan.dispose();

        waterFountain.dispose();
        if (currency != null) currency.dispose();
    }
}
