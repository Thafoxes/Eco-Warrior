package io.github.eco_warrior.sprite.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.animation.HeartEffect;
import io.github.eco_warrior.controller.FontGenerator;

import java.util.ArrayList;
import java.util.List;

public class Hearts {


    private TextureAtlas atlas;
    private TextureAtlas.AtlasRegion heartRegion;
    private int maxHearts;
    private int currentHearts;
    private float scale;
    private float spacing;
    private Vector2 position;
    private List<HeartEffect> heartEffects;

    private FontGenerator fontGenerator;
    private OrthographicCamera camera;

    /***
     * Since there is no collision and is for presentation purposes only, no need to extend from the gameSprite class.
     */
    public Hearts(Vector2 position, int maxHearts, float scale, float spacing, OrthographicCamera camera) {
        this.atlas = new TextureAtlas(Gdx.files.internal("atlas/heart/hearts.atlas"));
        this.heartRegion = atlas.findRegion("heart_pixel");
        this.maxHearts = maxHearts;
        this.currentHearts = maxHearts;
        this.scale = scale;
        this.spacing = spacing;
        this.position = position;
        this.heartEffects = new ArrayList<>();

        this.fontGenerator = new FontGenerator(24, Color.WHITE, Color.BLACK);
        this.camera = camera;
    }

    public void draw(SpriteBatch batch) {
        // Draw the remaining hearts

        float heartWidth = heartRegion.getRegionWidth() * scale;
        float heartHeight = heartRegion.getRegionHeight() * scale;

        Vector2 labelPos = new Vector2(position.x , position.y + heartRegion.getRegionHeight() );

        batch.end();
        batch.begin();
        fontGenerator.fontDraw(batch , "Hearts: ", camera, labelPos);


        for (int i = 0; i < currentHearts; i++) {
            float x = position.x + (i * (heartWidth + spacing));
            batch.draw(heartRegion, x, position.y, heartWidth, heartHeight);
        }

        for(HeartEffect effect : heartEffects){
            effect.draw(batch);
        }
    }


    public void update(float delta) {
        // Update all effects
        for (int i = heartEffects.size() - 1; i >= 0; i--) {
            HeartEffect effect = heartEffects.get(i);
            effect.update(delta);
            if (effect.isFinished()) {
                heartEffects.remove(i);
            }
        }
    }

    public boolean loseHeart() {
        if (currentHearts > 0) {
            currentHearts--;
        }
        return currentHearts <= 0;
    }

    public int getCurrentHearts() {
        return currentHearts;
    }

    public void dispose() {
        atlas.dispose();
    }

    public void loseHeartWithEffect(float delta) {
        if (currentHearts > 0) {
            // Calculate position of the heart that will be lost
            float heartWidth = heartRegion.getRegionWidth() * scale;
            float x = position.x + ((currentHearts - 1) * (heartWidth + spacing));


            // Create a new effect at the position of the lost heart
            heartEffects.add(new HeartEffect(
                heartRegion,
                new Vector2(x, position.y),
                scale
            ));

            currentHearts--;
        }
    }


}
