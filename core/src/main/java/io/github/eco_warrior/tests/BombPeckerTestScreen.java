package io.github.eco_warrior.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.eco_warrior.controller.Enemy.BombPeckerController;
import io.github.eco_warrior.controller.Enemy.EnemyController;
import io.github.eco_warrior.controller.Manager.EnemyManager;

import java.util.Iterator;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;

public class BombPeckerTestScreen implements Screen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private EnemyManager enemyManager;
    private float deltaTime;

    //testing collision area
    private Array<Rectangle> collisionAreas;



    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        enemyManager = new EnemyManager();

        collisionAreas = new Array<Rectangle>();
//        // Add some target areas for collision testing
        collisionAreas.add(new Rectangle(0, 200, 100, 50));
        collisionAreas.add(new Rectangle(500, 200, 100, 50));
        collisionAreas.add(new Rectangle(1000, 200, 100, 50));

//        initializeEnemies();
    }

    private void initializeEnemies() {

        float x = (float) (Math.random() * (Gdx.graphics.getWidth() - 100));
        float y = (float) (Math.random() * (Gdx.graphics.getHeight() - 100));
        BombPeckerController enemy = new BombPeckerController(new Vector2(x, WINDOW_HEIGHT));
        enemyManager.addEnemy(enemy);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        deltaTime += delta;

        //updates
        enemyManager.update(delta);

        checkCollisions();

        //draw
        batch.begin();
        enemyManager.draw(batch);
        Iterator <EnemyController> iterator = enemyManager.getEnemies().iterator();
        while(iterator.hasNext()) {
            EnemyController enemy = iterator.next();
            if(enemy.isDead()){
                iterator.remove();
                System.out.println("Enemy removed: " + enemy.getClass().getSimpleName());
            }
        }

        batch.end();


        //Inputs
        HandleInput();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        drawDebug();
        shapeRenderer.end();
    }

    private void checkCollisions() {

        for (EnemyController enemy : enemyManager.getEnemies()) {
            Rectangle enemyBounds = enemy.getSprite().getBoundingRectangle();
            for (Rectangle area : collisionAreas) {
                if(enemy instanceof BombPeckerController) {
                    if (enemyBounds.overlaps(area)) {
                        enemy.attack(); // Example action on collision
                        break;
                    }
                }
            }
        }

    }

    private void drawDebug() {
       enemyManager.drawDebug(shapeRenderer);

        // Draw collision areas
        shapeRenderer.setColor(1, 0, 0, 1);
        for(Rectangle area : collisionAreas) {
            shapeRenderer.rect(area.x, area.y, area.width, area.height);
        }
    }

    private void HandleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            System.out.println("Space key pressed, spawning BombPecker");
            // Spawn a BombPecker when space is pressed
            spawnBombPecker();
        }
    }

    private void spawnBombPecker() {

        // Set target Y where the bomb will explode (near one of the collision areas)
        Rectangle targetArea = collisionAreas.get((int)(Math.random() * collisionAreas.size));
        BombPeckerController bombPeckerController = new BombPeckerController(new Vector2(targetArea.x, targetArea.y - targetArea.height));
        bombPeckerController.setTargetPosition(targetArea.getPosition(new Vector2()));

        enemyManager.addEnemy(bombPeckerController);
    }

    @Override
    public void dispose() {
        batch.dispose();
        enemyManager.dispose();
    }

    // Other Screen methods remain empty
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
