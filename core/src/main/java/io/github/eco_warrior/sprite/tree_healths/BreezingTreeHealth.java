package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.sprite.tree_variant.BreezingTree;

public class BreezingTreeHealth extends TreeHealth {
    BreezingTree breezingTree;

    public BreezingTreeHealth(BreezingTree breezingTree) {
        super("atlas/tree_health/breezing_tree_health.atlas",
            "BrHP",
            breezingTree.adjustedPosition);

        this.breezingTree = breezingTree;
    }

    @Override
    public void updateHealth() {
//        switch (breezingTree.health) {
//            case 4:
//                setFrame(TreeHealthAnimation.HP_MAX.ordinal());
//                break;
//            case 3:
//                setFrame(TreeHealthAnimation.HP_3.ordinal());
//                break;
//            case 2:
//                setFrame(TreeHealthAnimation.HP_2.ordinal());
//                break;
//            case 1:
//                setFrame(TreeHealthAnimation.HP_1.ordinal());
//                break;
//            default:
//                setFrame(TreeHealthAnimation.HP_0.ordinal());
//                break;
//        }
    }
}
