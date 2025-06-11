package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.controller.BinController;
import io.github.eco_warrior.controller.fontGenerator;
import io.github.eco_warrior.entity.ConveyorBelt;
import io.github.eco_warrior.entity.LevelMaker;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.textEnum;
import io.github.eco_warrior.screen.ResultScreen;
import io.github.eco_warrior.sprite.Bins.*;
import io.github.eco_warrior.sprite.Recyables.*;
import io.github.eco_warrior.sprite.UI.Hearts;

import java.util.*;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.gl;
import static io.github.eco_warrior.constant.ConstantsVar.*;

/** First screen of the application. Displayed after the application is created. */
public class FirstLevelScreen extends LevelMaker implements Screen {

    private Main game;

    public FirstLevelScreen(Main game) {
        this.game = game;
    }
    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer maprenderer;
    private Viewport viewport;

    private SpriteBatch batch;

    //conveyor
    private ConveyorBelt conveyorBelt;
    private float stateTime;
    private final float CONVEYOR_SCALE = 0.5f;

    //uiBatch
    private fontGenerator scoreFont;
    private fontGenerator timerFont;
    private SpriteBatch uiBatch;

    //score
    private int score = 0;
    private float timerSeconds = 30f;
    private boolean timerEnded = false;

    //Hearts
    private Hearts playerHearts;
    private static final int MAX_HEARTS = 5;

    //Sprites
    private final List<gameSprite> recyclables = new ArrayList<>();
    private final Class<? extends gameSprite>[] recyclableClasses = new Class[] {
        PlasticBottle.class,
        Newspaper.class,
        TinCans.class,
        GlassBottle.class,
    };
    private Recyclables draggingItem = null;

    //recyclable spawn pos
    private float startX = WINDOW_WIDTH +  50f;
    private float startY = WINDOW_HEIGHT / 8;

    //spawn time
    private float spawnTimer = 0f;
    private float spawnInterval = 1.5f; //every ?? secs

    //input section
    private boolean isDragging = false;
    private Vector2 lastTouchPos;
    private Vector2 currentTouchPos;
    private boolean isReturning = false;
    private static int winningScore = 20;

    //all bins
   private BinController binController;
    //debug method
    private ShapeRenderer shapeRenderer;

    private Music backgroundMusic;

    @Override
    public void show() {
        //init setting

        camera = new OrthographicCamera();
        viewport = new FitViewport(WINDOW_WIDTH, WINDOW_HEIGHT, camera);
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();


        //setup camera to middle
        camera.position.set(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, 0);
        camera.update();


        scoreFont = new fontGenerator();
        timerFont = new fontGenerator();

        lastTouchPos = new Vector2();
        currentTouchPos = new Vector2();

        shapeRenderer = new ShapeRenderer();

        backgroundMusic = audio.newMusic(Gdx.files.internal("Background_Music/Recycle.mp3"));
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        loadHearts();
        loadMap();

        loadingConveyorAnimation();

        initAllBins();

    }

    private void loadHearts() {
        playerHearts = new Hearts(
            new Vector2(WINDOW_WIDTH / 2 - 60, WINDOW_HEIGHT - 60), // Position in top left
            MAX_HEARTS,  // Start with 5 hearts
            1.5f,       // Scale
            10f,         // Spacing between hearts
            camera
        );
    }

    private void initAllBins() {
        int totalBins = 4;
        float spacing = WINDOW_WIDTH / (totalBins + 1);

        float binWidth = new PaperBin(new Vector2(0,0)).getMidX();
        float yPos = WINDOW_HEIGHT / 2 - 30f;


        binController = new BinController();


        // Create bins for both background and foreground layers
        BinBase paperBin = new PaperBin(new Vector2(spacing - binWidth, yPos));
        BinBase canBin = new CanBin(new Vector2(spacing * 2 - binWidth, yPos));
        BinBase plasticBin = new PlasticBin(new Vector2(spacing * 3 - binWidth, yPos));
        BinBase glassBin = new GlassBin(new Vector2(spacing * 4 - binWidth, yPos));

        // Add bins to background layer
        binController.addBin(paperBin);
        binController.addBin(canBin);
        binController.addBin(plasticBin);
        binController.addBin(glassBin);

    }

