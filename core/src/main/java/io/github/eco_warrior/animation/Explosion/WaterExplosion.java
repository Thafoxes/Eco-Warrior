package io.github.eco_warrior.animation.Explosion;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.BaseExplosion;

public class WaterExplosion extends BaseExplosion {
    private static final String ATLAS_PATH = "effects/blue_explosion.atlas";
    private static final String REGION_NAME = "Explosion";
    private static final float ANIMATION_DURATION = 0.6f;

    public WaterExplosion(Vector2 position, float scale) {
        super(ATLAS_PATH, position, scale, REGION_NAME, ANIMATION_DURATION);

    }


}
