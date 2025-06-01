package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.animation.WaterExplosion;
import io.github.eco_warrior.controller.WaterSystemManager;
import io.github.eco_warrior.controller.fontGenerator;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.BucketState;
import io.github.eco_warrior.enums.textEnum;
import io.github.eco_warrior.screen.ResultScreen;
import io.github.eco_warrior.sprite.CrackSprite;
import io.github.eco_warrior.sprite.Enemy.SpiderSprite;
import io.github.eco_warrior.sprite.UI.WaterWasteBarUI;
import io.github.eco_warrior.sprite.tools.DuctTape;
import io.github.eco_warrior.sprite.tools.PipeWrench;
import io.github.eco_warrior.sprite.tools.WaterBucket;
import io.github.eco_warrior.sprite.tools.WaterSpray;

import java.util.*;

import static com.badlogic.gdx.Gdx.gl;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;

public class LevelThreeScreen implements Screen {


    public static final int MAX_SPAWN_RATE = 5;
    public static final int CRACK_FIX_SCORE_INCREMENT = 25;
    public static final int SPLASHED_SPIDER_SCORE = 5;
    private Main game;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private SpriteBatch uiBatch;

    //map
    private TiledMap map;
    private OrthogonalTiledMapRenderer maprenderer;


    //delta time
    private float stateTime;

    //tools
    private Map<String, gameSprite> tools = new HashMap<>();
    private BucketState bucketState;
    private float startY;

    //input selection
    private Vector2 currentTouchPos;
    private Vector2 lastTouchPos;
    private boolean isDragging = false;
    private boolean isReturning = false;
    private gameSprite draggingTool;


    //music
    private Music BGM;

    //debug
    //debug method
    private ShapeRenderer shapeRenderer;

    //spider spawn
    private float spiderSpawnTimer = 0f;
    private float spiderSpawnInterval = 2f;
    private ArrayList<SpiderSprite> activeSpiders;
    private List<WaterExplosion> activeExplosions;
    private Rectangle spiderSpawnArea;


    //crack instance
    private List<CrackSprite> crackSprites;
    private static final float CRACK_SPAWN_CHANCE = 0.75f;

    //scoring section
    private int score = 0;
    private float levelTimerSec = 60f;
    private fontGenerator scoreFont;
    private fontGenerator timerFont;

    //water drop UI
    private WaterSystemManager waterSystem;
    // water meter
    private WaterWasteBarUI waterMeter;
    private boolean gameOverTriggered = false;

    public LevelThreeScreen(Main main) {
        this.game = main;

    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();

        lastTouchPos = new Vector2();
        currentTouchPos = new Vector2();

        //initialize font
        scoreFont = new fontGenerator(24, Color.WHITE, Color.BLACK);
        timerFont = new fontGenerator(24, Color.WHITE, Color.BLACK);

        //spider
        activeSpiders = new ArrayList<>();
        activeExplosions = new ArrayList<>();

        //crack
        crackSprites = new ArrayList<>();


        //setup camera to middle
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();

        waterDropManagerInitialize();
        loadMap();
        initializeTools();

        initializeSpawningArea();
        spiderSpawnInterval = MathUtils.random(2.0f, 3.0f);



    }

    private void waterDropManagerInitialize() {
        float waterMeterScale = 1.5f;
        waterMeter = new WaterWasteBarUI(WINDOW_WIDTH * 3/4 - (128 * waterMeterScale), 40 , waterMeterScale);
        //water system UI
        waterSystem = new WaterSystemManager(waterMeter);
    }

    private void initializeSpawningArea() {
        // Define spawn zone as percentages of screen size
        float leftMargin = viewport.getWorldWidth() * 0.1f;
        float rightMargin = viewport.getWorldWidth() * 0.7f;
        float topMargin = viewport.getWorldHeight() * 0.5f;
        float bottomOffset = viewport.getWorldHeight() * 0.35f;
        spiderSpawnArea = new Rectangle(leftMargin, bottomOffset, rightMargin, topMargin);
    }



    private void initializeTools() {
        int toolCount = 4;
        float spacing = WINDOW_WIDTH / (toolCount + 1);
        float toolScale = 3f;

        float toolWidth = new DuctTape(new Vector2(0, 0), toolScale).getSprite().getWidth();
        startY = WINDOW_HEIGHT/7f;
        tools.put("water_bucket", new WaterBucket(new Vector2(spacing - toolWidth/2, startY), toolScale));
        tools.put("water_spray", new WaterSpray(new Vector2(spacing * 2 - toolWidth/2, startY), toolScale));
        tools.put("pipe_wrench", new PipeWrench(new Vector2(spacing * 3 - toolWidth/2, startY), toolScale));
        tools.put("duct_tape", new DuctTape(new Vector2(spacing * 4 - toolWidth /2, startY), toolScale));

    }

