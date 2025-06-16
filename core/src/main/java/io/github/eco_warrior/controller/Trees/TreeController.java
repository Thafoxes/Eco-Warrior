package io.github.eco_warrior.controller.Trees;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;

public abstract class TreeController <T extends Trees> {

    protected T tree;
    protected final WateringCan wateringCan;
    protected boolean isInteractionEnabled = true;
    protected final float interactionCooldown = 0.5f;
    protected float cooldownTimer = 0;
    protected int health = 4;
    protected boolean isDead = false;

    public TreeController(T tree, WateringCan wateringCan) {
        this.tree = tree;
        this.wateringCan = wateringCan;
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
    }

    public void handleSaplingPlanting(GameSprite sapling){
        if (isInteractionEnabled && tree.getCollisionRect().overlaps(sapling.getCollisionRect())) {
            tree.plantSapling();
            isInteractionEnabled = false;
        }
    }

    public void digHole(){
        tree.digHole();
    }

    public void handleWatering(){
        if (isInteractionEnabled && wateringCan.waterLevel == WateringCan.WateringCanState.FILLED.ordinal()) {
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
        tree.draw(batch);
    }

    public boolean isInteractionEnabled() {
        return isInteractionEnabled;
    }

    public T getTree() {
        return tree;
    }

    public void dispose() {
        tree.dispose();
    }

    public void reset() {
        tree.reset();
        isInteractionEnabled = true;
        cooldownTimer = 0;
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
