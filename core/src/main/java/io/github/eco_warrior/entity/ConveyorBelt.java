package io.github.eco_warrior.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class ConveyorBelt {
    private final TextureAtlas atlas;
    private final Animation<TextureRegion> animation;
    private ArrayList<Sprite> sprites;
    private float stateTime;
    private final float scale;
    private final float y;
    private boolean isAnimating = true;


    public ConveyorBelt(String atlasPath, String region, float scale, float yOffset, Viewport viewport) {
        this.scale = scale; //place here just in case I need to use it
        this.y = yOffset;
        this.sprites = new ArrayList<>();

        atlas = new TextureAtlas(atlasPath);
        animation = new Animation<>(0.1f, atlas.findRegions(region), Animation.PlayMode.LOOP);

        TextureRegion firstFrame = animation.getKeyFrame(0);
        float scaledWidth = firstFrame.getRegionWidth() * scale;

        expandConveyor(scale, viewport, scaledWidth, firstFrame);

        stateTime = 0f;
    }


    public ConveyorBelt(float scale, float yOffset, Viewport viewport) {
        this("atlas/conveyor/conveyor_belt.atlas",
            "belt",
            scale,
            yOffset,
            viewport);
    }

    private void expandConveyor(float scale, Viewport viewport, float scaledWidth, TextureRegion firstFrame) {
        for (float x = viewport.getScreenX() - 300f; x < viewport.getWorldWidth() + scaledWidth; x += scaledWidth) {
            Sprite conveyor = new Sprite(firstFrame);
            conveyor.setScale(scale);
            conveyor.setPosition(x, y);
            sprites.add(conveyor);
        }
    }


    public void update(float delta) {
        if(isAnimating){
            stateTime += delta;

        }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion region = animation.getKeyFrame(stateTime, true);
        for (Sprite sprite : sprites) {
            sprite.setRegion(region);
            sprite.flip(true, false); // Flip the conveyor
            sprite.draw(batch);
        }
    }

    public void stopAnimation() {
        isAnimating = false;
    }

    public void startAnimation() {
        isAnimating = true;
    }

    public void dispose() {
        atlas.dispose();
    }

}
