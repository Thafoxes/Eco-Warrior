package io.github.eco_warrior.controller.Manager;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.eco_warrior.controller.Enemy.EnemyController;
import io.github.eco_warrior.controller.Enemy.WormController;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.enums.EnemyType;

import java.util.ArrayList;


public class EnemyManager {

    private ArrayList<EnemyController> enemies;

    public EnemyManager(){
        enemies = new ArrayList<>();
    }

    public void addEnemy(EnemyController enemy) {
        enemies.add(enemy);
    }

    public ArrayList<EnemyController> getEnemies() {
        return enemies;
    }


    public void update(float delta) {

        Array<EnemyController> enemiesToRemove = new Array<>();

        for(EnemyController enemy : enemies) {
            enemy.update(delta);
            if(enemy.isDead()){
                enemiesToRemove.add(enemy);
            }
        }

        for(EnemyController enemy : enemiesToRemove) {
            enemies.remove(enemy);
        }
    }

    public void draw(SpriteBatch batch) {
        for (EnemyController enemy : enemies) {
            Sprite sprite = enemy.getSprite();
            if (sprite != null) {
                sprite.draw(batch);
            }
        }
    }

    public void spawnEnemy(Vector2 vector2) {
        WormController enemyController = new WormController(vector2);
        addEnemy(enemyController);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        for (EnemyController enemy : enemies) {
            Sprite sprite = enemy.getSprite();
            shapeRenderer.setColor(1, 0, 0, 1); // Set color for debug
            shapeRenderer.rect(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        }
    }

    public void dispose() {
        for (EnemyController enemy : enemies) {
           enemy.dispose();
        }
        enemies.clear();
    }
}
