package io.github.eco_warrior.animation;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.BaseExplosion;

public class WaterSplashAnimation extends BaseExplosion {
    private static final String ATLAS_PATH = "effects/water_splash.atlas";
    private static final String REGION_NAME = "Explosion";
    private static final float ANIMATION_DURATION = 0.6f;

    public WaterSplashAnimation(Vector2 position, float scale) {
        super(ATLAS_PATH, position, scale, REGION_NAME, ANIMATION_DURATION);
    }
}
