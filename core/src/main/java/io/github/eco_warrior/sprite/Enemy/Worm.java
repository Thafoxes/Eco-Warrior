package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.LevelTwoScreen.WormPath;
import io.github.eco_warrior.entity.Trees;
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
        DEATH_14,
        DEATH_15,
        DEATH_16
    }

    private WormPath path;
    private Trees tree;

    public float speed = 100f; // Speed of the worm
    private int wormLevel = WormAnimation.MOVE_1.ordinal(); // Current level of the worm
    private boolean isStoppingScheduled = false;
    private boolean isMoveTransitionScheduled = false;
    public boolean isDeathTransition = false;
    public boolean isDead = false; // Flag to check if the worm is dead

    // References to scheduled tasks
    public Timer.Task attackTask;
    private Timer.Task moveTask;
    private Timer.Task stopTask;
    private Timer.Task deathTask;

    //sound effects
    private final Sound attackSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/whip.mp3"));

    public Worm(Vector2 position, float scale) {
        super("atlas/mobs/worm.atlas",
            "worm",
            16,
            position,
            scale);
        originalPos = position;
    }

    public Worm(Vector2 position) {
        this(position, .2f);
    }

    public void reset() {
        speed = 100f;
        setFrame(WormAnimation.MOVE_1.ordinal());
        isStoppingScheduled = false;
        isMoveTransitionScheduled = false;
        isDeathTransition = false;
        isDead = false;

        // Cancel any scheduled tasks
        if (attackTask != null) {
            attackTask.cancel();
            attackTask = null;
        }
        if (moveTask != null) {
            moveTask.cancel();
            moveTask = null;
        }
        if (stopTask != null) {
            stopTask.cancel();
            stopTask = null;
        }
        if (deathTask != null) {
            deathTask.cancel();
            deathTask = null;
        }

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

    private void startAttackAnimationLoop() {
        runAttackFrame(WormAnimation.ATTACK_5.ordinal());
    }

    private void runAttackFrame(final int frame) {
        setFrame(frame);
        wormLevel = frame;

        if (!isDeathTransition) {
            if (frame < WormAnimation.ATTACK_9.ordinal()) {
                // Cycle to next attack frame after 0.2s
                attackTask = new Timer.Task() {
                    @Override
                    public void run() {
                        runAttackFrame(frame + 1);

                        if (frame == WormAnimation.ATTACK_5.ordinal()) {
                            attackSound.play(.5f);
                        } else if (frame == WormAnimation.ATTACK_7.ordinal()) {
                            if (tree.health > 0) {
                                tree.health--; // Decrease tree health
                                System.out.println("Tree health:" + tree.health);
                            } else {
                                System.out.println("Tree health:" + tree.health);
                            }
                        }
                    }
                };
                Timer.schedule(attackTask, 0.2f);
            } else {
                // Hold ATTACK_9 for 3s, then restart at ATTACK_5
                attackTask = new Timer.Task() {
                    @Override
                    public void run() {
                        runAttackFrame(WormAnimation.ATTACK_5.ordinal());
                    }
                };
                Timer.schedule(attackTask, 3f);
            }
        }
    }

    public void startDeathAnimation() {
        runDeathFrame(WormAnimation.DEATH_10.ordinal());
    }

    private void runDeathFrame(final int frame) {
        setFrame(frame);
        wormLevel = frame;

        if (frame < WormAnimation.DEATH_16.ordinal()) {
            deathTask = new Timer.Task() {
                @Override
                public void run() {
                    runDeathFrame(frame + 1);
                }
            };
            Timer.schedule(deathTask, 0.3f);
        } else {
            isDead = true;
        }
    }

    // Handle the animated worm movement
    public void updateWormAnimationMovement() {
        if (!isMoveTransitionScheduled
            && !isStoppingScheduled
            && !isDeathTransition) {
            isMoveTransitionScheduled = true;

            moveTask = new Timer.Task() {
                @Override
                public void run() {
//                    System.out.println("Worm moving to next frame: " + wormLevel);
                    int nextFrame = wormLevel + 1;
                    if (nextFrame > WormAnimation.MOVE_4.ordinal()) {
                        nextFrame = WormAnimation.MOVE_1.ordinal();
                    }
                    setFrame(nextFrame);
                    wormLevel = nextFrame;
                    isMoveTransitionScheduled = false;
                }
            };
            Timer.schedule(moveTask, .3f); // 0.3 seconds delay
        }

        if (getCollisionRect().overlaps(tree.getCollisionRect())
            && speed != 0
            && !isStoppingScheduled) {
            isStoppingScheduled = true;

            stopTask = new Timer.Task() {
                @Override
                public void run() {
//                    System.out.println("Stop");
                    speed = 0; // Stop the worm after 0.3s
                    startAttackAnimationLoop();
                }
            };
            Timer.schedule(stopTask, .3f);
        }
    }

    public void treeTarget(Trees tree) {
        this.tree = tree;
    }
}
