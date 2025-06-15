package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.sprite.tree_variant.BlazingTree;

public class BlazingTreeHealth extends TreeHealth {
    BlazingTree blazingTree;

    public BlazingTreeHealth(BlazingTree blazingTree) {
        super("atlas/tree_health/blazing_tree_health.atlas",
            "BzHP",
            blazingTree.adjustedPosition);

        this.blazingTree = blazingTree;
    }

    @Override
    public void updateHealth() {
        switch (blazingTree.health) {
            case 4:
                setFrame(TreeHealthAnimation.HP_MAX.ordinal());
                break;
            case 3:
                setFrame(TreeHealthAnimation.HP_3.ordinal());
                break;
            case 2:
                setFrame(TreeHealthAnimation.HP_2.ordinal());
                break;
            case 1:
                setFrame(TreeHealthAnimation.HP_1.ordinal());
                break;
            default:
                setFrame(TreeHealthAnimation.HP_0.ordinal());
                break;
        }
    }
}
