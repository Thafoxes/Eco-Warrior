package io.github.eco_warrior.controller.Trees;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.enums.SaplingType;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;
import io.github.eco_warrior.sprite.tree_healths.BlazingTreeHealth;
import io.github.eco_warrior.sprite.tree_variant.BlazingTree;

public class BlazingTreeController extends TreeController<BlazingTree> {
    private boolean isInteractionEnabled = true;
    private final float interactionCooldown = 0.5f;
    private float cooldownTimer = 0;

    private int health = 4;

    public BlazingTreeController(BlazingTree blazingTree, WateringCan wateringCan) {
        super(blazingTree, wateringCan , new BlazingTreeHealth(blazingTree));
    }

    @Override
    protected boolean canPlantSapling(BaseSaplingController sapling) {
        return super.canPlantSapling(sapling) &&
            sapling.getSaplingType() == SaplingType.BLAZING;
    }


    public void draw(SpriteBatch batch) {
        tree.draw(batch);
    }

    public boolean isInteractionEnabled() {
        return isInteractionEnabled;
    }

    public BlazingTree getBlazingTree() {
        return tree;
    }

    public void dispose() {
        tree.dispose();
    }
}
