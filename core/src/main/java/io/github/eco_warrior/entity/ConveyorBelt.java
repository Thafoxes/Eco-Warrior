package io.github.eco_warrior.entity;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class ConveyorBelt {
    private TextureAtlas atlas;
    private Animation<TextureRegion> animation;
    private ArrayList<Sprite> sprites;
    private float stateTime;
    private float scale;
    private float y;

    public ConveyorBelt(String atlasPath, float scale, float yOffset, Viewport viewport) {
        this.scale = scale;
        this.y = yOffset;
        this.sprites = new ArrayList<>();

        atlas = new TextureAtlas(atlasPath);
        animation = new Animation<>(0.1f, atlas.findRegions("image"), Animation.PlayMode.LOOP);

        TextureRegion firstFrame = animation.getKeyFrame(0);
        float scaledWidth = firstFrame.getRegionWidth() * scale;

        for (float x = 0f; x < viewport.getWorldWidth() + scaledWidth; x += scaledWidth) {
            Sprite conveyor = new Sprite(firstFrame);
            conveyor.setScale(scale);
            conveyor.setPosition(x, y);
            conveyor.flip(true, false); // pre-flip once
            sprites.add(conveyor);
        }

        stateTime = 0f;
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion region = animation.getKeyFrame(stateTime, true);
        for (Sprite sprite : sprites) {
            sprite.setRegion(region);
            sprite.flip(true, false); // Flip the conveyor
            sprite.draw(batch);
        }
    }

    public void dispose() {
        atlas.dispose();
    }

}
