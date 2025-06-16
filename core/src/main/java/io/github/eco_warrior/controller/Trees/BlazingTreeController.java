package io.github.eco_warrior.controller.Trees;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.eco_warrior.controller.Sapling.BaseSaplingController;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.enums.SaplingType;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;
import io.github.eco_warrior.sprite.tree_healths.BlazingTreeHealth;
import io.github.eco_warrior.sprite.tree_variant.BlazingTree;

public class BlazingTreeController extends TreeController<BlazingTree> {

    public BlazingTreeController(BlazingTree blazingTree, WateringCan wateringCan) {
        super(blazingTree, wateringCan , new BlazingTreeHealth(blazingTree));
    }


}
