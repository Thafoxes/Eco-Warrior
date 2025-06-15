package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.entity.gameSprite;
import java.util.Random;
import io.github.eco_warrior.sprite.*;
import io.github.eco_warrior.sprite.Enemy.Worm;
import io.github.eco_warrior.sprite.gardening_equipments.*;
import io.github.eco_warrior.sprite.gardening_equipments.sapling_variant.*;
import io.github.eco_warrior.sprite.tree_healths.*;
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

    //tree healths
    private Map<treesHealthsType, TreeHealth> treeHealths;

    //entities declaration
    private WateringCan wateringCan;
    private WaterFountain waterFountain;
    public static Shovel shovel;

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

    //tree healths
    private OrdinaryTreeHealth ordinaryTreeHealth;
    private BlazingTreeHealth blazingTreeHealth;
    private BreezingTreeHealth breezingTreeHealth;
    private IceTreeHealth iceTreeHealth;
    private VoltaicTreeHealth voltaicTreeHealth;

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
    private Array<Worm> worms;
    private Array<Worm> wormPool;
    private static final float wormStartX = WINDOW_WIDTH + 50f;
    private static final float wormStartY = 100f; //default value, will be set based on random path
    private Vector2 startWormPosition = new Vector2(wormStartX, wormStartY);
    private float wormSpawnTimer;
    private float stateTime;
    private static final int WORM_BUFFER_CAPACITY = 10;

    //enemy path
    private final Random rand = new Random();
    public enum WormPath {
        PATH_1(1),
        PATH_2(2),
        PATH_3(3),
        PATH_4(4),
        PATH_5(5),
        ;

        private final int number;

        WormPath(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public Vector2 getWormStartPosition() {
            switch (this) {
                case PATH_1: return new Vector2(wormStartX, 25);
                case PATH_2: return new Vector2(wormStartX, 100);
                case PATH_3: return new Vector2(wormStartX, 175);
                case PATH_4: return new Vector2(wormStartX, 250);
                case PATH_5: return new Vector2(wormStartX, 300);
                default: return new Vector2(0, 0); // Default case, should not happen
            }
        }
    }

    //sound effects
    private final Sound shovelSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/pan.mp3"));

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
        treeHealths = new HashMap<>();

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

        ordinaryTreeHealth = new OrdinaryTreeHealth(ordinaryTree);
        blazingTreeHealth = new BlazingTreeHealth(blazingTree);
        breezingTreeHealth = new BreezingTreeHealth(breezingTree);
        iceTreeHealth = new IceTreeHealth(iceTree);
        voltaicTreeHealth = new VoltaicTreeHealth(voltaicTree);

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

        treeHealths.put(treesHealthsType.ORDINARY_TREE_HEALTH, ordinaryTreeHealth);
        treeHealths.put(treesHealthsType.BLAZING_TREE_HEALTH, blazingTreeHealth);
        treeHealths.put(treesHealthsType.BREEZING_TREE_HEALTH, breezingTreeHealth);
        treeHealths.put(treesHealthsType.ICE_TREE_HEALTH, iceTreeHealth);
        treeHealths.put(treesHealthsType.VOLTAIC_TREE_HEALTH, voltaicTreeHealth);

        wormPool = new Array<>(WORM_BUFFER_CAPACITY);
        for (int i = 0; i < WORM_BUFFER_CAPACITY; i++) {
            wormPool.add(new Worm(startWormPosition));
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

    private enum treesHealthsType {
        ORDINARY_TREE_HEALTH,
        BLAZING_TREE_HEALTH,
        BREEZING_TREE_HEALTH,
        ICE_TREE_HEALTH,
        VOLTAIC_TREE_HEALTH
    }

    @Override
    public void render(float delta) {
        input();
        draw();
        updateWateringCan();
        updateTrees();
        spawnWorm(delta);
        updateEnemyAnimationMovement();

        for (TreeHealth treeHealth : treeHealths.values()) {
            treeHealth.updateHealth();
        }
    }

    private void spawnWorm(float delta) {
        wormSpawnTimer += delta; // Adds the current delta to the timer

        if (wormSpawnTimer > 3f) { // Check if it has been more than 3 second\
            if (wormPool.size == 0) { // If the pool is empty, do not spawn a new worm
                wormSpawnTimer = 0; // Reset the timer
            } else {
                Worm worm;
                WormPath path = WormPath.values()[rand.nextInt(WormPath.values().length)];

                startWormPosition = path.getWormStartPosition();
                long startTime = System.nanoTime();

                if(wormPool.size > 0) {
                    worm = wormPool.pop();
                    worm.getSprite().setPosition(startWormPosition.x, startWormPosition.y);
                    worm.getCollisionRect().setPosition(startWormPosition); // Reset the worm with the new position and scale
                }else {
                    worm = new Worm(startWormPosition);
                }

                worms.add(worm);

                // Assign tree target based on path

                wormSpawnTimer = 0; // Reset the timer
                long endTime = System.nanoTime();
                System.out.println("Worm creation time: " + (endTime - startTime) / 1000000.0 + " ms");
                System.out.println("Worms in pool: " + wormPool.size + ", Worms in game: " + worms.size);
            }
        }
    }

    private void draw() {
        camera.update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        stateTime = Gdx.graphics.getDeltaTime();

        batch.begin();
        backgroundSprite.draw(batch);


        drawWorm();
        drawTrees();
        drawTreeHealths();
        drawToolFiltering();
        drawTools();


        batch.end();

//        debugSprite();
    }

    private void drawTrees() {
        for (Trees tree : trees.values()) {
            tree.draw(batch);
        } //edit
    }

    private void drawTreeHealths() {
        for (TreeHealth treeHealth : treeHealths.values()) {
            treeHealth.draw(batch);
        }
    }

    private void drawTools() {
        //draw tools that are not saplings to be unlocked
        for (gameSprite tool : tools.values()) {

            if(!(tool instanceof BaseSapling) || tool == tools.get(gameSpriteType.ORDINARY_SAPLING)) {
                tool.draw(batch);
            }
        }
    }

    private void drawWorm() {
        Iterator<Worm> iterator = worms.iterator();
        while(iterator.hasNext()) {
            Worm worm = iterator.next();
            worm.update(stateTime);
            worm.draw(batch);

//            if(worm.isDead) {
//                worm.reset();
//                wormPool.add(worm); //push back into pull
//                iterator.remove();
//                break;
//            }
        }
    }

    private void drawToolFiltering() {
        //remove ordinary sapling upon planting
        if ((ordinaryTree.treeLevel == OrdinaryTree.TreeStage.HOLE.ordinal())
            && ordinaryTree.getCollisionRect().overlaps(ordinarySapling.getCollisionRect())) {
            tools.remove(gameSpriteType.ORDINARY_SAPLING);
        }

        //draw voltaic sapling when the ordinary tree reaches adult phase
        if(!isVoltaicSaplingUsed) {
            if (ordinaryTree.treeLevel == OrdinaryTree.TreeStage.MATURE_TREE.ordinal()
                || ordinaryTree.treeLevel == OrdinaryTree.TreeStage.DEAD_MATURE_TREE.ordinal()) {
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
            if (voltaicTree.treeLevel >= VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
                && voltaicTree.treeLevel <= VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_4.ordinal()
                || voltaicTree.treeLevel == VoltaicTree.TreeStage.DEAD_MATURE_TREE.ordinal()) {
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
            if (breezingTree.treeLevel >= BreezingTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
            && breezingTree.treeLevel <= BreezingTree.TreeStage.ANIMATED_MATURE_TREE_3.ordinal()
            || breezingTree.treeLevel == BreezingTree.TreeStage.DEAD_MATURE_TREE.ordinal()) {
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
            if (iceTree.treeLevel >= IceTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
                && iceTree.treeLevel <= IceTree.TreeStage.ANIMATED_MATURE_TREE_4.ordinal()
                || iceTree.treeLevel == IceTree.TreeStage.DEAD_MATURE_TREE.ordinal()) {
                tools.get(gameSpriteType.BLAZING_SAPLING).draw(batch);
            }
        }

        //remove blazing sapling upon planting
        if ((blazingTree.treeLevel == BlazingTree.TreeStage.HOLE.ordinal())
            && blazingTree.getCollisionRect().overlaps(blazingSapling.getCollisionRect())) {
            tools.remove(gameSpriteType.BLAZING_SAPLING);
            isBlazingSaplingUsed = true; //set to true when blazing sapling is used
        }
    }

    private void input(){
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            currentTouchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(currentTouchPos);

            //check if pressed the tools
            if (Gdx.input.justTouched()) {
                for (Map.Entry<gameSpriteType, gameSprite> entry : tools.entrySet()){
                    gameSpriteType type = entry.getKey();
                    gameSprite tool = entry.getValue();

                    if(!tool.getCollisionRect().contains(currentTouchPos)){
                        continue; //skip if the tool is not touched
                    }
                    //if is touched the tools
                    if (type == gameSpriteType.VOLTAIC_SAPLING) {
                        //voltaic sapling can only be dragged when ordinary tree is mature
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && (ordinaryTree.treeLevel == OrdinaryTree.TreeStage.MATURE_TREE.ordinal()
                            || ordinaryTree.treeLevel == OrdinaryTree.TreeStage.DEAD_MATURE_TREE.ordinal())) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    }
                    else if (type == gameSpriteType.BREEZING_SAPLING) {
                        //breezing sapling can only be dragged when voltaic tree is mature
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && (voltaicTree.treeLevel >= VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
                            && voltaicTree.treeLevel <= VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_4.ordinal()
                            || voltaicTree.treeLevel == VoltaicTree.TreeStage.DEAD_MATURE_TREE.ordinal())) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    }
                    else if (type == gameSpriteType.ICE_SAPLING) {
                        //ice sapling can only be dragged when breezing tree is mature
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && (breezingTree.treeLevel >= BreezingTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
                            && breezingTree.treeLevel <= BreezingTree.TreeStage.ANIMATED_MATURE_TREE_3.ordinal()
                            || breezingTree.treeLevel == BreezingTree.TreeStage.DEAD_MATURE_TREE.ordinal())) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    }
                    else if (type == gameSpriteType.BLAZING_SAPLING) {
                        //blazing sapling can only be dragged when ice tree is mature
                        if (tool.getCollisionRect().contains(currentTouchPos)
                            && (iceTree.treeLevel >= IceTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
                            && iceTree.treeLevel <= IceTree.TreeStage.ANIMATED_MATURE_TREE_4.ordinal()
                            || iceTree.treeLevel == IceTree.TreeStage.DEAD_MATURE_TREE.ordinal())) {
                            draggingTool = tool;
                            isDragging = true;
                            lastTouchPos.set(currentTouchPos);
                            break;
                        }
                    }
                    else {
                        //other tools can be dragged anytime
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

        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
//            if (draggingTool == tools.get(gameSpriteType.SHOVEL)) {
//                for (Worm worm : worms) {
//                    if (worm.getCollisionRect().overlaps(shovel.getCollisionRect()) && !worm.isDeathTransition) {
//                        worm.speed = 0;
//                        worm.isDeathTransition = true;
//
//                        if (worm.attackTask != null) {
//                            worm.attackTask.cancel();
//                            worm.attackTask = null;
//                        }
//
//                        shovelSound.play(.5f);
//                        worm.startDeathAnimation();
//                    }
//                }
//            }
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

        if(draggingTool != null) {
            isReturning = true;

            if (draggingTool.equals(tools.get(gameSpriteType.SHOVEL))) {

                if (ordinaryTree.treeLevel == OrdinaryTree.TreeStage.FLAG.ordinal()
                && ordinaryTree.getCollisionRect().overlaps(shovel.getCollisionRect())) {

                    ordinaryTree.treeLevel = OrdinaryTree.TreeStage.HOLE.ordinal();
                    ordinaryTree.diggingSound();

                    ordinaryTree.setFrame(ordinaryTree.treeLevel);
                }
                else if (blazingTree.treeLevel == BlazingTree.TreeStage.FLAG.ordinal()
                    && blazingTree.getCollisionRect().overlaps(shovel.getCollisionRect())
                    && iceTree.isMatureTree) {

                    blazingTree.treeLevel = BlazingTree.TreeStage.HOLE.ordinal();
                    blazingTree.diggingSound();

                    blazingTree.setFrame(blazingTree.treeLevel);
                }
                else if (breezingTree.treeLevel == BreezingTree.TreeStage.FLAG.ordinal()
                    && breezingTree.getCollisionRect().overlaps(shovel.getCollisionRect())
                    && voltaicTree.isMatureTree) {

                    breezingTree.treeLevel = BreezingTree.TreeStage.HOLE.ordinal();
                    breezingTree.diggingSound();

                    breezingTree.setFrame(breezingTree.treeLevel);
                }
                else if (iceTree.treeLevel == IceTree.TreeStage.FLAG.ordinal()
                    && iceTree.getCollisionRect().overlaps(shovel.getCollisionRect())
                    && breezingTree.isMatureTree) {

                    iceTree.treeLevel = IceTree.TreeStage.HOLE.ordinal();
                    iceTree.diggingSound();

                    iceTree.setFrame(iceTree.treeLevel);
                }
                else if (voltaicTree.treeLevel == VoltaicTree.TreeStage.FLAG.ordinal()
                    && voltaicTree.getCollisionRect().overlaps(shovel.getCollisionRect())
                    && ordinaryTree.isMatureTree) {

                    voltaicTree.treeLevel = VoltaicTree.TreeStage.HOLE.ordinal();
                    voltaicTree.diggingSound();

                    voltaicTree.setFrame(voltaicTree.treeLevel);
                }

//                for (Worm worm : worms) {
//                    if (worm.getCollisionRect().overlaps(shovel.getCollisionRect())) {
//                        isShovelReleased = true; //set to true when shovel is used
//                    }
//                }
            }
        }
    }

    private void updateWateringCan() {
        wateringCan.updateWateringCan(waterFountain);
    }

    private void updateTrees() {
        ordinaryTree.updateTree(ordinarySapling, wateringCan);
        blazingTree.updateTree(blazingSapling, wateringCan);
        breezingTree.updateTree(breezingSapling, wateringCan);
        iceTree.updateTree(iceSapling, wateringCan);
        voltaicTree.updateTree(voltaicSapling, wateringCan);
    }

    private void updateEnemyAnimationMovement() {
        for (Worm worm : worms) {
//            worm.updateWormAnimationMovement();
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

        for(gameSprite tool: tools.values()){
            tool.drawDebug(shapeRenderer);
        }

        for(Trees tree: trees.values()){
            tree.drawDebug(shapeRenderer);
        }

        for (Worm worm : worms) {
            worm.drawDebug(shapeRenderer);
        }

//        for (TreeHealth treeHealth : treeHealths.values()) {
//            treeHealth.drawDebug(shapeRenderer);
//        }

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

        for (Trees tree : trees.values()) {
            tree.dispose();
        }

        for(gameSprite worm: worms){
            worm.dispose();
        }

        for (TreeHealth treeHealth : treeHealths.values()) {
            treeHealth.dispose();
        }

        wateringCan.dispose();
        waterFountain.dispose();
        shovel.dispose();

        liquids.get("water_fountain_hitbox").dispose();
    }
}
