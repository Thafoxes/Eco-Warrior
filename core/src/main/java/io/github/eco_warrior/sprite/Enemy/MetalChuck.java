package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.GameSprite;

public class MetalChuck extends GameSprite {

    public MetalChuck(String atlasPath, String regionBaseName, int frameCount, Vector2 position, float scale) {
        super(atlasPath,
            regionBaseName,
            frameCount,
            position,
            scale);
    }
}
