package io.github.eco_warrior.controller.Trees;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.animation.FireBurningAnim;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.entity.BaseExplosion;
import io.github.eco_warrior.entity.BaseTreeHealth;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.sprite.gardening_equipments.Fertilizer;
import io.github.eco_warrior.sprite.gardening_equipments.Shovel;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;

public abstract class TreeController <T extends Trees> {

    protected T tree;
    protected final WateringCan wateringCan;
    protected final BaseTreeHealth treeHealth;
    protected BaseExplosion deadAnim;
    protected boolean isInteractionEnabled = true;
    protected final float interactionCooldown = 0.5f;
    protected float cooldownTimer = 0;
    protected int health = 4;
    protected boolean isDead = false;
    protected Vector2 animPosition;

    public TreeController(T tree, WateringCan wateringCan, BaseTreeHealth treeHealth) {
        this.tree = tree;
        this.wateringCan = wateringCan;
        this.treeHealth = treeHealth;
        setDeadAnimation();
    }

    private void setDeadAnimation() {
        float xPos = tree.getInitPosition().x + (tree.getSprite().getWidth() * tree.getScale() /2);
        float yPos = tree.getInitPosition().y + (tree.getSprite().getHeight() * tree.getScale()/2);
        animPosition = new Vector2(xPos, yPos);
        this.deadAnim = new FireBurningAnim(animPosition, 0.5f);
    }

    public void update(float delta){
        if (!isInteractionEnabled) {
            cooldownTimer += delta;
            if (cooldownTimer >= interactionCooldown) {
                isInteractionEnabled = true;
                cooldownTimer = 0;
            }
        }
        tree.update(delta);
        treeHealth.update(delta, health);

        if(isDead && !deadAnim.isFinished()) {
            deadAnim.update(delta);
        } else if (isDead) {
            isDead = false; // Reset isDead after the animation finishes
        }
    }

    public boolean handleSaplingPlanting(BaseSaplingController sapling){
        if(!isInteractionEnabled || !canPlantSapling(sapling)) {
            System.out.println("Cannot plant sapling: either interaction is disabled or sapling type does not match.");
            return false;
        }
        if (isInteractionEnabled && tree.getCollisionRect().overlaps(sapling.getCollisionRect())) {
            tree.plantSapling();
            isInteractionEnabled = false;
            return true;
        }
        return false;
    }

    protected boolean canPlantSapling(BaseSaplingController sapling) {
        return sapling != null
            && tree.getSaplingType() == sapling.getSaplingType()
            && sapling.getCollisionRect().overlaps(tree.getCollisionRect());
    }

    public void digHole(){
        tree.digHole();
    }

    /**
     * Handles the watering interaction with the tree.
     * If the watering can is filled and overlaps with the tree, it waters the tree.
     * Interaction is disabled after watering to prevent multiple interactions in quick succession.
     */
    public void handleWatering(){
        if (isInteractionEnabled && wateringCan.waterLevel == WateringCan.WateringCanState.FILLED) {
            if (tree.getCollisionRect().overlaps(wateringCan.getCollisionRect())) {
                tree.water();
                isInteractionEnabled = false;
            }
        }
    }

    public void takeDamage(int damage) {
        if (isInteractionEnabled) {
            health -= damage;
            if (health <= 0) {
                tree.die();
                deadAnim.reset(animPosition);
                isDead = true;
                health = 0;
            }
            isInteractionEnabled = false;
        }
    }

    public void reviveTree(){
        if (isInteractionEnabled && isDead) {
            tree.revivePlant();
            health = 4; // Reset health upon revival
            isInteractionEnabled = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if(isDead && !deadAnim.isFinished()) {
            deadAnim.draw(batch);
        }
        tree.draw(batch);
        treeHealth.draw(batch);

    }

    public boolean isInteractionEnabled() {
        return isInteractionEnabled;
    }

    public T getTree() {
        return tree;
    }

    public void dispose() {
        tree.dispose();
        deadAnim.dispose();
    }

    public void reset() {
        tree.reset();
        isInteractionEnabled = true;
        cooldownTimer = 0;
        health = 4;
        isDead = false;
        deadAnim.reset(tree.getPosition());
    }

    public void setMaturedStateDebug() {
        tree.MaturedStateDebug();
        isInteractionEnabled = true;
        cooldownTimer = 0;
        health = 4;
        isDead = false;
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        tree.drawDebug(shapeRenderer);
        treeHealth.drawDebug(shapeRenderer);

    }

    public void resetHealth() {
        health = 4;
        isDead = false;
    }

    public int getHealth() {
        return health;
    }

    public boolean isDead() {
        return isDead;
    }


}
