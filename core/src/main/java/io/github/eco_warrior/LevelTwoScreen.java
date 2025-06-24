package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.controller.Enemy.*;
import io.github.eco_warrior.controller.FontGenerator;
import io.github.eco_warrior.controller.Manager.*;
import io.github.eco_warrior.controller.FertilizerController;
import io.github.eco_warrior.controller.GroundTrashController;
import io.github.eco_warrior.controller.Pools.*;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.controller.Trees.*;
import io.github.eco_warrior.entity.GameSprite;

import java.util.*;

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
    private GunManager gunManager;
    private RayGun rayGun;

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
    private IceCrabPool iceCrabPool;
    private BombPeckerPool bombPeckerPool;
    private TentaclesPool tentaclesPool;
    private List<EnemyController> hiddenEnemies;
    private float spawnTimer = 0f;
    private float averageSpawnInterval = 10f;

    private float wormSpawnTimer = 0f;
    private float metalChuckSpawnTimer = 0f;
    private float bombPeckerSpawnTimer = 0f;
    private float iceCrabSpawnTimer, tentaclesSpawnTimer = 0f;

    private final Random rand = new Random();

    private Map<TreeType, Vector2> treePositions = new HashMap<>();

    //font
    private FontGenerator timerFont;

    //winning condition timer
    private float gameTimer = 0f;
    private final float MAX_GAME_TIME = 60f; // 1 minutes in seconds
    private boolean gameOver,isTimerStarts = false;
    //show boss
    private boolean showBigBoss, showSmallBoss = false;
    private BigOctopusBossController boss;
    private WaterOctopusController smallBoss;

    //Music
    private Music countdownMusic;
    private Music backgroundMusic;
    private boolean musicStarted = false;

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
        backgroundTexture = new Texture(Gdx.files.internal("textures/greenland.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        lastTouchPos = new Vector2();
        currentTouchPos = new Vector2();
        shapeRenderer = new ShapeRenderer();

        timerFont = new FontGenerator(32, Color.WHITE, Color.BLACK);

        //setup camera to middle
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);


        initializeMusic();
        initializeTools();
        initializeTrees();
        initializeTrashArea();
        initializeEnemyManager();
        initializeCurrencyUI();
        initializeButtons();
        initializeCooldownReductionTimerUI();
        initializeBoss();

        initializeGun();

    }

    private void initializeMusic() {
        countdownMusic = Gdx.audio.newMusic(Gdx.files.internal("Background_Music/KEROSENE-last60secs.mp3"));
        countdownMusic.setVolume(0.5f);
        countdownMusic.setLooping(false);
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Background_Music/forest-nature.mp3"));
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }

    private void initializeGun() {
        //initialize for drawing gun elements + elements hiding time
        gunManager = new GunManager(treeControllerManager, rayGun, 3000);// 3 seconds
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
        bombPeckerPool = new BombPeckerPool(enemyManager);
        iceCrabPool = new IceCrabPool(enemyManager);
        tentaclesPool = new TentaclesPool(enemyManager);

        //TODO - change enemy Tree Attack Type
        tentaclesPool.setAttackTreeType(
            new ArrayList<>(
                Arrays.asList(TreeType.ICE, TreeType.BLAZING)
            )
        );

        iceCrabPool.setAttackTreeType(
            new ArrayList<>(
                Collections.singletonList(TreeType.BLAZING)
            )
        );

        wormPool.setAttackTreeType(
            new ArrayList<>(
                Arrays.asList(TreeType.ORDINARY, TreeType.VOLTAIC, TreeType.BLAZING, TreeType.BREEZING, TreeType.ICE)
            )
        );

        metalChuckPool.setAttackTreeType(
            new ArrayList<>(
                Arrays.asList(TreeType.VOLTAIC, TreeType.BREEZING, TreeType.ICE, TreeType.BLAZING)
            )
        );

        bombPeckerPool.setAttackTreeType(
            new ArrayList<>(
                Arrays.asList(TreeType.BLAZING, TreeType.BREEZING, TreeType.ICE)
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

    private void initializeBoss(){
        Vector2 bossSpawnPos = new Vector2(50, 180f);
        smallBoss = new WaterOctopusController(bossSpawnPos);

        bossSpawnPos = new Vector2(10, 150f);
        boss = new BigOctopusBossController(bossSpawnPos);
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



        //TODO - edit back sapling list
        //following tier list
//        toolManager.addSaplingController(iceSapling); //debug debug

        toolManager.addSaplingController(ordinarySapling);
        toolManager.addSaplingController(voltaicSapling);
        toolManager.addSaplingController(breezingSapling);
        toolManager.addSaplingController(iceSapling);
        toolManager.addSaplingController(blazingSapling);

        // Initially only make the first sapling available
        toolManager.setSaplingAvailable(iceSapling, true); //debug debug

        toolManager.setSaplingAvailable(ordinarySapling, true);
        toolManager.setSaplingAvailable(voltaicSapling, false);
        toolManager.setSaplingAvailable(breezingSapling, false);
        toolManager.setSaplingAvailable(iceSapling, false);
        toolManager.setSaplingAvailable(blazingSapling, false);

    }

    private void initializeTrees() {
        float treeScale = 0.20f;

        treePositions.put(TreeType.ORDINARY, new Vector2(763, 122));
        treePositions.put(TreeType.BLAZING, new Vector2(1048, 256));
        treePositions.put(TreeType.BREEZING, new Vector2(920, 183));
        treePositions.put(TreeType.ICE, new Vector2(1013, 50));
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
        updateShowBoss(delta);


    }

    private void updateShowBoss(float delta) {

        if(showBigBoss){
            boss.update(delta);
        }
        if(showSmallBoss){
            smallBoss.update(delta);
        }
    }


    private void winningCondition(float delta) {
        if(gameOver) return;


        boolean allTreesMatured = true;
        boolean allTreeMaturedAlive = true;
        for(TreeController<?> treeController : treeControllerManager.getTreeControllers()) {
            if(!treeController.isMaturedTree()){
                allTreesMatured = false;
                break;
            }

        }

        if(allTreesMatured){
            showSmallBoss = false; //hide boss when all trees are matured
            showBigBoss = true;
            isTimerStarts = true;
            gameTimer += delta;
            //all trees are matured, show start timer
            // Play the countdown music when the timer starts
            if(!musicStarted) {
                backgroundMusic.stop();
                countdownMusic.play();
                musicStarted = true;
            }
        }


        for(TreeController<?> treeController : treeControllerManager.getTreeControllers()) {
            if (!treeController.isMaturedAliveTree()) {
                allTreeMaturedAlive = false;
                break;
            }
        }


        if(gameTimer >= MAX_GAME_TIME && allTreeMaturedAlive) {
            gameOver = true;
            countdownMusic.stop();
            showWinScreen();
            return;
        }

        if(gameTimer >= MAX_GAME_TIME && !allTreeMaturedAlive) {
            gameOver = true;
            countdownMusic.stop();
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

            if (enemy.getCurrentAttackTreeType() == currentTreeType) {

                if(enemy instanceof BombPeckerController){
                    BombPeckerController bombPecker = (BombPeckerController) enemy;
                    float treeBottomY = treeController.getCollisionRect().y;
                    float bombPeckerY = bombPecker.getSprite().getY();

                    // When BombPecker reaches slightly above the tree's bottom
                    if (bombPeckerY <= treeBottomY + 20 && !bombPecker.isAttacking()) {

                        if(treeController.getCollisionRect().overlaps(enemy.getCollisionRect())){
                            bombPecker.attack();
                        }

                    }
                    bombPecker.isAnimDoneAttacking(treeController);
                }
                else if(enemy instanceof IceCrabController){
                    IceCrabController iceCrab = (IceCrabController) enemy;
                    if(treeController.getCollisionRect().overlaps(enemy.getCollisionRect())){
                        if(iceCrab.isCurrentAnimationDone()){
                            iceCrab.attack(treeController);
                        }
                    }
                }else if(enemy instanceof  TentaclesController){
                    TentaclesController tentacles = (TentaclesController) enemy;
                    if(treeController.getCollisionRect().overlaps(enemy.getCollisionRect())){
                        if(tentacles.isCurrentAnimationDone()){
                            tentacles.attack(treeController);
                        }
                    }

                }
                else if(treeController.getCollisionRect().overlaps(enemy.getCollisionRect())){
                    if(!enemy.isAttacking()){
                        enemy.attack();
                    }
                    enemy.isAnimDoneAttacking(treeController);
                }

            }
        }
    }

    private void updateEnemyManager(float delta) {
        wormSpawnTimer += delta;
        metalChuckSpawnTimer += delta;
        bombPeckerSpawnTimer += delta;
        iceCrabSpawnTimer += delta;
        tentaclesSpawnTimer += delta;

        //TODO - add and edit enemyManager
        if (enemyManager.getEnemies().size() < 10) {
            spawnWorm();
            spawnMetalChuck();
            spawnBombPecker();
            spawnIceCrab();
            spawnTentacles();

        }

        enemyManager.update(delta);


        Iterator<EnemyController> activeIterator = enemyManager.getEnemies().iterator();
        while (activeIterator.hasNext()) {
            EnemyController enemy = activeIterator.next();

            if(enemy instanceof BombPeckerController){
                BombPeckerController bombPecker = (BombPeckerController) enemy;

                // Check if Bomb Pecker is dead or done attacking
                if (bombPecker.isDead()) {
                    activeIterator.remove();
                    addBackEnemyToPool(enemy);
                }
            }
            else if (enemy.isDead()) {
                activeIterator.remove();
                addBackEnemyToPool(enemy);
            }
        }

    }



    private <T extends EnemyPool> void addBackEnemyToPool(EnemyController enemy) {
        if (enemy instanceof WormController) {
            wormPool.returnEnemy((WormController) enemy);
        } else if (enemy instanceof MetalChuckController) {
            metalChuckPool.returnEnemy((MetalChuckController) enemy);
        } else if (enemy instanceof BombPeckerController) {
            bombPeckerPool.returnEnemy((BombPeckerController) enemy);
        } else if (enemy instanceof IceCrabController) {
            iceCrabPool.returnEnemy((IceCrabController) enemy);
        } else if (enemy instanceof TentaclesController) {
            tentaclesPool.returnEnemy((TentaclesController) enemy);
        }
        else {
            throw new IllegalArgumentException("Unknown enemy type: " + enemy.getClass().getSimpleName());
        }
    }

    private void spawnTentacles() {
        if(tentaclesSpawnTimer >= averageSpawnInterval + 10 && tentaclesPool.getActiveCount() < 2) {
            spawnEnemy(tentaclesPool);
            tentaclesSpawnTimer = 0;
        }
    }


    private void spawnIceCrab() {
        if(iceCrabSpawnTimer >= averageSpawnInterval + 6 && iceCrabPool.getActiveCount() < 2) {
            spawnEnemy(iceCrabPool);
            iceCrabSpawnTimer = 0;
        }
    }


    private void spawnMetalChuck() {
        if (metalChuckSpawnTimer >= averageSpawnInterval + 8 && metalChuckPool.getActiveCount() < 5) {
            spawnEnemy(metalChuckPool);
            metalChuckSpawnTimer = 0;
        }
    }

    private void spawnBombPecker() {
        if( bombPeckerSpawnTimer >= averageSpawnInterval + 4 && bombPeckerPool.getActiveCount() < 3) {
            spawnEnemy(bombPeckerPool); // Bomb Pecker has a longer spawn interval

            bombPeckerSpawnTimer = 0;
        }
    }

    private void spawnWorm() {

        if (wormSpawnTimer >= averageSpawnInterval && wormPool.getActiveCount() < 5) {
            spawnEnemy(wormPool);
            wormSpawnTimer = 0;
        }

    }

    private <T extends EnemyController> void spawnEnemy(EnemyPool<T> pool) {

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
            float xpos = treePositions.get(selectedType).x;
            float ypos = treePositions.get(selectedType).y;
            Vector2 spawnPos;
            if (pool instanceof BombPeckerPool){
                spawnPos = new Vector2( xpos, WINDOW_HEIGHT + 50F);
            }else if(pool instanceof IceCrabPool){
                spawnPos = new Vector2(xpos - 150f , ypos);
            }else if(pool instanceof TentaclesPool){
                spawnPos = new Vector2(xpos -  50f, ypos);
            }
            else{
                spawnPos = new Vector2(WINDOW_WIDTH + 50f, ypos);

            }
            pool.getEnemy(spawnPos, selectedType);
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

        for(TreeController<?> treeController : treeControllerManager.getTreeControllers()) {
            if(treeController.getTreeType() == TreeType.ICE &&
            treeController.isMaturedTree() && !showSmallBoss){
                showSmallBoss = true;
            }
            if (treeController.isMaturedTree() && !treeController.isMaturityProcessed()) {
                // Mark this tree as processed for unlocking
                treeController.setMaturityProcessed(true);

                // Unlock the next sapling in sequence
                BaseSaplingController nextSapling = toolManager.unlockNextSapling();

                // Make sure the newly unlocked sapling is available for dragging
                if (nextSapling != null) {
                    toolManager.setSaplingAvailable(nextSapling, true);
                    // Reset the isPlanting flag to allow dragging the new sapling
                    toolManager.setIsPlanting(false);
                }

            }
        }
    }

    private void updateToolManager(float delta) {
        toolManager.update(delta);

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
        drawBoss(batch);
        buttonManager.draw(batch);
        drawCooldownReductionTimer();
        enemyManager.draw(batch);

        //draw the elements for gun
        gunManager.draw(batch, stateTime);

        batch.end();
//        debugSprite();

    }

    private void drawBoss(SpriteBatch batch) {
        if(showBigBoss){
            boss.draw(batch);
        }
        if(showSmallBoss){
            smallBoss.draw(batch);
        }
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
        if(isTimerStarts){
            drawTimer(delta);
        }

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
            gunManager.handleClick(sx, sy);
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

                gunManager.decreaseHideTime(1000); // 1 second
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
        if(draggingTool instanceof WateringCan){
            //after interact with trees using watering can, empty the water can
            if(treeControllerManager.wasWateringSuccessful()){
                toolManager.emptyWaterCan();
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
                }
            }

        }

        // Check if the tool is a ray gun and if it can shoot
        if(draggingTool instanceof RayGun){
            ShotRayGun(draggingTool);
        }
        // Check if the tool is a watering can and if it can water a fountain
        toolManager.isWaterCansCollideRefillWater(waterFountain);
    }

    private void ShotRayGun(GameSprite draggingTool) {
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
                        if(rayGun.getMode() == RayGun.RayGunMode.VOLTAIC){
                            MetalChuckController metalChuck = (MetalChuckController) enemy;
                            if(!metalChuck.isDead()){
                                metalChuck.die();
                                rayGun.playModeSound();
                            }
                        }

                    }
                    if(enemy instanceof BombPeckerController){
                        if(rayGun.getMode() == RayGun.RayGunMode.BREEZING){
                            BombPeckerController bombPecker = (BombPeckerController) enemy;
                            if(!bombPecker.isDead()){
                                bombPecker.die();
                                rayGun.playModeSound();
                            }
                        }

                    }
                    if(enemy instanceof IceCrabController){

                        if(rayGun.getMode() == RayGun.RayGunMode.BLAZING){
                            IceCrabController iceCrab = (IceCrabController) enemy;
                            if(!iceCrab.isDead()){
                                iceCrab.die();
                                rayGun.playModeSound();
                            }
                        }
                    }
                    if(enemy instanceof TentaclesController){

                        if(rayGun.getMode() == RayGun.RayGunMode.ICE){
                            TentaclesController tentacles = (TentaclesController) enemy;
                            if(!tentacles.isDead()){
                                tentacles.die();
                                rayGun.playModeSound();
                            }
                        }
                    }
                }
            }
        }
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
        if(showBigBoss){
            boss.drawDebug(shapeRenderer);
        }
        if(showSmallBoss){
            smallBoss.drawDebug(shapeRenderer);
        }

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
        gunManager.dispose();

        if(boss != null) boss.dispose();
        if(rayGun != null) rayGun.dispose();
        if(timerFont != null) timerFont.dispose();

        waterFountain.dispose();
        if (currency != null) currency.dispose();
    }
}
