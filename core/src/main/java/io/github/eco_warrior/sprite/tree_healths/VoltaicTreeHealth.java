package io.github.eco_warrior.sprite.tree_healths;

import io.github.eco_warrior.entity.TreeHealth;
import io.github.eco_warrior.sprite.tree_variant.VoltaicTree;

public class VoltaicTreeHealth extends TreeHealth {
    VoltaicTree voltaicTree;

    public VoltaicTreeHealth(VoltaicTree voltaicTree) {
        super("atlas/tree_health/voltaic_tree_health.atlas",
            "VHP",
            voltaicTree.adjustedPosition);

        this.voltaicTree = voltaicTree;
    }

    @Override
    public void updateHealth() {
        switch (voltaicTree.health) {
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
