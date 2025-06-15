package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.sprite.tree_variant.OrdinaryTree;

public class OrdinaryTreeHealth extends TreeHealth {
    OrdinaryTree ordinaryTree;

    public OrdinaryTreeHealth(OrdinaryTree ordinaryTree) {
        super("atlas/tree_health/tree_health.atlas",
            "HP",
            ordinaryTree.adjustedPosition);

        this.ordinaryTree = ordinaryTree;
    }

    @Override
    public void updateHealth() {
        switch (ordinaryTree.health) {
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
