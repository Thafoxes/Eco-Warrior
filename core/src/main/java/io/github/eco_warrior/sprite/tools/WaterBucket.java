package io.github.eco_warrior.sprite.tools;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.ManualFrameSprite;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.entity.tool;

public class WaterBucket extends gameSprite {

    public WaterBucket(Vector2 position, float scale) {
        super(
            "atlas/bucket/bucket_anim.atlas",
            "bucket",
            3,
            position,
            scale);
    }



}
