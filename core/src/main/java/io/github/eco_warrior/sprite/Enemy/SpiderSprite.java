package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ToolType;

public class SpiderSprite extends gameSprite {
    public enum SpiderState {
        ALIVE,
        DYING,
        DEAD
    }

    private static final String ATLAS_PATH = "enemy/spider.atlas";
    private static final String ALIVE_REGION = "spider_v2x64";
    private static final String DEAD_REGION = "spider_v2x65";
    private static final String SPLAT_SOUND = "sound_effects/cartoon_splat.mp3";

    //animation parameters
    private static final float JUMP_VELOCITY = 200F; // initial upward velocity
    private static final float GRAVITY = 500F; //downward acceleration

    private SpiderState state = SpiderState.ALIVE;
    private float velocityY = 0F;
    private TextureRegion aliveRegion;
    private TextureRegion deadRegion;

    //audio zone
    private boolean hasSoundPlayed = false;
    private Sound splatSound;


    public SpiderSprite(Vector2 Position, float scale) {
        super(ATLAS_PATH, ALIVE_REGION, Position, scale);

        TextureAtlas atlas = new TextureAtlas(ATLAS_PATH);
        aliveRegion = atlas.findRegion(ALIVE_REGION);
        deadRegion = atlas.findRegion(DEAD_REGION);
        splatSound = Gdx.audio.newSound(Gdx.files.internal(SPLAT_SOUND));

    }

    public SpiderSprite (Vector2 position){
        this(position, 1.0f);
    }

    /**
     * Update the spider state and animation
     *
     * @param delta Time elapsed since last update
     */
    public void update(float delta){
        super.update(delta);

        if(state == SpiderState.DYING){
            velocityY -= GRAVITY * delta; //applying gravity

            getCollisionRect().y += velocityY * delta; //update position

            if(getCollisionRect().y + getCollisionRect().height < 0){
                state = SpiderState.DEAD;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch){
        if(state != SpiderState.DEAD){
            super.draw(batch);
        }
    }

    /**
     * Kill the spider with water spray
     *
     * @return true if killed
     */
    public boolean kill(){
        if(state == SpiderState.ALIVE){
            getSprite().setRegion(deadRegion);

            velocityY = JUMP_VELOCITY;
            onHitByWaterSpray();

            state = SpiderState.DYING;

            return true;
        }

        return false;
    }

    /**
     * Check if a tool can kill this spider
     *
     * @param toolType Type of tool to check
     * @return true if this tool can kill the spider
     */
    public boolean isKillable(ToolType toolType) {
        return (toolType == ToolType.WATER_SPRAY && state == SpiderState.ALIVE);
    }

    /**
     * Handle sound and effects when hit by water spray
     * Only plays sound once when the spider is first hit
     */
    private void onHitByWaterSpray() {
        if (!hasSoundPlayed) {
            // Play the spider death sound
            splatSound.play(0.6f);

            // Mark that we've played the sound, and then dead
            hasSoundPlayed = true;
        }
    }

    /**
     * Check if the spider is dead and should be removed
     *
     * @return true if the spider is dead
     */
    public boolean isDead() {
        return state == SpiderState.DEAD;
    }

    public SpiderState getState() {
        return state;
    }

    @Override
    public void dispose() {
        super.dispose();
        splatSound.dispose();
    }

}
