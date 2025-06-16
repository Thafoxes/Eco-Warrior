package io.github.eco_warrior.sprite.tree_variant;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;

public class BreezingTree extends Trees {

    public enum TreeStage {
        FLAG,
        HOLE,
        SAPLING,
        YOUNG_TREE,
        ANIMATED_MATURE_TREE_1,
        ANIMATED_MATURE_TREE_2,
        ANIMATED_MATURE_TREE_3,
        DEAD_SAPLING,
        DEAD_YOUNG_TREE,
        DEAD_MATURE_TREE
    }


    public int treeLevel = TreeStage.FLAG.ordinal();

    private final Sound growthSound;
    private final Sound waterPourSound;
    private final Sound saplingSound;

    // References to scheduled tasks
    public Timer.Task growTask;
    public Timer.Task animationTask;

    public BreezingTree(Vector2 position, float scale) {
        super("atlas/tree_variant_stages/breezing_tree_stages.atlas",
            "breezing_tree",
            10,
            position,
            scale);

        growthSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/breeze.mp3"));
        waterPourSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/pour_watering_can.mp3"));
        saplingSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/sapling_placement.mp3"));
    }

    public void updateTree(GameSprite sapling, WateringCan wateringCan) {
        if (treeLevel == TreeStage.HOLE.ordinal() && getCollisionRect().overlaps(sapling.getCollisionRect())) {
            saplingSound.play(1.5f);
            treeLevel = TreeStage.SAPLING.ordinal();

            setFrame(treeLevel);
        }
        if ((treeLevel == TreeStage.SAPLING.ordinal() || treeLevel == TreeStage.YOUNG_TREE.ordinal())
            && getCollisionRect().overlaps(wateringCan.getCollisionRect())
            && wateringCan.waterLevel == WateringCan.WateringCanState.FILLED.ordinal()
            && !isStageTransitionScheduled) {

            wateringCan.waterLevel = WateringCan.WateringCanState.EMPTY.ordinal();
            wateringCan.setFrame(wateringCan.waterLevel);
            isStageTransitionScheduled = true;
            waterPourSound.play(1f);

            growTask = new Timer.Task() {
                @Override
                public void run() {
                    if (treeLevel == TreeStage.SAPLING.ordinal()) {
                        treeLevel = TreeStage.YOUNG_TREE.ordinal();
                    } else if (treeLevel == TreeStage.YOUNG_TREE.ordinal()) {
                        treeLevel = TreeStage.ANIMATED_MATURE_TREE_1.ordinal();
                        isMatureTree = true;
                    }

                    growthSound.play(1.5f);
//                    health = 4; // Reset health for the next tree stage
                    setFrame(treeLevel);
                    isStageTransitionScheduled = false;
                }
            };
            Timer.schedule(growTask, 2); // 2 seconds delay
        }

        // Handle the animated mature tree stages
        if (treeLevel >= TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
            && treeLevel <= TreeStage.ANIMATED_MATURE_TREE_3.ordinal()
            && !isStageTransitionScheduled) {
            isStageTransitionScheduled = true;

            animationTask = new Timer.Task() {
                @Override
                public void run() {
                    int nextFrame = treeLevel + 1;
                    if (nextFrame > TreeStage.ANIMATED_MATURE_TREE_3.ordinal()) {
                        nextFrame = TreeStage.ANIMATED_MATURE_TREE_1.ordinal();
                    }
                    setFrame(nextFrame);
                    treeLevel = nextFrame;
                    isStageTransitionScheduled = false;
                }
            };
            Timer.schedule(animationTask, .3f); // 0.3 seconds delay
        }

        treeObliteration();
    }

    @Override
    public void treeObliteration() {
//        if (health == 0) {
//            if (growTask != null) {
//                growTask.cancel();
//                growTask = null;
//            }
//            if (animationTask != null) {
//                animationTask.cancel();
//                animationTask = null;
//            }
//
//            if (treeLevel == TreeStage.SAPLING.ordinal()) {
//                treeLevel = TreeStage.DEAD_SAPLING.ordinal();
//            } else if (treeLevel == TreeStage.YOUNG_TREE.ordinal()) {
//                treeLevel = TreeStage.DEAD_YOUNG_TREE.ordinal();
//            } else if (treeLevel >= TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
//                && treeLevel <= TreeStage.ANIMATED_MATURE_TREE_3.ordinal()) {
//                treeLevel = TreeStage.DEAD_MATURE_TREE.ordinal();
//            }
//
//            setFrame(treeLevel);
//        }
    }
}
