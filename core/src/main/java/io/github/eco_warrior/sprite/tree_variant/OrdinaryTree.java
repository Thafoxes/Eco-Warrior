package io.github.eco_warrior.sprite.tree_variant;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.entity.GameSprite;
import io.github.eco_warrior.sprite.gardening_equipments.WateringCan;

public class OrdinaryTree extends Trees {

    public OrdinaryTree(Vector2 position, float scale) {
        super("atlas/tree_variant_stages/OrdinaryTree.atlas",
            "flag",
            1,
            position,
            scale);

    }

    @Override
    protected void loadAnimation() throws RuntimeException {
        // Load animations directly here
        atlas = new TextureAtlas(Gdx.files.internal(filePath));
        // For growing phase - distribute frames evenly across growingTime
        int growingFrameCount = atlas.findRegions("growing_phase").size;
        float frameDuration = growingTime / growingFrameCount;

        animationMap.put(TreeStage.FLAG, new Animation<>(0.1f, atlas.findRegions("flag"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.HOLE, new Animation<>(0.1f, atlas.findRegions("hole"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.SAPLING, new Animation<>(0.1f, atlas.findRegions("sapling"), Animation.PlayMode.NORMAL));

        animationMap.put(TreeStage.GROWING_TREE, new Animation<>(frameDuration, atlas.findRegions("growing_phase"), Animation.PlayMode.NORMAL));

        animationMap.put(TreeStage.MATURED_TREE, new Animation<>(0.3f, atlas.findRegions("matured"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.DEAD_SAPLING, new Animation<>(0.1f, atlas.findRegions("dead_sapling"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.DEAD_YOUNG_TREE, new Animation<>(0.1f, atlas.findRegions("dead_young_tree"), Animation.PlayMode.NORMAL));
        animationMap.put(TreeStage.DEAD_MATURE_TREE, new Animation<>(0.1f, atlas.findRegions("dead_mature_tree"), Animation.PlayMode.NORMAL));
    }

    @Override
    protected void loadAudio() throws RuntimeException {

        growthSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/Bonemeal1.mp3"));
        waterPourSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/pour_watering_can.mp3"));
        saplingSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/sapling_placement.mp3"));
    }

}
