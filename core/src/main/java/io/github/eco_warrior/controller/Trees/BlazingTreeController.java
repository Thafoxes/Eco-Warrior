package io.github.eco_warrior.controller.Trees;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;
import io.github.eco_warrior.sprite.tree_variant.BlazingTree;

public class BlazingTreeController {
    private final BlazingTree blazingTree;
    private final WateringCan wateringCan;
    private boolean isInteractionEnabled = true;
    private final float interactionCooldown = 0.5f;
    private float cooldownTimer = 0;

    private int health = 4;

    public BlazingTreeController(BlazingTree blazingTree, WateringCan wateringCan) {
        this.blazingTree = blazingTree;
        this.wateringCan = wateringCan;
    }


    public void update(float delta) {
        // Update cooldown timer
        if (!isInteractionEnabled) {
            cooldownTimer += delta;
            if (cooldownTimer >= interactionCooldown) {
                isInteractionEnabled = true;
                cooldownTimer = 0;
            }
        }

        // Update tree animation
        blazingTree.update(delta);
    }

    public void handleSaplingPlanting(GameSprite sapling) {
        if (isInteractionEnabled && blazingTree.getCollisionRect().overlaps(sapling.getCollisionRect())) {
            blazingTree.plantSapling();
            isInteractionEnabled = false;
        }
    }

    public void digHole(){
        blazingTree.digHole();
    }


    public void handleWatering() {
        if (isInteractionEnabled && wateringCan.waterLevel == WateringCan.WateringCanState.FILLED.ordinal()) {
            if (blazingTree.getCollisionRect().overlaps(wateringCan.getCollisionRect())) {
                blazingTree.water();
                isInteractionEnabled = false;
            }
        }
    }

    public void handleDamage(int damage) {
        if (isInteractionEnabled) {
            health -= damage;
            if (health <= 0) {
                blazingTree.die();
            }
            isInteractionEnabled = false;
        }
    }


    public void draw(SpriteBatch batch) {
        blazingTree.draw(batch);
    }

    public boolean isInteractionEnabled() {
        return isInteractionEnabled;
    }

    public BlazingTree getBlazingTree() {
        return blazingTree;
    }

    public void dispose() {
        blazingTree.dispose();
    }
}
