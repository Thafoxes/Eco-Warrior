package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
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
import io.github.eco_warrior.controller.FontGenerator;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.BucketState;
import io.github.eco_warrior.enums.textEnum;
import io.github.eco_warrior.screen.ResultScreen;
import io.github.eco_warrior.sprite.CrackSprite;
import io.github.eco_warrior.sprite.Enemy.SpiderSprite;
import io.github.eco_warrior.sprite.UI.WaterWasteBarUI;
import io.github.eco_warrior.sprite.WaterDrop;
import io.github.eco_warrior.sprite.tools.*;

import java.util.*;

import static com.badlogic.gdx.Gdx.gl;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;
import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_WIDTH;

public class LevelThreeScreen implements Screen, SpiderSprite.CrackCreationCallback {


    public static final int MAX_SPAWN_RATE = 5;
    public static final int CRACK_FIX_SCORE_INCREMENT = 25;
    public static final int SPLASHED_SPIDER_SCORE = 5;
    private static final int MAX_CRACKS = 8; // Maximum number of cracks allowed at once
    public static final int WINNING_SCORE = 700;
    private final Main game;

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

    //sound effects
    private Sound cantRepairSound;
    private Sound explosionSound;

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



    //scoring section
    private int score = 0;
    private float levelTimerSec = 60f;
    private FontGenerator scoreFont;
    private FontGenerator timerFont;

    //water drop UI
    private WaterSystemManager waterSystem;
    // water meter
    private WaterWasteBarUI waterMeter;
    private boolean gameOverTriggered = false;

    //water bucket logics
    private WaterBucket waterBucket;
    private WaterResevior waterResevior;

    //Water capacity thresholds
    private int BuckethalfFullThreshold = 10;
    private int bucketMaxCapacity = BuckethalfFullThreshold * 2;

    //meter drop volume size
    private float dropVolume = 1f;



    //Losing image image face
    private Texture losingImage;
    private Texture winningImage;

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
        scoreFont = new FontGenerator(24, Color.WHITE, Color.BLACK);
        timerFont = new FontGenerator(24, Color.WHITE, Color.BLACK);

        //spider
        activeSpiders = new ArrayList<>();
        activeExplosions = new ArrayList<>();

        //crack
        crackSprites = new ArrayList<>();

        //losing Image
        losingImage = new Texture(Gdx.files.internal("Image/plumber_girl_defeated.png"));
        winningImage = new Texture(Gdx.files.internal("Image/plumbergirl_cheerful.png"));


        //setup camera to middle
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();

        waterDropManagerInitialize();
        loadMap();
        initializeTools();

        initializeSpawningArea();
        spiderSpawnInterval = MathUtils.random(2.0f, 3.0f);

