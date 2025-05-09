package io.github.eco_warrior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.eco_warrior.controller.fontGenerator;
import io.github.eco_warrior.entity.ConveyorBelt;
import io.github.eco_warrior.entity.LevelMaker;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.textEnum;
import io.github.eco_warrior.sprite.*;

import java.util.*;

import static com.badlogic.gdx.Gdx.gl;
import static io.github.eco_warrior.constant.ConstantsVar.*;

/** First screen of the application. Displayed after the application is created. */
public class FirstLevelScreen extends LevelMaker implements Screen {

    private TiledMap map;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer maprenderer;
    private Viewport viewport;
    private Animation<TextureRegion> conveyorAnimation;

    private SpriteBatch batch;

    //conveyor
    private ConveyorBelt conveyorBelt;
    private float stateTime;
    private final int CONVEYOR_SCALE = 10;

    //uiBatch
    private fontGenerator scoreFont;
    private fontGenerator timerFont;
    private SpriteBatch uiBatch;

    //score
    private int score = 0;
    private float timerSeconds = 60f;
    private boolean timerEnded = false;

    //Sprites
//    WasteBin wasteBin;
    TrashPile trashPile;
    private List<gameSprite> recyclables = new ArrayList<>();
    private final Class<?>[] recyclableClasses = new Class[] {
        PlasticBottle.class,
        Newspaper.class,
        TinCans.class,
        TrashPile.class,
    };
    //recyclable spawn pos
    private float startX = WINDOW_WIDTH +  50f;
    private float startY = WINDOW_HEIGHT / 8;

    //spawn time
    private float spawnTimer = 0f;
    private float spawnInterval = 3f; //every 3 secs

    //input section
    private boolean isDragging = false;
    private Vector2 lastTouchPos;
    private Vector2 currentTouchPos;
    private boolean isReturning = false;
    private boolean hasbeenDroppedInBin = false;

    //all bins
    private Map<String, gameSprite> bins = new HashMap<>();

    //debug method
    private ShapeRenderer shapeRenderer;

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

        loadMap();

        loadingConveyorAnimation();

