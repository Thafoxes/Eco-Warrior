package io.github.eco_warrior.animation;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.BaseExplosion;

public class FireBurningAnim extends BaseExplosion {
    private static final String ATLAS_PATH = "effects/fire_burn.atlas";
    private static final String REGION_NAME = "Explosion";
    private static final float ANIMATION_DURATION = 0.9f;

    public FireBurningAnim(Vector2 position, float scale) {
        super(ATLAS_PATH, position, scale, REGION_NAME, ANIMATION_DURATION);
    }

}