        //sound
        cantRepairSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/wrong.mp3"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/underwater_explosion_sfx.mp3"));
        BGM = Gdx.audio.newMusic(Gdx.files.internal("Background_Music/Pipe.mp3"));
        playBGM();


    }

    private void playBGM() {
        BGM.setVolume(0.5f);
        BGM.play();
    }

    private void waterDropManagerInitialize() {
        float waterMeterScale = 2f;
        waterMeter = new WaterWasteBarUI(WINDOW_WIDTH * 3/4 - (128 * waterMeterScale), 100 , waterMeterScale);
        //water system UI
        waterSystem = new WaterSystemManager(waterMeter, dropVolume);

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

        waterBucket = new WaterBucket(new Vector2(spacing * 1 - toolWidth/2, startY), toolScale,
            BuckethalfFullThreshold,
            bucketMaxCapacity);
        tools.put("water_bucket", waterBucket);
        tools.put("water_spray", new WaterSpray(new Vector2(spacing * 2 - toolWidth/2, startY), toolScale));
        tools.put("pipe_wrench", new PipeWrench(new Vector2(spacing * 3 - toolWidth/2, startY), toolScale));
        tools.put("duct_tape", new DuctTape(new Vector2(spacing * 4 - toolWidth /2, startY), toolScale));

        // bottom left water dump funnel resevior
        waterResevior = new WaterResevior(new Vector2(toolWidth/2, 10f), 1.5f);
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
        checkScore();

    }

    private void checkScore() {
        if(score >= WINNING_SCORE){
            WinningResult();
        }
    }

    private void font(float delta) {
        levelTimerSec -= delta;
        if(levelTimerSec <= 0f){

            levelTimerSec = 0;
            //winning

            TimeOutResult();
        }
    }



    private void increaseCrackFixScore() {
        score += CRACK_FIX_SCORE_INCREMENT;
    }

    private void update(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

                    createCrack((Vector2) crackObj);
                }
                explosionIterator.remove();
            }
        }

        //water system update
        waterSystem.update(delta, crackSprites);
        waterMeter.update();

        if (waterSystem.isWaterMeterFull() && !gameOverTriggered) {
            MeterExplodedOver();
        }


        updateWaterInteractions(delta);

    }

    private void MeterExplodedOver() {
        StopMusic();
        explosionSound.play();
        LosingResult();

    }

    private void WinningResult() {
        StopMusic();
        game.setScreen(new ResultScreen(game, score, false,
            "Score hit!",
            winningImage)); // true indicates game over
    }

    private void StopMusic() {
        BGM.stop();
    }

    private void TimeOutResult() {
        StopMusic();
        game.setScreen(new ResultScreen(game, score, false, "Time out!")); // true indicates game over
    }

    private void LosingResult() {
        gameOverTriggered = true;
        Gdx.app.log("LevelThreeScreen", "Water meter is full! Game Over!");
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
        @Override
        public void run() {
            game.setScreen(new ResultScreen(game, score, true,
                "Too much water wasted!",
                losingImage
                )); // true indicates game over
        }
    }, 5); // 5 second delay
    }

    private void updateWaterInteractions(float delta) {


       List<WaterDrop> waterDrops = waterSystem.getActiveWaterDrops();

       Iterator<WaterDrop> waterDropIterator = waterDrops.iterator();
       while(waterDropIterator.hasNext()){
           WaterDrop drop = waterDropIterator.next();

           if(drop.getCollisionRect().overlaps(waterBucket.getCollisionRect())){
                if(waterBucket.catchWaterDrop()){
                    waterDropIterator.remove();
                }
           }
       }


        // Check if full bucket overlaps with reservoir during drag, when is not dragging check the condition
       if(draggingTool == waterBucket){
           int amount = waterBucket.getWaterDropCount();

            if(waterBucket.emptyIntoReservoir(waterResevior)){

                waterResevior.receiveWater(amount);
                score += amount;
                // Reset bucket state after emptying
                resetBucket();
//                // Play sound effect for emptying
                waterResevior.playSound();
            }
       }
    }

    private void spawnSpider() {
        if(activeSpiders.size() < MAX_SPAWN_RATE) {
            float x = spiderSpawnArea.x + MathUtils.random(spiderSpawnArea.width);
            float y = spiderSpawnArea.y + MathUtils.random(spiderSpawnArea.height);

            Vector2 position = new Vector2(x, y);
            // Create spider with this as the callback
            SpiderSprite spider = new SpiderSprite(position, this);
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

        if(draggingTool != null){
            // If it's the water bucket, don't set isReturning flag
            if(draggingTool.equals(tools.get("water_bucket"))) {
                isReturning = false;
            } else {
                isReturning = true;
            }

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
                       boolean spiderOverlapping = false;

                       for(SpiderSprite spider: activeSpiders){
                            if(spider.getCollisionRect().overlaps(draggingTool.getCollisionRect())){
                                cantRepairSound.play(0.8f);
                                 spiderOverlapping = true;
                                 break;
                            }
                       }

                       //if not overlapping with spider, check if can repair the crack
                       if (!spiderOverlapping) {
                           if(canRepairCrack(toolKey, crack.getCrackType())){
                               draggingTool.playSound();
                               crackIterator.remove();
                               increaseCrackFixScore();
                           }else{
                               // Wrong tool used
                               //sound please?
                           }
                       }

                   }
               }
           }

           // If we've released the bucket, make sure it updates its collision rectangle
           if(draggingTool.equals(tools.get("water_bucket"))) {
               draggingTool = null;
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
        waterMeter.drawWithLabel(batch, camera);
        waterResevior.drawWithWaterCount(batch, camera);

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
//        debugSprite();
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
//        debugSpawnArea();

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
        waterResevior.dispose();
        cantRepairSound.dispose();
        BGM.dispose();
        explosionSound.dispose();
        shapeRenderer.dispose();

    }

    @Override
    public void createCrack(Vector2 position) {
        // Only create a new crack if we haven't reached the maximum
        if (crackSprites.size() >= MAX_CRACKS) {
            return; // Skip crack creation if we've reached the limit
        }

        // Reuse your existing crack creation logic
        // Choose a random crack type (1, 2, or 3)
        int crackType = MathUtils.random(1, 3);
        try {
            // Create the crack sprite at the given position
            CrackSprite crack = new CrackSprite(crackType, position);

            // Optionally set a random rotation or scale for variety
            crack.setRotation(MathUtils.random(0f, 360f));
            float scale = MathUtils.random(0.8f, 1.5f);
            crack.setScale(scale);

            // Add the crack to our list
            crackSprites.add(crack);

            // Optional: Play a crack sound effect
            // crackSound.play();
        } catch (IllegalArgumentException e) {
            Gdx.app.error("LevelThreeScreen", "Could not create crack: " + e.getMessage());
        }
    }

}
