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
import io.github.eco_warrior.controller.FontGenerator;
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

import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.enums.ButtonEnums;
import io.github.eco_warrior.enums.GardeningEnums;
import io.github.eco_warrior.enums.TreeType;
import io.github.eco_warrior.screen.ResultScreen;
import io.github.eco_warrior.sprite.*;
import io.github.eco_warrior.sprite.UI.CooldownReductionTimer;
import io.github.eco_warrior.sprite.UI.GunElementUI;
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
    private float stateTime;

    //cooldown reduction timer
    private CooldownReductionTimer cooldownReductionTimer;

    //gun elements
    private GunElementUI gunElementUI;

    //gun elements drawers
    private io.github.eco_warrior.sprite.gun_elements.BlazingTreeFireElementDrawer blazingTreeFireElementDrawer;
    private io.github.eco_warrior.sprite.gun_elements.BreezingTreeWindElementDrawer breezingTreeWindElementDrawer;
    private io.github.eco_warrior.sprite.gun_elements.IceTreeIceElementDrawer iceTreeIceElementDrawer;
    private io.github.eco_warrior.sprite.gun_elements.VoltaicTreeLightningElementDrawer voltaicTreeLightningDrawer;

    private io.github.eco_warrior.sprite.gardening_equipments.RayGun rayGun;

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
    private List<EnemyController> hiddenEnemies;
    private float spawnTimer = 0f;
    private float averageSpawnInterval = 5f;

    private final Random rand = new Random();

    private Map<TreeType, Vector2> treePositions = new HashMap<>();

    //font
    private FontGenerator timerFont;

    //winning condition timer
    private float gameTimer = 0f;
    private final float MAX_GAME_TIME = 60f; // 3 minutes in seconds
    private boolean gameOver = false;

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

        timerFont = new FontGenerator(32, Color.WHITE, Color.BLACK);

        //setup camera to middle
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);

        //initialize GunElement atlas
        gunElementUI = new GunElementUI("atlas/gun_element/GunElement.atlas", "atlas/gun_element/Lighting.atlas", "atlas/gun_element/Fire.atlas", "atlas/gun_element/Wind.atlas", "atlas/gun_element/Ice.atlas");


        initializeTools();
        initializeTrees();
        initializeTrashArea();
        initializeEnemyManager();

        initializeCurrencyUI();
        initializeButtons();
        initializeCooldownReductionTimerUI();