    private void loadingConveyorAnimation() {
        float conveyorYPos = -100f ;
        conveyorBelt = new ConveyorBelt(
            CONVEYOR_SCALE,
            conveyorYPos, //some calculation only
            viewport
        );

        stateTime = 0f;
    }

    private void loadMap() {
        //load the map
        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
        parameters.generateMipMaps = false;
        map = new TmxMapLoader().load("maps/recycle_center_map.tmx", parameters);
        //create the renderer with 1 unit = 1 tile
        maprenderer = new OrthogonalTiledMapRenderer(map, 2f);
    }

    @Override
    public void render(float delta) {

        timerCount(delta);
        draw();
        countdownTimer();
        input();
        controller(delta);


    }

    private void controller(float delta) {
        playerHearts.update(delta);
        binController.update(delta);

        if(score >= winningScore || timerEnded) {
            winningScreen();

        }
    }

    private void winningScreen() {
        //show wins
        conveyorBelt.stopAnimation();
        backgroundMusic.stop();
        game.setScreen(new ResultScreen(game, score, false,
            "You have successfully recycled all the items!",
            new Texture(Gdx.files.internal("Image/recycle_manager_happy_v2.png"))));
    }

    private void losingScreen() {
        //show wins
        conveyorBelt.stopAnimation();
        backgroundMusic.stop();
        game.setScreen(new ResultScreen(game, score, true,
            "You misplace too many wrong recyclables!",
            new Texture(Gdx.files.internal("Image/recycle_manager_looks_sad.png"))));
    }


    private void timerCount(float delta) {
        spawnTimer += delta;
        if(spawnTimer > spawnInterval) {
            spawnTimer = 0f;
            spawnRecyclable();
        }
    }

