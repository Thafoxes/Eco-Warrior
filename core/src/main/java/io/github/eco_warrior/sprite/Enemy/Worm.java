package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.LevelTwoScreen.WormPath;
import io.github.eco_warrior.entity.gameSprite;

public class Worm extends gameSprite {
    private static Vector2 originalPos;

    public enum WormState {
        ALIVE,
        DEAD
    }

    public enum WormAnimation {
        MOVE_1,
        MOVE_2,
        MOVE_3,
        MOVE_4,
        ATTACK_5,
        ATTACK_6,
        ATTACK_7,
        ATTACK_8,
        ATTACK_9,
        DEATH_10,
        DEATH_11,
        DEATH_12,
        DEATH_13,
        DEATH_14
    }

    private WormPath path;

    private final float speed = 100f; // Speed of the worm
    private int wormLevel = WormAnimation.MOVE_1.ordinal(); // Current level of the worm
    private boolean isMoveTransitionScheduled = false;

    public Worm(Vector2 position, float scale) {
        super("atlas/mobs/worm.atlas",
            "worm",
            14,
            position,
            scale);
        originalPos = position;
    }

    public Worm(Vector2 position) {
        this(position, .2f);
    }

    public void reset() {
        getSprite().setPosition(originalPos.x, originalPos.y);
        getCollisionRect().setPosition(originalPos.x, originalPos.y);
        System.out.println("Worm reset to original position: " + originalPos);
    }
    //move from right to left
    @Override
    public void update(float delta){
        getSprite().setX(getSprite().getX() - speed * delta);
        getCollisionRect().setX(getSprite().getX());
    }

    // Set worm path
    public void setPath(WormPath path) {
        this.path = path;
    }

    // Get worm path
    public WormPath getPath() {
        return path;
    }

    // Handle the animated worm movement
    public void updateWormAnimationMovement() {
        if (wormLevel >= WormAnimation.MOVE_1.ordinal()
            && wormLevel <= WormAnimation.MOVE_4.ordinal()
            && !isMoveTransitionScheduled) {
            isMoveTransitionScheduled = true;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    int nextFrame = wormLevel + 1;
                    if (nextFrame > WormAnimation.MOVE_4.ordinal()) {
                        nextFrame = WormAnimation.MOVE_1.ordinal();
                    }
                    setFrame(nextFrame);
                    wormLevel = nextFrame;
                    isMoveTransitionScheduled = false;
                }
            }, .3f); // 0.3 seconds delay
        }
    }
}
