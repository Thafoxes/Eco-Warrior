package io.github.eco_warrior.animation.Explosion;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.BaseExplosion;

public class RedExplosion extends BaseExplosion {
    private static final String ATLAS_PATH = "effects/red_explosion.atlas";
    private static final String REGION_NAME = "Explosion";
    private static final float ANIMATION_DURATION = 0.6f;

    private Animation<TextureRegion> explosionAnimation;
    private float stateTime = 0f;
    private boolean isFinished = false;
    private float scale = 1.0f;

    public RedExplosion(Vector2 position, float scale) {
        super(ATLAS_PATH, position, scale, REGION_NAME, ANIMATION_DURATION);
    }

    public RedExplosion(Vector2 position) {
        this(position, 1.0f);
    }

}
