package io.github.eco_warrior.controller.Trees;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.animation.FireBurningAnim;
import io.github.eco_warrior.controller.FertilizerController;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.entity.BaseExplosion;
import io.github.eco_warrior.entity.BaseTreeHealth;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.enums.TreeType;
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
    protected int wateringTime = 0;
    protected boolean isGrowing = false;
    protected TreeType treeType;

    public TreeController(T tree, WateringCan wateringCan, BaseTreeHealth treeHealth, TreeType treeType) {
        this.tree = tree;
        this.wateringCan = wateringCan;
        this.treeHealth = treeHealth;
        this.treeType = treeType;
        setDeadAnimation();
    }

    private void setDeadAnimation() {
        float xPos = tree.getInitPosition().x + (tree.getSprite().getWidth() * tree.getScale() /2);
        float yPos = tree.getInitPosition().y + (tree.getSprite().getHeight() * tree.getScale()/2);
        animPosition = new Vector2(xPos + 50f, yPos);
        this.deadAnim = new FireBurningAnim(animPosition, 0.1f);
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
        if(!isInteractionEnabled || !canPlantSapling(sapling) || isGrowing) {
            System.out.println("Tree Controller - Cannot plant sapling: either interaction is disabled or sapling type" +
                " does not match or current tree is growing.");
            return false;
        }
        if (isInteractionEnabled && tree.getCollisionRect().overlaps(sapling.getCollisionRect())) {
            isGrowing = true;
            tree.plantSapling();
            isInteractionEnabled = false;
            return true;
        }
        return false;
    }

    public boolean handleFertilizerUsing(FertilizerController fertilizerController) {
        if (!isInteractionEnabled || !canUseFertilizer(fertilizerController)) {
            return  false;
        }
        return tree.getCollisionRect().overlaps(fertilizerController.getCollisionRect());
    }


    protected boolean canPlantSapling(BaseSaplingController sapling) {
        return sapling != null
            && tree.getSaplingType() == sapling.getSaplingType()
            && sapling.getCollisionRect().overlaps(tree.getCollisionRect());
    }

    protected boolean canUseFertilizer(FertilizerController fertilizerController) {
        return fertilizerController != null
            && fertilizerController.getCollisionRect().overlaps(tree.getCollisionRect());
    }

    public void digHole(){
        tree.digHole();
    }

    public Trees.TreeStage getStage(){
        return tree.getStage();
    }

    /**
     * Handles the watering interaction with the tree.
     * If the watering can is filled and overlaps with the tree, it waters the tree.
     * Interaction is disabled after watering to prevent multiple interactions in quick succession.
     */
    public void handleWatering(){

        if (isInteractionEnabled && wateringCan.waterLevel == WateringCan.WateringCanState.FILLED) {
            wateringTime ++;
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

    }

    public BaseTreeHealth getTreeHealth() {
        return this.treeHealth;
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

    public Sprite getSprite() {
        return tree.getSprite();
    }

    public Rectangle getCollisionRect() {
        return tree.getCollisionRect();
    }

    public boolean isPlanted(){
        return tree.isPlanted();
    }

    public boolean isPlanting() {
        return tree.isPlanting();
    }

    public boolean isMaturedAliveTree() {
        return tree.isFullyMaturedAlive();
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        tree.drawDebug(shapeRenderer);
        treeHealth.drawDebug(shapeRenderer);

    }

    public void resetHealth() {
        tree.revivePlant();
        health = 4;
        isDead = false;
    }

    public TreeType getTreeType() {
        return treeType;
    }

    public int getHealth() {
        return health;
    }

    public boolean isDead() {
        return isDead;
    }


}
