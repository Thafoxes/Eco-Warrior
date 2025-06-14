package io.github.eco_warrior.sprite.tree_variant;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;

public class OrdinaryTree extends Trees {

    public enum TreeStage {
        FLAG,
        HOLE,
        SAPLING,
        YOUNG_TREE,
        MATURE_TREE,
    }

    public int treeLevel = TreeStage.FLAG.ordinal();

    private final Sound growthSound;
    private final Sound waterPourSound;
    private final Sound saplingSound;

    public OrdinaryTree(Vector2 position, float scale) {
        super("atlas/tree_variant_stages/ordinary_tree_stages.atlas",
            "ordinary_tree",
            5,
            position,
            scale);

        growthSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/Bonemeal1.mp3"));
        waterPourSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/pour_watering_can.mp3"));
        saplingSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/sapling_placement.mp3"));
    }

    public void updateTree(gameSprite shovel, gameSprite sapling, WateringCan wateringCan) {
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
                        treeLevel = TreeStage.MATURE_TREE.ordinal();
                        isMatureTree = true;
                    }
                    growthSound.play(1.5f);
                    setFrame(treeLevel);
                    isStageTransitionScheduled = false;
                }
            }, 2); // 2 seconds delay
        }
    }
}
