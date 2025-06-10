package io.github.eco_warrior.sprite.tree_variant;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;

public class VoltaicTree extends Trees {

    public enum TreeStage {
        FLAG,
        HOLE,
        SAPLING,
        YOUNG_TREE,
        ANIMATED_MATURE_TREE_1,
        ANIMATED_MATURE_TREE_2,
        ANIMATED_MATURE_TREE_3,
        ANIMATED_MATURE_TREE_4
    }

    public int treeLevel = TreeStage.FLAG.ordinal();
    private boolean isStageTransitionScheduled = false;

    private final Sound digSound;
    private final Sound growthSound;
    private final Sound waterPourSound;
    private final Sound saplingSound;

    public VoltaicTree(Vector2 position, float scale) {
        super("atlas/tree_variant_stages/voltaic_tree_stages.atlas",
            "voltaic_tree",
            8,
            position,
            scale);

        digSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/Gravel_dig1.mp3"));
        growthSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/thunder.mp3"));
        waterPourSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/pour_watering_can.mp3"));
        saplingSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/sapling_placement.mp3"));
    }

    public void updateTreeStatus(gameSprite shovel, gameSprite sapling, WateringCan wateringCan) {
        if (treeLevel == TreeStage.FLAG.ordinal() && getCollisionRect().overlaps(shovel.getCollisionRect())) {
            digSound.play();
            treeLevel = TreeStage.HOLE.ordinal();

            setFrame(treeLevel);
        }
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

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (treeLevel == TreeStage.SAPLING.ordinal()) {
                        treeLevel = TreeStage.YOUNG_TREE.ordinal();
                    } else if (treeLevel == TreeStage.YOUNG_TREE.ordinal()) {
                        treeLevel = TreeStage.ANIMATED_MATURE_TREE_1.ordinal();
                    }
                    growthSound.play(1.5f);
                    setFrame(treeLevel);
                    isStageTransitionScheduled = false;
                }
            }, 2); // 3 seconds delay
        }

        if (treeLevel >= VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal()
            && treeLevel <= VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_4.ordinal()
            && !isStageTransitionScheduled) {
            isStageTransitionScheduled = true;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    int nextFrame = treeLevel + 1;
                    if (nextFrame > VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_4.ordinal()) {
                        nextFrame = VoltaicTree.TreeStage.ANIMATED_MATURE_TREE_1.ordinal();
                    }
                    setFrame(nextFrame);
                    treeLevel = nextFrame;
                    isStageTransitionScheduled = false;
                }
            }, 0.3f); // 0.3 seconds delay
        }// 3 seconds delay
    }
}