    private void fillBucket(){

        BucketState[] states = BucketState.values();
        int nextOrdinal = bucketState.ordinal() + 1;
        gameSprite waterBucket = tools.get("water_bucket");
        if(waterBucket != null){
            throw new RuntimeException();
        }
        if(nextOrdinal < states.length){
            waterBucket.nextFrame();
            bucketState = states[nextOrdinal];
        }
    }

    private void resetBucket(){
        gameSprite waterBucket = tools.get("water_bucket");
        if(waterBucket != null){
            waterBucket.resetFrame();
            bucketState = bucketState.EMPTY;
        }
    }
    private void loadMap(){
        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
        parameters.generateMipMaps = false;
        map = new TmxMapLoader().load("maps/water_map.tmx", parameters);

        maprenderer = new OrthogonalTiledMapRenderer(map, 4f);
    }

    @Override
    public void render(float delta) {
        update(delta);
        input();
        draw();
        font(delta);
    }

    private void font(float delta) {
        levelTimerSec -= delta;
        if(levelTimerSec <= 0f){
            levelTimerSec = 0;
        }
    }

    private void increaseCrackFixScore() {
        score += CRACK_FIX_SCORE_INCREMENT;
    }

    private void update(float delta) {
        spiderSpawnTimer += delta;

        if(spiderSpawnTimer >= spiderSpawnInterval){
            spawnSpider();
            spiderSpawnTimer = 0f;
            spiderSpawnInterval = MathUtils.random(2.0f, 3.0f);
        }

        //update spiders
        Iterator<SpiderSprite> spiderIterator = activeSpiders.iterator();
        while(spiderIterator.hasNext()){
            SpiderSprite spider = spiderIterator.next();
            spider.update(delta);

            if(spider.isDead()){
                spiderIterator.remove();
            }
        }

        //update explosions
        Iterator<WaterExplosion> explosionIterator = activeExplosions.iterator();
        while(explosionIterator.hasNext()){
            WaterExplosion explosion = explosionIterator.next();
            explosion.update(delta);

            if(explosion.isFinished()){
                Object crackObj = explosion.getUserObject();
                if(crackObj instanceof Vector2){
                    System.out.println("Creating crack at: " + crackObj);
                    createCrack((Vector2) crackObj);
                }
                explosionIterator.remove();
            }
        }

        //water system update
        waterSystem.update(delta, crackSprites);
        waterMeter.update();

        if (waterSystem.isWaterMeterFull() && !gameOverTriggered) {
            gameOverTriggered = true;
            Gdx.app.log("LevelThreeScreen", "Water meter is full! Game Over!");
                com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                @Override
                public void run() {
                    game.setScreen(new ResultScreen(game, score, true)); // true indicates game over
                }
            }, 1); // 1 second delay
        }

    }

    private void spawnSpider() {
        if(activeSpiders.size() < MAX_SPAWN_RATE) {
            float x = spiderSpawnArea.x + MathUtils.random(spiderSpawnArea.width);
            float y = spiderSpawnArea.y + MathUtils.random(spiderSpawnArea.height);

            SpiderSprite spider = new SpiderSprite(new Vector2(x, y), 2f);
            activeSpiders.add(spider);
        }
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
        if(draggingTool != null){

           if(draggingTool.equals(tools.get("water_spray"))){

               for(SpiderSprite spider: activeSpiders){
                   if(!spider.isDead() && spider.getCollisionRect().overlaps(draggingTool.getCollisionRect())){
                        if(spider.kill()){
                            createExplosion(spider);
                            score += SPLASHED_SPIDER_SCORE;
                        }
                   }
               }

           }

           String toolKey = getToolKeyForTool(draggingTool);
           if(toolKey != null){
               Iterator<CrackSprite> crackIterator = crackSprites.iterator();
               while(crackIterator.hasNext()){
                   CrackSprite crack = crackIterator.next();



                   if(crack.isVisible() && crack.getCollisionBox().overlaps(draggingTool.getCollisionRect())){
                     if(canRepairCrack(toolKey, crack.getCrackType())){
                         draggingTool.playSound();
                         crackIterator.remove();
                         increaseCrackFixScore();
                     }else{
                         // Wrong tool used
                         System.out.println("Cannot fix this crack with " + toolKey);
                         //sound please?
                     }

                   }
               }
           }
        }


    }


    private String formatTime(float timeInSeconds){
        int minutes = (int) (timeInSeconds / 60);
        int seconds = (int) (timeInSeconds % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
    private boolean canRepairCrack(String toolKey, int crackType) {
        switch (toolKey) {
            case "duct_tape":
                return crackType == 1; // duct_tape fixes crack1
            case "pipe_wrench":
                return crackType == 2 || crackType == 3; // pipe_wrench fixes crack2 and crack3
            default:
                return false;
        }
    }

    private String getToolKeyForTool(gameSprite draggingTool) {
        for (Map.Entry<String, gameSprite> entry : tools.entrySet()) {
            if (entry.getValue().equals(draggingTool)) {
                return entry.getKey();
            }
        }
        return null; // Tool not found
    }

    private void createExplosion(SpiderSprite spider) {

        Vector2 position = new Vector2(
            spider.getCollisionRect().x + spider.getCollisionRect().width / 2,
            spider.getCollisionRect().y + spider.getCollisionRect().height / 2
        );

        WaterExplosion explosion = new WaterExplosion(position, 0.5f);
        activeExplosions.add(explosion);

        explosion.setUserObject(position);
    }


    private void draw() {
        camera.update();
        gl.glClear(GL32.GL_COLOR_BUFFER_BIT);

        maprenderer.setView(camera);
        maprenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw cracks first (they should be at the bottom layer)
        for (CrackSprite crack : crackSprites) {
            crack.draw(batch);
        }

        //water drop bottom of the crack
        waterSystem.draw(batch);
        waterMeter.draw(batch);

        //display tools
        for (gameSprite tool : tools.values()) {
            tool.draw(batch);
        }

        //draw spiders
        for(SpiderSprite spider: activeSpiders){
            spider.draw(batch);
        }

        //draw explosions
        for(WaterExplosion explosion: activeExplosions){
            explosion.draw(batch);
        }

        batch.end();


        drawGUI();


        //debugging draw green line
        debugSprite();
    }

    private void drawGUI() {

        String scoreText = "Score: " + score;
        scoreFont.fontDraw(uiBatch, scoreText, camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT - 10f), textEnum.LEFT, textEnum.TOP);
        String timerText = "Time: " + formatTime(levelTimerSec);
        timerFont.fontDraw(uiBatch, timerText, camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT - 10f), textEnum.RIGHT, textEnum.TOP);

    }

    private void debugSprite() {
        //debug mode start
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);

        for(gameSprite tool: tools.values()){
            tool.drawDebug(shapeRenderer);
        }
        debugSpawnArea();

        shapeRenderer.end();
    }

    private void debugSpawnArea() {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(
            spiderSpawnArea.x,
            spiderSpawnArea.y,
            spiderSpawnArea.width,
            spiderSpawnArea.height
        );

        // Draw crack bounds
        shapeRenderer.setColor(Color.CYAN);
        for (CrackSprite crack : crackSprites) {
            if (crack.isVisible()) {
                shapeRenderer.rect(
                    crack.getX(),
                    crack.getY(),
                    crack.getOriginX(),
                    crack.getOriginY(),
                    crack.getWidth(),
                    crack.getHeight(),
                    crack.getScaleX(),
                    crack.getScaleY(),
                    crack.getRotation()
                );
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

    @Override
    public void dispose() {
        uiBatch.dispose();
        batch.dispose();
        maprenderer.dispose();
        map.dispose();
        for (gameSprite tool : tools.values()) {
            tool.dispose();
        }
        for(SpiderSprite spiders: activeSpiders){
            spiders.dispose();
        }
        for(WaterExplosion explosions: activeExplosions){
            explosions.dispose();
        }
        for(CrackSprite crack: crackSprites){
            crack.dispose();
        }

        waterSystem.dispose();
        scoreFont.dispose();
        timerFont.dispose();
        waterMeter.dispose();
    }

    private void createCrack(Vector2 position){
        // Determine if we should create a crack (75% chance)
        if (MathUtils.random() > CRACK_SPAWN_CHANCE) {
            return; // No crack this time (25% chance)
        }

        // Choose a random crack type (1, 2, or 3)
        int crackType = MathUtils.random(1, 3);

        try {
            // Create the crack sprite at the given position
            CrackSprite crack = new CrackSprite(crackType, position);

            // Optionally set a random rotation or scale for variety
            crack.setRotation(MathUtils.random(0f, 360f));
            float scale = MathUtils.random(1f, 2f);
            crack.setScale(scale);

            // Add the crack to our list
            crackSprites.add(crack);
        } catch (IllegalArgumentException e) {
            // Handle case where crack region isn't found in atlas
            Gdx.app.error("LevelThreeScreen", "Could not create crack: " + e.getMessage());
        }
    }
}
