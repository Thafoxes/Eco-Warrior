package io.github.eco_warrior.sprite.tree_variant;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.entity.Trees;

import java.util.HashMap;
import java.util.Map;

public class BlazingTree extends Trees {

    public enum TreeStage {
        FLAG,
        HOLE,
        SAPLING,
        GROWING_TREE,
        MATURED_TREE,
        DEAD_SAPLING,
        DEAD_YOUNG_TREE,
        DEAD_MATURE_TREE
    }

    public TreeStage treeStage = TreeStage.FLAG;

    private final Sound growthSound;
    private final Sound waterPourSound;
    private final Sound saplingSound;

    // References to scheduled tasks
    public Timer.Task growTask;
    public Timer.Task animationTask;

    private TextureAtlas atlas;
    private final Map<TreeStage, Animation<TextureRegion>> animationMap = new HashMap<>();

    private boolean isMaturedTree = false;
    private float stateTime = 0;
    private final String filePath = "atlas/tree_variant_stages/BlazingTree.atlas";

    public BlazingTree(Vector2 position, float scale) {

        super("atlas/tree_variant_stages/BlazingTree.atlas",
            "flag",
            1,
            position,
            scale);

        growthSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/fireball.mp3"));
        waterPourSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/pour_watering_can.mp3"));
        saplingSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/sapling_placement.mp3"));
        loadAnimation();
    }

    private void loadAnimation() {
        // Load animations directly here
        atlas = new TextureAtlas(Gdx.files.internal(filePath));


        animationMap.put(TreeStage.FLAG, new Animation<>(0.1f, atlas.findRegions("flag"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.HOLE, new Animation<>(0.1f, atlas.findRegions("hole"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.SAPLING, new Animation<>(0.1f, atlas.findRegions("sapling"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.GROWING_TREE, new Animation<>(0.1f, atlas.findRegions("growing_phase"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.MATURED_TREE, new Animation<>(0.1f, atlas.findRegions("matured"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.DEAD_SAPLING, new Animation<>(0.1f, atlas.findRegions("dead_sapling"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.DEAD_YOUNG_TREE, new Animation<>(0.1f, atlas.findRegions("dead_young_tree"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.DEAD_MATURE_TREE, new Animation<>(0.1f, atlas.findRegions("dead_mature_tree"), Animation.PlayMode.NORMAL));
    }


    /**
     * Water the tree to promote growth.
     * This method will only work if the tree is in a stage that allows watering.
     */
    public void water(){
        if( treeStage == TreeStage.SAPLING || treeStage == TreeStage.GROWING_TREE) {
            waterPourSound.play(0.5f);
            setStage(TreeStage.GROWING_TREE);
            if (growTask != null) {
                growTask.cancel();
            }
            growTask = Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    setStage(TreeStage.MATURED_TREE);
                    growthSound.play(0.5f);
                }
            }, 3f); // Adjust the delay as needed
        }
    }

    /**
     * Plant a sapling in the dug hole.
     * This method will only work if the tree is in the HOLE stage.
     */
    public void plantSapling() {
        if (treeStage == TreeStage.HOLE) {
            saplingSound.play(1.5f);
            setStage(TreeStage.SAPLING);
        }else{
            System.out.println("Cannot plant sapling at this stage: " + treeStage);
        }
    }

    /**
     * Dig a hole for planting a sapling.
     */
    public void digHole() {
        if (treeStage == TreeStage.FLAG) {
            diggingSound();
            setStage(TreeStage.HOLE);
//            System.out.println(treeStage);

        }else{
            System.out.println("Cannot dig a hole at this stage: " + treeStage);
        }
    }

    public void die(){
        TreeStage deadStage;
        switch (treeStage) {
            case SAPLING:
                deadStage = TreeStage.DEAD_SAPLING;
                break;
            case MATURED_TREE:
                deadStage = TreeStage.DEAD_MATURE_TREE;
                break;
            default:
                deadStage = treeStage; // No change for FLAG or HOLE
                break;
        };
        setStage(deadStage);
    }

    private void setStage(TreeStage stage) {
        treeStage = stage;
        stateTime = 0;
        if (animationMap.containsKey(stage)) {
            // Reset animation and update sprite
            Animation<TextureRegion> animation = animationMap.get(stage);
            getSprite().setRegion(animation.getKeyFrame(0));
        }
    }

    public TreeStage getStage() {
        return treeStage;
    }


    @Override
    public void update(float delta) {
        stateTime += delta;
//        if (treeStage == TreeStage.MATURED_TREE) {
//            Animation<TextureRegion> animation = animationMap.get(treeStage);
//            getSprite().setRegion(animation.getKeyFrame(delta, true));
//        }
        if (animationMap.containsKey(treeStage)) {
            Animation<TextureRegion> animation = animationMap.get(treeStage);
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            getSprite().setRegion(currentFrame);
        }
        super.update(delta);
    }
}
