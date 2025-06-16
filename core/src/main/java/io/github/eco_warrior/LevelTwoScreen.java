package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
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
import io.github.eco_warrior.entity.BaseTreeHealth;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.entity.GameSprite;
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

//    //tools
//    private Map<gameSpriteType, GameSprite> tools = new HashMap<>();
//    private float manipulatorX;
//    private float startY;
//
//    //water fountain
//    private Map<String, GameSprite> liquids;
//
//    //trees
//    private Map<treesType, Trees> trees;
//
//    //tree healths
//    private Map<treesHealthsType, BaseTreeHealth> treeHealths;

    //entities declaration
    private WateringCan wateringCan;
    private WaterFountain waterFountain;
    public static Shovel shovel;

    private OrdinaryTree ordinaryTree;
    private BlazingTree blazingTree;
    private BreezingTree breezingTree;
    private IceTree iceTree;
    private VoltaicTree voltaicTree;


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
    private GameSprite draggingTool;

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
//        liquids = new HashMap<>();
//        trees = new HashMap<>();
//        treeHealths = new HashMap<>();

        initializeEntities();
    }

    private void initializeEntities() {
        int toolCount = 5;
        float spacing = WINDOW_WIDTH / (toolCount + 4);
        float toolScale = 0.35f;
        float lakeScale = 1.3f;
        float treeScale = 0.26f;

        float toolWidth = 200f;
//        startY = WINDOW_HEIGHT/30f;
//        manipulatorX = toolWidth/2f;
//
//        wateringCan = new WateringCan(new Vector2(spacing * 2 - manipulatorX, startY), toolScale);
//        waterFountain = new WaterFountain(new Vector2(1, 180), lakeScale);
//        shovel = new Shovel(new Vector2(spacing * 3 - manipulatorX, startY), toolScale);
//
//        ordinaryTree = new OrdinaryTree(new Vector2(763, 92), treeScale);
//        blazingTree = new BlazingTree(new Vector2(1048, 256), treeScale);
//        breezingTree = new BreezingTree(new Vector2(920, 183), treeScale);
//        iceTree = new IceTree(new Vector2(1023, 25), treeScale);
//        voltaicTree = new VoltaicTree(new Vector2(781, 299), treeScale);
//
//        ordinarySapling = new OrdinarySapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
//        blazingSapling = new BlazingSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
//        breezingSapling = new BreezingSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
//        iceSapling = new IceSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
//        voltaicSapling = new VoltaicSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
//
//        ordinaryTreeHealth = new OrdinaryTreeHealth(ordinaryTree);
//        blazingTreeHealth = new BlazingTreeHealth(blazingTree);
//        breezingTreeHealth = new BreezingTreeHealth(breezingTree);
//        iceTreeHealth = new IceTreeHealth(iceTree);
//        voltaicTreeHealth = new VoltaicTreeHealth(voltaicTree);
//
//        liquids.put("water_fountain_hitbox", waterFountain);
//
//        wormPool = new Array<>(WORM_BUFFER_CAPACITY);
//        for (int i = 0; i < WORM_BUFFER_CAPACITY; i++) {
//            wormPool.add(new Worm(startWormPosition));
//        }
    }


    @Override
    public void render(float delta) {
//        input();
//        draw();
//        updateWateringCan();
//        updateTrees();
//        spawnWorm(delta);
//        updateEnemyAnimationMovement();
//
//        for (TreeHealth treeHealth : treeHealths.values()) {
//            treeHealth.updateHealth();
//        }
    }


    private void draw() {
        camera.update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        stateTime = Gdx.graphics.getDeltaTime();

        batch.begin();
        backgroundSprite.draw(batch);

        batch.end();
//        debugSprite();
    }




    private void returnOriginalPosition() {
        float deltaTime = Gdx.graphics.getDeltaTime();

//        if(draggingTool != null){
//            float dy = startY;
//            Vector2 current = new Vector2(draggingTool.getSprite().getX(), draggingTool.getSprite().getY());
//            Vector2 target = draggingTool.getInitPosition();
//
//            Vector2 learped = current.lerp(target, deltaTime * 10f);
//
//            draggingTool.getSprite().setPosition(learped.x, learped.y);
//            draggingTool.getCollisionRect().setPosition(
//                draggingTool.getSprite().getX(),
//                draggingTool.getSprite().getY());
//
//            if(current.dst(target) < 2f){
//                draggingTool.getSprite().setPosition(target.x, target.y);
//                draggingTool.getCollisionRect().setPosition(
//                    draggingTool.getSprite().getX(),
//                    draggingTool.getSprite().getY());
//                isReturning = false;
//                draggingTool = null;
//            }
//
//        }
    }
