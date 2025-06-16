package io.github.eco_warrior.entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.enums.SaplingType;

import java.util.HashMap;
import java.util.Map;

public abstract class Trees extends GameSprite {


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

    protected String filePath;
    protected TreeStage treeStage = TreeStage.FLAG;
    protected TextureAtlas atlas;
    protected final Map<TreeStage, Animation<TextureRegion>> animationMap = new HashMap<>();
    protected float stateTime = 0;
    protected Timer.Task growTask;
    public Vector2 labelPosition;
    protected boolean isStageTransitionScheduled = false;
    public boolean isMatureTree = false;
    protected SaplingType saplingType;


    protected Sound digSound;
    protected Sound growthSound;
    protected Sound waterPourSound;
    protected Sound saplingSound;

    protected float growingTime = 3f;

    public Trees(String atlasPath, String regionBaseName, int frameCount, Vector2 position, float scale, SaplingType saplingType) {
        super(atlasPath,
            regionBaseName,
            frameCount,
            position,
            scale);

        this.saplingType = saplingType;
        this.filePath = atlasPath;
        this.labelPosition = new Vector2(position.x , position.y - 20f);


        digSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/Gravel_dig1.mp3"));
        try{
            loadAnimation();
            loadAudio();
        } catch (RuntimeException e) {
            Gdx.app.error("Trees", "Failed to load animations: " + e.getMessage());
            e.printStackTrace(); // Rethrow the exception to indicate failure
        }
    }

    /**
     * Load animations for the tree stages.
     * This method should be implemented by subclasses to define specific animations.
     * It is called in the constructor of the Trees class.
     *
     * @throws RuntimeException if there is an error loading animations.
     */
    protected abstract void loadAnimation() throws RuntimeException;

    protected abstract void loadAudio() throws RuntimeException;

    @Override
    public void update(float delta) {
        stateTime += delta;
        if (animationMap.containsKey(treeStage)) {
            Animation<TextureRegion> animation = animationMap.get(treeStage);
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            getSprite().setRegion(currentFrame);
        }
        super.update(delta);
    }

    /**
     * Set the stage of the tree.
     * This method will reset the state time and update the sprite region based on the new stage.
     *
     * @param stage The new stage of the tree.
     */
    protected void setStage(TreeStage stage) {
        treeStage = stage;
        stateTime = 0;
        if (animationMap.containsKey(stage)) {
            Animation<TextureRegion> animation = animationMap.get(stage);
            getSprite().setRegion(animation.getKeyFrame(0));
        }
    }

    /**
     * Dig a hole to plant a sapling.
     * This method will only work if the tree is in the FLAG stage.
     */
    public void water() {
        if (treeStage == TreeStage.SAPLING || treeStage == TreeStage.GROWING_TREE) {
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
                    isMatureTree = true;
                }
            }, growingTime);
        }
    }

    /**
     * Dig a hole to plant a sapling.
     * This method will only work if the tree is in the FLAG stage.
     */
    public void plantSapling() {
        if (treeStage == TreeStage.HOLE) {
            saplingSound.play(0.5f);
            setStage(TreeStage.SAPLING);
        }
    }

    public void revivePlant() {
        switch (treeStage){
            case DEAD_SAPLING:
                setStage(TreeStage.SAPLING);
                break;
            case DEAD_YOUNG_TREE:
                setStage(TreeStage.GROWING_TREE);
                break;
                case DEAD_MATURE_TREE:
                setStage(TreeStage.MATURED_TREE);
                break;
            default:
                setStage(treeStage);
        }
    }

    /**
     * Dig a hole for planting a sapling.
     * This method will only work if the tree is in the FLAG stage.
     */
    public void digHole() {
        if (treeStage == TreeStage.FLAG) {
            digSound.play();
            setStage(TreeStage.HOLE);
        }
    }

    public void die() {
        TreeStage deadStage;
        switch (treeStage){
            case SAPLING:
                deadStage = TreeStage.DEAD_SAPLING;
                break;
            case MATURED_TREE:
                deadStage = TreeStage.DEAD_MATURE_TREE;
                break;
            default:
                deadStage = treeStage;
                break;
        };
        setStage(deadStage);
    }

    public TreeStage getStage(){
        return this.treeStage;
    }


    /**
     * Reset the tree to its initial state.
     * This method can be used to reset the tree for testing or reinitialization purposes.
     */
    public void reset() {
        setStage(TreeStage.SAPLING);
        if (growTask != null) {
            growTask.cancel();
        }
        stateTime = 0;
        isMatureTree = false;
    }

    /**
     * Reset the tree to its initial state.
     * This method can be used to reset the tree for testing or reinitialization purposes.
     */
    public void MaturedStateDebug() {
        setStage(TreeStage.MATURED_TREE);
        if (growTask != null) {
            growTask.cancel();
        }
        stateTime = 0;
        isMatureTree = false;
    }

    public SaplingType getSaplingType() {
        return saplingType;
    }

    public void diggingSound() {
        digSound.play();
    }

    @Override
    public void dispose() {
        super.dispose();
        digSound.dispose();
        growthSound.dispose();
        waterPourSound.dispose();
        saplingSound.dispose();
        if (atlas != null) {
            atlas.dispose();
        }
    }

}
