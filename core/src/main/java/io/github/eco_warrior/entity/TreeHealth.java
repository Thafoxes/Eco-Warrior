package io.github.eco_warrior.entity;

import com.badlogic.gdx.math.Vector2;

public class TreeHealth extends gameSprite {

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
        int frameIndex = Math.max(0, Math.min(4, 4 - tree.health));
        setFrame(frameIndex);
    }
}