//initialize for drawing gun elements + elements hiding time
        blazingTreeFireElementDrawer = new io.github.eco_warrior.sprite.gun_elements.BlazingTreeFireElementDrawer(treeControllerManager, gunElementUI, 3000); // 3 seconds
        breezingTreeWindElementDrawer = new io.github.eco_warrior.sprite.gun_elements.BreezingTreeWindElementDrawer(treeControllerManager, gunElementUI, 3000); // 3 seconds
        iceTreeIceElementDrawer = new io.github.eco_warrior.sprite.gun_elements.IceTreeIceElementDrawer(treeControllerManager, gunElementUI, 3000); // 3 seconds
        voltaicTreeLightningDrawer = new io.github.eco_warrior.sprite.gun_elements.VoltaicTreeLightningElementDrawer(treeControllerManager, gunElementUI, 3000); // 3 seconds

    }

    private void initializeCurrencyUI() {
        currency = new Currency(new Vector2(20, WINDOW_HEIGHT - 60), 0.2f, camera);
    }

    private void initializeCooldownReductionTimerUI() {
        cooldownReductionTimer = new CooldownReductionTimer(new Vector2(WINDOW_WIDTH / 2 + 250, WINDOW_HEIGHT / 2), 0.5f);
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
        hiddenEnemies = new ArrayList<>();
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


        rayGun = new io.github.eco_warrior.sprite.gardening_equipments.RayGun(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
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
        BaseSaplingController ordinarySapling = new OrdinarySapling(new Vector2(spacing - manipulatorX, startY), toolScale);
        BaseSaplingController blazingSapling = new BlazingSapling(new Vector2(spacing - manipulatorX, startY), toolScale);
        BaseSaplingController breezingSapling = new BreezingSapling(new Vector2(spacing - manipulatorX, startY), toolScale);
        BaseSaplingController iceSapling = new IceSapling(new Vector2(spacing - manipulatorX, startY), toolScale);
        BaseSaplingController voltaicSapling = new VoltaicSapling(new Vector2(spacing - manipulatorX, startY), toolScale);



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
        drawUI(delta);
        returnOriginalPosition();

        updateToolManager(delta);
        updateTreeManager(delta);
        updateEnemyManager(delta);
        updateEnemyTreeLogic(delta);


        updateTrashController(delta);
        updateButtonManager(delta);
        updateTimerAnimation(delta);
        winningCondition(delta);

    }

    private void winningCondition(float delta) {
        if(gameOver) return;

        gameTimer += delta;

        boolean allTreesMatured = true;
        for(TreeController<?> treeController : treeControllerManager.getTreeControllers()) {
            if (!treeController.isMaturedAliveTree()) {
                allTreesMatured = false;
                break;
            }
        }

        if(allTreesMatured && gameTimer >= MAX_GAME_TIME){
            gameOver = true;
            showWinScreen();
            return;
        } else if (gameTimer >= MAX_GAME_TIME) {
            gameOver = true;
            showLoseScreen();
            return;
        }
    }

    private void showLoseScreen() {
        game.setScreen(new ResultScreen(game, 0, true, "Time out! You lose!"));
    }

    private void showWinScreen() {
        game.setLevel(3);
        game.setScreen(new ResultScreen(game, 0, false, "All trees are matured! You win!"));
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
                }
                enemy.isAnimDoneAttacking(treeController);
            }
        }
    }

    private void updateEnemyManager(float delta) {
        spawnWorm(delta);
        spawnMetalChuck(delta);
        enemyManager.update(delta);


        Iterator<EnemyController> activeIterator = enemyManager.getEnemies().iterator();
        while (activeIterator.hasNext()) {
            EnemyController enemy = activeIterator.next();
            if (enemy.isDead()) {
                activeIterator.remove();
                //reset and return to pool
                addBackEnemyToPool(enemy);
            }
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
        spawnEnemy(delta, metalChuckPool, averageSpawnInterval);
    }

    private void spawnWorm(float delta) {
        spawnEnemy(delta, wormPool, averageSpawnInterval);

    }

    private <T extends EnemyController> void spawnEnemy(float delta, EnemyPool<T> pool, float spawnInterval) {
        spawnTimer += delta;

        if(spawnTimer < spawnInterval && pool.getActiveCount() >= 5){
            return; // Early exit if conditions aren't met
        }

        TreeController<?> selectedTree = null;
        ArrayList<TreeType> targetTreeTypes = pool.getAttackTreeType();
        ArrayList<TreeController> readyTrees = new ArrayList<>();

        // Find a random tree ready for attack
        ArrayList<TreeController> treeControllers = treeControllerManager.getTreeControllers();

        for (TreeController<?> treeController : treeControllers) {
            if (targetTreeTypes.contains(treeController.getTreeType()) && treeController.isPlanted()) {
                readyTrees.add(treeController);
            }
        }

        if (!readyTrees.isEmpty()) {
            selectedTree = readyTrees.get(rand.nextInt(readyTrees.size()));
        }else{
            return;
        }

        if (selectedTree != null) {
            TreeType selectedType = selectedTree.getTreeType();
            float ypos = treePositions.get(selectedType).y;

            Vector2 spawnPos = new Vector2(WINDOW_WIDTH + 50f, ypos);
            pool.getEnemy(spawnPos, selectedType);
            spawnTimer = 0; // Reset the timer after spawning an enemy
        }


    }

    private void updateTrashController(float delta) {
        groundTrashController.update(delta);
    }

    private void updateTimerAnimation(float delta) {
        cooldownReductionTimer.update(delta);
    }

    private void updateTreeManager(float delta) {
        treeControllerManager.update(delta);
    }

    private void updateToolManager(float delta) {
        toolManager.update(delta);
        toolManager.setIsPlanting(treeControllerManager.isPlanting());
    }

    private void updateButtonManager(float delta) {
        buttonManager.update(delta);
    }


    private void draw(float delta) {

        camera.update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateTime = Gdx.graphics.getDeltaTime();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        backgroundSprite.draw(batch);
        toolManager.render(batch);
        treeControllerManager.draw(batch);
        groundTrashController.draw(batch);
        buttonManager.draw(batch);
        drawCooldownReductionTimer();
        enemyManager.draw(batch);

        //draw the elements for gun
        blazingTreeFireElementDrawer.draw(batch, stateTime/*, rayGun.getMode()*/);
        voltaicTreeLightningDrawer.draw(batch, stateTime/*, rayGun.getMode()*/);
        breezingTreeWindElementDrawer.draw(batch, stateTime/*, rayGun.getMode()*/);
        iceTreeIceElementDrawer.draw(batch, stateTime/*, rayGun.getMode()*/);

        batch.end();
//        debugSprite();

    }

    private void drawCooldownReductionTimer() {
        if (CooldownReductionTimer.isAnimationPlayed) {
            cooldownReductionTimer.draw(batch);
        }
    }

    private void drawUI(float delta) {
        //Currency UI
        uiBatch.setProjectionMatrix(camera.combined);
        uiBatch.begin();
        currency.update(delta);
        currency.draw(uiBatch);
        drawTimer(delta);

        uiBatch.end();
    }

    private void drawTimer(float delta) {
        if (gameOver) return;

        // Calculate time left
        float timeLeft = Math.max(0, MAX_GAME_TIME - gameTimer);
        int minutes = (int)(timeLeft / 60);
        int seconds = (int)(timeLeft % 60);

        // Format time as MM:SS
        String timeText = "Time left: " + String.format("%02d:%02d", minutes, seconds);

        // Draw timer at top right
        Vector2 timerPosition = new Vector2(WINDOW_WIDTH - 130, WINDOW_HEIGHT - 20);

        // Use the fontDraw method with RIGHT alignment
        timerFont.fontDraw(uiBatch, timeText, camera, timerPosition);

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
                    // Not drawn sapling cannot be dragged
                    if (draggingTool instanceof BaseSaplingController) {
                        if (toolManager.canDragSaplingAt(currentTouchPos)) {
                            isDragging = true;
                            isReturning = false;
                        }
                    } else {
                        isDragging = true;
                        isReturning = false;
                    }
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

        //Handle clicks for gun elements
        if (Gdx.input.justTouched()) {
            float sx = Gdx.input.getX();
            float sy = Gdx.graphics.getHeight() - Gdx.input.getY(); // flip y
            blazingTreeFireElementDrawer.handleClick(sx, sy);
            breezingTreeWindElementDrawer.handleClick(sx, sy);
            iceTreeIceElementDrawer.handleClick(sx, sy);
            voltaicTreeLightningDrawer.handleClick(sx, sy);
            //when the gun element is clicked, set the rayGun mode
            if (blazingTreeFireElementDrawer.wasLastIconClicked()) {
                rayGun.setMode(io.github.eco_warrior.sprite.gardening_equipments.RayGun.RayGunMode.BLAZING);
                rayGun.playModeSound();
            } else if (breezingTreeWindElementDrawer.wasLastIconClicked()) {
                rayGun.setMode(io.github.eco_warrior.sprite.gardening_equipments.RayGun.RayGunMode.BREEZING);
                rayGun.playModeSound();
            } else if (iceTreeIceElementDrawer.wasLastIconClicked()) {
                rayGun.setMode(io.github.eco_warrior.sprite.gardening_equipments.RayGun.RayGunMode.ICE);
                rayGun.playModeSound();
            } else if (voltaicTreeLightningDrawer.wasLastIconClicked()) {
                rayGun.setMode(io.github.eco_warrior.sprite.gardening_equipments.RayGun.RayGunMode.VOLTAIC);
                rayGun.playModeSound();
            }
            // to set back to useless mode
            //rayGun.setMode(RayGun.RayGunMode.USELESS);
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
                long newHideMs;
                newHideMs = blazingTreeFireElementDrawer.getHideMs() - 1000;
                blazingTreeFireElementDrawer.setHideMs(newHideMs);
                newHideMs = breezingTreeWindElementDrawer.getHideMs() - 1000;
                breezingTreeWindElementDrawer.setHideMs(newHideMs);
                newHideMs = iceTreeIceElementDrawer.getHideMs() - 1000;
                iceTreeIceElementDrawer.setHideMs(newHideMs);
                newHideMs = voltaicTreeLightningDrawer.getHideMs() - 1000;
                voltaicTreeLightningDrawer.setHideMs(newHideMs);

                cooldownReductionTimer.clockRun();
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
        if (draggingTool instanceof WateringCan) {
            for (TreeController<?> treeController : treeControllerManager.getTreeControllers()) {
                // Empty watering can on appropriate tree stages
                if (draggingTool.getCollisionRect().overlaps(treeController.getCollisionRect())
                    && (treeController.getStage() == Trees.TreeStage.SAPLING
                    || treeController.getStage() == Trees.TreeStage.YOUNG_TREE)) {
                    toolManager.emptyWaterCan();
                    break; // Only need to empty once per overlap
                }
            }
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

        if(draggingTool instanceof RayGun){
            if (rayGun.getMode() != RayGun.RayGunMode.USELESS) {
                for(EnemyController enemy : enemyManager.getEnemies()){
                    if(enemy.getCollisionRect().overlaps(draggingTool.getCollisionRect())){
                        if(enemy instanceof WormController){
                            if(!enemy.isDead()){
                                enemy.die();
                                rayGun.playModeSound();
                            }
                        }
                        if(enemy instanceof MetalChuckController){
                            if(!enemy.isDead()){
                                enemy.die();
                                rayGun.playModeSound();
                            }
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
        cooldownReductionTimer.drawDebug(shapeRenderer);
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
        cooldownReductionTimer.dispose();
        enemyManager.dispose();
        wateringCan.dispose();
        if(timerFont != null) timerFont.dispose();

        waterFountain.dispose();
        if (currency != null) currency.dispose();
    }
}