//
//    private void onMouseRelease() {
//        isDragging = false;
//
//        if(draggingTool != null) {
//            isReturning = true;
//
//            if (draggingTool.equals(tools.get(gameSpriteType.SHOVEL))) {
//
//                if (ordinaryTree.treeLevel == OrdinaryTree.TreeStage.FLAG.ordinal()
//                && ordinaryTree.getCollisionRect().overlaps(shovel.getCollisionRect())) {
//
//                    ordinaryTree.treeLevel = OrdinaryTree.TreeStage.HOLE.ordinal();
//                    ordinaryTree.diggingSound();
//
//                    ordinaryTree.setFrame(ordinaryTree.treeLevel);
//                }
////                else if (blazingTree.treeLevel == BlazingTree.TreeStage.FLAG.ordinal()
////                    && blazingTree.getCollisionRect().overlaps(shovel.getCollisionRect())
////                    && iceTree.isMatureTree) {
////
////                    blazingTree.treeLevel = BlazingTree.TreeStage.HOLE.ordinal();
////                    blazingTree.diggingSound();
////
////                    blazingTree.setFrame(blazingTree.treeLevel);
////                }
//                else if (breezingTree.treeLevel == BreezingTree.TreeStage.FLAG.ordinal()
//                    && breezingTree.getCollisionRect().overlaps(shovel.getCollisionRect())
//                    && voltaicTree.isMatureTree) {
//
//                    breezingTree.treeLevel = BreezingTree.TreeStage.HOLE.ordinal();
//                    breezingTree.diggingSound();
//
//                    breezingTree.setFrame(breezingTree.treeLevel);
//                }
//                else if (iceTree.treeLevel == IceTree.TreeStage.FLAG.ordinal()
//                    && iceTree.getCollisionRect().overlaps(shovel.getCollisionRect())
//                    && breezingTree.isMatureTree) {
//
//                    iceTree.treeLevel = IceTree.TreeStage.HOLE.ordinal();
//                    iceTree.diggingSound();
//
//                    iceTree.setFrame(iceTree.treeLevel);
//                }
//                else if (voltaicTree.treeLevel == VoltaicTree.TreeStage.FLAG.ordinal()
//                    && voltaicTree.getCollisionRect().overlaps(shovel.getCollisionRect())
//                    && ordinaryTree.isMatureTree) {
//
//                    voltaicTree.treeLevel = VoltaicTree.TreeStage.HOLE.ordinal();
//                    voltaicTree.diggingSound();
//
//                    voltaicTree.setFrame(voltaicTree.treeLevel);
//                }
//
////                for (Worm worm : worms) {
////                    if (worm.getCollisionRect().overlaps(shovel.getCollisionRect())) {
////                        isShovelReleased = true; //set to true when shovel is used
////                    }
////                }
//            }
//        }
//    }
//
//    private void updateWateringCan() {
//        wateringCan.updateWateringCan(waterFountain);
//    }
//
//    private void updateTrees() {
//        ordinaryTree.updateTree(ordinarySapling, wateringCan);
////        blazingTree.updateTree(blazingSapling, wateringCan);
//        breezingTree.updateTree(breezingSapling, wateringCan);
//        iceTree.updateTree(iceSapling, wateringCan);
//        voltaicTree.updateTree(voltaicSapling, wateringCan);
//    }

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

//        for(GameSprite tool: tools.values()){
//            tool.drawDebug(shapeRenderer);
//        }
//
//        for(Trees tree: trees.values()){
//            tree.drawDebug(shapeRenderer);
//        }
//
//        for (Worm worm : worms) {
//            worm.drawDebug(shapeRenderer);
//        }

//        for (TreeHealth treeHealth : treeHealths.values()) {
//            treeHealth.drawDebug(shapeRenderer);
//        }

//        liquids.get("water_fountain_hitbox").drawDebug(shapeRenderer);

//        debugSpawnArea();

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        uiBatch.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        shapeRenderer.dispose();

//        for (GameSprite tool : tools.values()) {
//            tool.dispose();
//        }
//
//        for (Trees tree : trees.values()) {
//            tree.dispose();
//        }
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
//        waterFountain.dispose();
//        shovel.dispose();
//
//        liquids.get("water_fountain_hitbox").dispose();
    }
}
