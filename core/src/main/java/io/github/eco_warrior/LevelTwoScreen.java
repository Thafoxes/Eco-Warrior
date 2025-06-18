package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.controller.Manager.ToolManager;
import io.github.eco_warrior.controller.Manager.TreeControllerManager;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.controller.Trees.*;
import io.github.eco_warrior.entity.GameSprite;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.enums.GardeningEnums;
import io.github.eco_warrior.sprite.*;
import io.github.eco_warrior.sprite.Enemy.Worm;
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

        worms = new Array<>();
        initializeTools();
        initializeTrees();


        currency = new Currency(new Vector2(20, WINDOW_HEIGHT - 60), 0.5f, camera);
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
//
//        wormPool = new Array<>(WORM_BUFFER_CAPACITY);
//        for (int i = 0; i < WORM_BUFFER_CAPACITY; i++) {
//            wormPool.add(new Worm(startWormPosition));
//        }
    }

    private void initializeSapling(float spacing, float toolScale) {
        BaseSaplingController ordinarySapling = new OrdinarySapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        BaseSaplingController blazingSapling = new BlazingSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        BaseSaplingController breezingSapling = new BreezingSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        BaseSaplingController iceSapling = new IceSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);
        BaseSaplingController voltaicSapling = new VoltaicSapling(new Vector2(spacing * 5 - manipulatorX, startY), toolScale);


        toolManager.addSaplingController(voltaicSapling);
        toolManager.addSaplingController(ordinarySapling);
        toolManager.addSaplingController(blazingSapling);
        toolManager.addSaplingController(breezingSapling);
        toolManager.addSaplingController(iceSapling);

    }

    private void initializeTrees() {
        float treeScale = 0.26f;

        TreeController<OrdinaryTree> ordinaryTreeController = new OrdinaryTreeController(
            new OrdinaryTree(new Vector2(763, 92), treeScale),
            wateringCan
        );
        TreeController<BlazingTree> blazingTreeController = new BlazingTreeController(
            new BlazingTree(new Vector2(1048, 256), treeScale),
                wateringCan
        );
        TreeController<BreezingTree> breezingTreeController = new BreezingTreeController(
            new BreezingTree(new Vector2(920, 183), treeScale),
            wateringCan
        );
        TreeController<IceTree> iceTreeController = new IceTreeController(
            new IceTree(new Vector2(1023, 25), treeScale),
            wateringCan
        );
        TreeController<VoltaicTree> voltaicTreeController = new VoltaicTreeController(
            new VoltaicTree(new Vector2(781, 299), treeScale),
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
//        spawnWorm(delta);
//        updateEnemyAnimationMovement();
//
//        for (TreeHealth treeHealth : treeHealths.values()) {
//            treeHealth.updateHealth();
//        }
    }


    private void updateTreeManager(float delta) {
        treeControllerManager.update(delta);
    }

    private void updateToolManager(float delta) {
        toolManager.update(delta);
    }


    private void draw(float delta) {
        camera.update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        stateTime = Gdx.graphics.getDeltaTime();

        batch.begin();
        backgroundSprite.draw(batch);
        toolManager.render(batch);
        treeControllerManager.draw(batch);

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

            if(!isDragging){
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
                // Check if the tool is a sapling and if it can be planted
                if(treeControllerManager.interactWithTrees(draggingTool)){
                    // If a sapling was successfully planted, handle the planting logic
                    System.out.println("Is planted");
                    toolManager.handleSaplingPlanting(draggingTool);
                }
                // Check if the tool is a watering can and if it can water a fountain
                toolManager.isWaterCansCollide(waterFountain);
            }
            isDragging = false;
            isReturning = true;
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
