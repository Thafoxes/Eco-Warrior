package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.sprite.tree_variant.IceTree;
import io.github.eco_warrior.sprite.tree_variant.OrdinaryTree;

public class IceTreeHealth extends TreeHealth {
    IceTree iceTree;

    public IceTreeHealth(IceTree iceTree) {
        super("atlas/tree_health/ice_tree_health.atlas",
            "IHP",
            iceTree.adjustedPosition);

        this.iceTree = iceTree;
    }

    @Override
    public void updateHealth() {
//        switch (iceTree.health) {
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