    private void spawnRecyclable() {
        int index = new Random().nextInt(recyclableClasses.length);

        Class<?> selectedClass = recyclableClasses[index];

        gameSprite newItem = null;

        //create constructor for selected class
        try{
            newItem = (gameSprite) selectedClass.getConstructor(Vector2.class)
                .newInstance(new Vector2(startX, startY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(newItem != null) {
            recyclables.add(newItem);
        }

    }


    private void input() {

        //init press
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            currentTouchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(currentTouchPos);

            //check if press sprite
            if(Gdx.input.justTouched()){
                for(gameSprite item : recyclables){
                    if(item.getCollisionRect().contains(currentTouchPos)){
                        draggingItem = (Recyclables) item;
                        isDragging = true;
                        lastTouchPos.set(currentTouchPos);
                        break;
                    }
                }

                // Check for raccoon hits
                // Award points for hitting raccoons
                if(binController.checkRacoonHit(currentTouchPos)){
                    score += 1;
                }
                //just play sound
                for(BinBase bin: binController.getForegroundBins()){
                    bin.isPressed(new Vector2(currentTouchPos.x, currentTouchPos.y));
                }
            }

        }

        if(isDragging && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && draggingItem != null){

            currentTouchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(currentTouchPos);

            float dx = currentTouchPos.x - draggingItem.getMidX();
            float dy = currentTouchPos.y - draggingItem.getMidY();

            draggingItem.getSprite().setPosition(dx, dy);
            //Sync the collision rectangle with the new sprite position
            draggingItem.getCollisionRect().setPosition(
                draggingItem.getSprite().getX(),
                draggingItem.getSprite().getY()
            );


            lastTouchPos.set(currentTouchPos);

        }else if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            if(draggingItem != null){
                try{
                    onMouseRelease();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }else if(draggingItem != null){
            isDragging = false;
            isReturning = true;
        }

        //return draggingItem back to the conveyor
        if(isReturning){
            returnOriginalPosition();
        }



    }

    private void onMouseRelease() {
        isDragging = false;
        isReturning = true;
        if(draggingItem != null){
            //if the sprite hits the bin
            for(BinBase bin: binController.getForegroundBins()){
                if(draggingItem.getCollisionRect().overlaps(bin.getCollisionRect())){
                    //check if the draggingItem is place into the right bin
//                    System.out.println(draggingItem.getCategoryPile());
                    if(bin.isCorrectCategory(draggingItem.getCategoryPile())){

                        playCorrectAction(bin);

                    }else{
                        playWrongAction(bin);
                    }

                    recyclables.remove(draggingItem);
                    draggingItem = null;
                    break;
                }
            }
        }
    }

    private void playWrongAction(BinBase bin) {
        bin.playWrongSound();
        if(score > 0) score--;

        // Lose a heart and check if game over
        playerHearts.loseHeartWithEffect(Gdx.graphics.getDeltaTime());
        boolean gameOver = playerHearts.getCurrentHearts() <= 0;
        if (gameOver) {
            losingScreen();
        }
    }

    private void playCorrectAction(BinBase bin) {
        bin.playCorrectSound();
        score++;
    }

    private void returnOriginalPosition() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if(draggingItem != null){
            //if the sprite is not overlapping the bin, return back to original position
            float dy = startY;
            Vector2 current = new Vector2(draggingItem.getSprite().getX(), draggingItem.getSprite().getY());
            Vector2 target = new Vector2(current.x, dy);

            Vector2 learped = current.lerp(target, 5f * deltaTime);

            draggingItem.getSprite().setPosition(learped.x, learped.y);
            draggingItem.getCollisionRect().setPosition(learped.x, learped.y);

            if(current.dst(target) < 2f){
                draggingItem.getSprite().setPosition(target.x, target.y);
                draggingItem.getCollisionRect().setPosition(target.x, target.y);
                isReturning = false;
            }
        }


    }

    private void countdownTimer() {
        if (!timerEnded) {
            timerSeconds -= Gdx.graphics.getDeltaTime();
            if (timerSeconds <= 0) {
                //do something here
                timerEnded = true;
            }
        }
    }


    private void draw() {
        camera.update();
        gl.glClear(GL32.GL_COLOR_BUFFER_BIT);


        maprenderer.setView(camera);
        maprenderer.render();

        stateTime = Gdx.graphics.getDeltaTime();


        batch.setProjectionMatrix(camera.combined);

        conveyorBelt.update(stateTime);

        batch.begin();

        playerHearts.draw(batch);
        binController.draw(batch);
        conveyorBelt.draw(batch);

        //draw recyclables loop
        for(gameSprite item: recyclables){
            item.update(stateTime);
            item.draw(batch);
            if(item.isOffScreen()) {
                recyclables.remove(item);
                break;
            }
        }


        batch.end();
        binController.drawLabels(batch, camera);
        //debugging draw green line
        debugSprite();


        scoreFont.fontDraw(uiBatch, "Score: " + score , camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT - 10f), textEnum.LEFT, textEnum.TOP);
        timerFont.fontDraw(uiBatch, displayTimer(timerSeconds) , camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT - 10f), textEnum.RIGHT , textEnum.TOP);
    }

    private void debugSprite() {
        //debug mode start
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);

        // draw for each recyclable debug
        for (gameSprite item : recyclables) {
            item.drawDebug(shapeRenderer);
        }

        shapeRenderer.end();
    }


    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {

    }


    @Override
    public void dispose() {
        // Destroy screen's assets here.
        map.dispose();
        maprenderer.dispose();
        batch.dispose();
        conveyorBelt.dispose();
        scoreFont.dispose();
        timerFont.dispose();
        playerHearts.dispose();
        //disposing bins
        binController.dispose();
        for(gameSprite item: recyclables){
            item.dispose();
        }
    }

    @Override
    protected void winningDisplay() {

    }

    @Override
    protected void losingDisplay() {

    }
}
