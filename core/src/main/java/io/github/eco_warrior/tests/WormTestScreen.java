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
import io.github.eco_warrior.controller.Enemy.EnemyController;
import io.github.eco_warrior.controller.Enemy.WormController;
import io.github.eco_warrior.controller.Manager.EnemyManager;

public class WormTestScreen implements Screen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private EnemyManager enemyManager;
    private float deltaTime;

    //testing collision area
    private Array<Rectangle> collisionAreas;
    private float spawnTimer = 0f;
    private float spawnInterval = 5f; // Time in seconds between enemy spawns



    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        enemyManager = new EnemyManager();

        collisionAreas = new Array<Rectangle>();
        // Add some target areas for collision testing
        collisionAreas.add(new Rectangle(0, 200, 500, 500));
        collisionAreas.add(new Rectangle(300, 400, 500, 500));
        collisionAreas.add(new Rectangle(1000, 300, 500, 500));

        initializeEnemies();
    }

    private void initializeEnemies() {

        float x = (float) (Math.random() * (Gdx.graphics.getWidth() - 100));
        float y = (float) (Math.random() * (Gdx.graphics.getHeight() - 100));
        WormController wormController = new WormController(new Vector2(x, y));
        enemyManager.addEnemy(wormController);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        deltaTime += delta;
        spawnTimer += delta;

        //udpates
        enemyManager.update(delta);
        if(spawnTimer >= spawnInterval){
            spawnRandomEnemy();
            spawnTimer = 0; // Reset the timer after spawning an enemy
        }

        checkCollisions();

        //draw
        batch.begin();
        enemyManager.draw(batch);
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
                if(enemy instanceof WormController) {
                    if (enemyBounds.overlaps(area)) {
                        // Handle collision logic here
                        // For example, you can change the enemy's state or position
                        enemy.die(); // Example action on collision
                        break;
                    }
                }
            }
        }


    }

    private void spawnRandomEnemy() {
        float x = (float) (Math.random() * Gdx.graphics.getWidth());
        float y = (float) (Math.random() * Gdx.graphics.getHeight());
        enemyManager.spawnEnemy(new Vector2(x, y));
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
        // Test controls
        for(EnemyController enemy : enemyManager.getEnemies()) {
            if (enemy instanceof WormController) {
                // Debug controls for the worm
                if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                    ((WormController) enemy).setRightDirection(false);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                    ((WormController) enemy).setRightDirection(true);
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                    ((WormController) enemy).move();
                }
                if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    ((WormController) enemy).attack();
                }
            }
        }
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
