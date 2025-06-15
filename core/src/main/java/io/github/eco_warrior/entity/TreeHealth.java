package io.github.eco_warrior.entity;

import com.badlogic.gdx.math.Vector2;

public class TreeHealth extends gameSprite {

    public enum TreeHealthAnimation {
        HP_MAX,
        HP_3,
        HP_2,
        HP_1,
        HP_0
    }

    Trees tree;

    public TreeHealth(String atlasPath, String regionBaseName, Vector2 position, Trees tree) {
        super(atlasPath,
            regionBaseName,
            5,
            position,
            .1f);

        this.tree = tree;
    }

    public void updateHealth() {
        switch (tree.health) {
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