        initSprites();
        initAllBins();

    }

    private void initAllBins() {
        bins.put("paper", new BlueBin(new Vector2(WINDOW_WIDTH / 5f , WINDOW_HEIGHT / 2)));
        bins.put("can" , new CanBin(new Vector2(WINDOW_WIDTH * 2 / 5f , WINDOW_HEIGHT / 2)));
        bins.put("plastic" , new PlasticBin(new Vector2(WINDOW_WIDTH * 3 / 5f , WINDOW_HEIGHT / 2)));
        bins.put("general waste", new WasteBin(new Vector2(WINDOW_WIDTH * 4 / 5f , WINDOW_HEIGHT / 2)));
    }

    private void initSprites() {
//        wasteBin = new WasteBin(new Vector2(WINDOW_WIDTH / 1.5f , WINDOW_HEIGHT / 2));

        trashPile = new TrashPile(new Vector2(startX , startY ));
        recyclables.add(new TrashPile(new Vector2(startX , startY )));
        recyclables.add(new Newspaper(new Vector2(startX , startY )));
        recyclables.add(new TinCans(new Vector2(startX , startY )));
        recyclables.add(new PlasticBottle(new Vector2(startX , startY )));

    }

    private void loadingConveyorAnimation() {
        conveyorBelt = new ConveyorBelt("atlas/conveyor/conveyor.atlas",
            CONVEYOR_SCALE,
            viewport.getWorldHeight()/10f + 50f, //some calculation only
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

        //if touch key to test my score up
        gameSprite draggingItem = null;

        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            currentTouchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(currentTouchPos);
            if(Gdx.input.justTouched()){
                for(gameSprite bin: bins.values()){
                    bin.isPressed(new Vector2(currentTouchPos.x, currentTouchPos.y));
                }
            }

            if(trashPile.getCollisionRect().contains(currentTouchPos.x, currentTouchPos.y)){
                isDragging = true;
                lastTouchPos.set(currentTouchPos.x, currentTouchPos.y);

            }
        }
        if(isDragging && Gdx.input.isButtonPressed(Input.Buttons.LEFT)){

            currentTouchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(currentTouchPos);

            float dx = currentTouchPos.x - trashPile.getMidX();
            float dy = currentTouchPos.y - trashPile.getMidY();

            trashPile.getSprite().setPosition(dx, dy);
            //Sync the collision rectangle with the new sprite position
            trashPile.getCollisionRect().setPosition(
                trashPile.getSprite().getX(),
                trashPile.getSprite().getY()
            );



            lastTouchPos.set(currentTouchPos.x, currentTouchPos.y);

        }else if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            isDragging = false;
            isReturning = true;
        }else{
            isDragging = false;
            isReturning = true;
        }
        if(isReturning){
            returnOriginalPosition();
        }

        for(gameSprite bin: bins.values()){
            if(!trashPile.getCollisionRect().overlaps(bin.getCollisionRect())) {
                hasbeenDroppedInBin = false; //reset if drag failed
            }else{
                if(!hasbeenDroppedInBin && !isDragging ){
                    bin.playCorrectSound();
                    hasbeenDroppedInBin = true;
                    //do something to remove the trash

                }
            }
        }

    }

    private void returnOriginalPosition() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        //if the sprite is not overlapping the bin, return back to original position
        float dy = startY;
        Vector2 current = new Vector2(trashPile.getSprite().getX(), trashPile.getSprite().getY());
        Vector2 target = new Vector2(current.x, dy);

        Vector2 learped = current.lerp(target, 5f * deltaTime);

        trashPile.getSprite().setPosition(learped.x, learped.y);
        trashPile.getCollisionRect().setPosition(learped.x, learped.y);

        if(current.dst(target) < 2f){
            trashPile.getSprite().setPosition(target.x, target.y);
            trashPile.getCollisionRect().setPosition(target.x, target.y);
            isReturning = false;
        }
    }

    private void countdownTimer() {
        if (!timerEnded) {
            timerSeconds -= Gdx.graphics.getDeltaTime();
            if (timerSeconds <= 0) {
                //do something here
                isFinished();
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
        for(gameSprite bin: bins.values()){
            bin.update(stateTime);
        }
//        wasteBin.update(stateTime);
//        trashPile.update(stateTime);
        conveyorBelt.update(stateTime);
        Iterator<gameSprite> iterator = recyclables.iterator();


        batch.begin();



        for(gameSprite bin: bins.values()){
            bin.draw(batch);
        }
//        wasteBin.draw(batch);

        conveyorBelt.draw(batch);

        //recyclables here
        while(iterator.hasNext()){
            gameSprite item = iterator.next();
            item.update(stateTime);
            item.draw(batch);
            if((item.isOffScreen())){
                iterator.remove();
            }
        }


        batch.end();

        //debug mode start
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);

        // draw for each recyclable debug
        for (gameSprite item : recyclables) {
            item.drawDebug(shapeRenderer);
        }

        //check if the overlapping occurs
        for(gameSprite bin: bins.values()){
            bin.drawDebug(shapeRenderer);
        }
//        wasteBin.drawDebug(shapeRenderer);

        shapeRenderer.end();

        scoreFont.fontDraw(uiBatch, "Score: " + score , camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT - 10f), textEnum.LEFT, textEnum.TOP);
        timerFont.fontDraw(uiBatch, displayTimer(timerSeconds) , camera, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT - 10f), textEnum.RIGHT , textEnum.TOP);
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
//        wasteBin.dispose();
        trashPile.dispose();
        //disposing bins
        for(gameSprite bin: bins.values()){}
    }

    @Override
    protected void winningDisplay() {

    }

    @Override
    protected void losingDisplay() {

    }
}
