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

    public TreeHealth(String atlasPath, String regionBaseName, Vector2 position) {
        super(atlasPath,
            regionBaseName,
            5,
            position,
            .1f);
    }

   public void updateHealth() {
        // This method should be overridden by subclasses to update the health
        // based on the specific tree type.
        throw new UnsupportedOperationException("updateHealth() must be implemented in subclasses");
    }
}
