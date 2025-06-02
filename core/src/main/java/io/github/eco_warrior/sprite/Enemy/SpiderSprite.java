package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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

    private static final float TIME_BEFORE_ATTACK = 5.0F; // time before spider can attack again
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
    private float existenceTimer = 0f;
    private boolean hasMadeCrack = false;

    //audio zone
    private boolean hasSoundPlayed = false;
    private Sound splatSound;

    // particle effect for crack
    private ParticleEffect crackEffect;

    // callback for crack creation
    private CrackCreationCallback crackCreationCallback;

    // Interface for crack creation callback
    public interface CrackCreationCallback {
        void createCrack(Vector2 position);
    }

    public SpiderSprite(Vector2 Position, float scale, CrackCreationCallback callback) {
        super(ATLAS_PATH, ALIVE_REGION, Position, scale);

        TextureAtlas atlas = new TextureAtlas(ATLAS_PATH);
        aliveRegion = atlas.findRegion(ALIVE_REGION);
        deadRegion = atlas.findRegion(DEAD_REGION);
        splatSound = Gdx.audio.newSound(Gdx.files.internal(SPLAT_SOUND));

        this.crackCreationCallback = callback;

        try {
            //load particle effect
            crackEffect = new ParticleEffect();
            crackEffect.load(Gdx.files.internal("effects/crack_effect.p"), Gdx.files.internal("effects"));
            crackEffect.setPosition(Position.x, Position.y);
        } catch (Exception e) {
            Gdx.app.error("SpiderSprite", "Failed to load crack effect: " + e.getMessage());
            crackEffect = null; // Set to null if loading fails
        }

    }

    public SpiderSprite (Vector2 position, CrackCreationCallback callback){
        this(position, 1.0f, callback);
    }

    /**
     * Update the spider state and animation
     *
     * @param delta Time elapsed since last update
     */
    public void update(float delta){
        super.update(delta);

        if(state == SpiderState.ALIVE){
            existenceTimer += delta;

            if(existenceTimer >= TIME_BEFORE_ATTACK && !hasMadeCrack){
                // Create a crack at the spider's position
                createCrackUnderSpider();
                hasMadeCrack = true;

                if (crackEffect != null) {
                    crackEffect.start();
                }
            }
        }
        if(state == SpiderState.DYING){
            velocityY -= GRAVITY * delta; //applying gravity

            getCollisionRect().y += velocityY * delta; //update position

            if(getCollisionRect().y + getCollisionRect().height < 0){
                state = SpiderState.DEAD;
            }
        }
        if(crackEffect != null){
            crackEffect.update(delta);
        }
    }

    private void createCrackUnderSpider() {
        if(crackCreationCallback != null) {
            Vector2 crackPosition = new Vector2(getCollisionRect().x + getCollisionRect().width / 2, getCollisionRect().y);
            crackCreationCallback.createCrack(crackPosition);
        }
    }

    @Override
    public void draw(SpriteBatch batch){
        if(state != SpiderState.DEAD){
            super.draw(batch);
        }
        if(crackEffect != null){
            crackEffect.draw(batch);
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
        if(crackEffect != null){
            crackEffect.dispose();
        }
    }

}
