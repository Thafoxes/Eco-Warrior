package io.github.eco_warrior.controller.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Enemies;
import io.github.eco_warrior.enums.EnemyType;
import io.github.eco_warrior.sprite.Enemy.BigOctopusBoss;
import io.github.eco_warrior.sprite.Enemy.WaterOctopus;

public class WaterOctopusController extends EnemyController{


    private WaterOctopus boss;

    public WaterOctopusController(Vector2 position) {
        super(new WaterOctopus(position), EnemyType.BOSS);
        setState(Enemies.EnemyState.SPAWNING);
        this.boss = (WaterOctopus) super.enemy;

    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // Update the boss state
        switch(boss.getCurrentState()) {
            case SPAWNING:
                if(boss.isCurrentAnimationDone()) {
                    boss.setState(Enemies.EnemyState.IDLE);
                }
                break;
            case IDLE:
                break;
            default:
                System.err.println("Unhandled animation state: " + boss.getCurrentState());
                break;
        }
    }

    public boolean isCurrentAnimationDone() {
        return boss.isCurrentAnimationDone();
    }

    @Override
    public void draw(SpriteBatch batch){
        boss.draw(batch);
    }

    @Override
    public void dispose() {
        boss.dispose();
        super.dispose();
    }

    @Override
    public void drawDebug(ShapeRenderer shapeRenderer) {
        super.drawDebug(shapeRenderer);
    }


}
