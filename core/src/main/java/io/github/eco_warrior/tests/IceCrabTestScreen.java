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
import io.github.eco_warrior.controller.Enemy.IceCrabController;
import io.github.eco_warrior.controller.Manager.EnemyManager;

import java.util.Iterator;

import static io.github.eco_warrior.constant.ConstantsVar.WINDOW_HEIGHT;

public class IceCrabTestScreen implements Screen {
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
        collisionAreas.add(new Rectangle(200, 400, 100, 50));
        collisionAreas.add(new Rectangle(500, 400, 100, 50));
        collisionAreas.add(new Rectangle(1000, 400, 100, 50));

//        initializeEnemies();
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
            if (enemy instanceof IceCrabController) {
                IceCrabController iceCrab = (IceCrabController) enemy;
                if (iceCrab.isDead()) {
                    System.out.println("Enemy removed: " + iceCrab.getClass().getSimpleName());
                    iterator.remove();
                }
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
                if(enemy instanceof IceCrabController) {
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
            System.out.println("Space key pressed, spawning enemy");
            spawnIceCrab();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            for (EnemyController enemy : enemyManager.getEnemies()) {
                if(enemy instanceof IceCrabController) {
                    enemy.attack(); // Trigger attack on IceCrab
                }
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            for (EnemyController enemy : enemyManager.getEnemies()) {
                if(enemy instanceof IceCrabController) {
                    enemy.die(); // Trigger attack on IceCrab
                }
            }
        }
    }

    private void spawnIceCrab() {
        // Get a random collision area to spawn the IceCrab next to
        Rectangle targetArea = collisionAreas.get((int)(Math.random() * collisionAreas.size));

        // Position the IceCrab to the left of the selected rectangle
        // Offset it by a small amount so it's visibly separated
        float spawnX = targetArea.x + targetArea.width;  // 50 pixels to the left
        float spawnY = targetArea.y;       // Same Y level

        Vector2 position = new Vector2(spawnX, spawnY);
        IceCrabController iceCrabController =
            new IceCrabController(position);

        enemyManager.addEnemy(iceCrabController);
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
