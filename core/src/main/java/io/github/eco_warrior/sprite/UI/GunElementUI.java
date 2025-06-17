package io.github.eco_warrior.sprite.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

public class GunElementUI {
    public enum ElementType { FIRE, WIND, LIGHTING, ICE }

    private static final int ELEMENT_COUNT = 4;
    private static final float ICON_SIZE = 80f;
    private static final float ICON_Y = 10f;
    private static final float ICON_X_START = 50f;
    private static final float ICON_X_STEP = 120f;
    private static final long HIDE_DURATION_MS = 5000; // 5 seconds

    private TextureAtlas atlas;
    private TextureRegion[] regions;
    private Rectangle[] bounds;
    private boolean[] visible;
    private long[] hideEndTime;

    // Animation fields for lighting, fire, wind, and ice
    private Array<TextureAtlas.AtlasRegion> lightingFrames;
    private float lightingFrameDuration = 0.15f;
    private float lightingAnimTime = 0f;

    private Array<TextureAtlas.AtlasRegion> fireFrames;
    private float fireFrameDuration = 0.15f;
    private float fireAnimTime = 0f;

    private Array<TextureAtlas.AtlasRegion> windFrames;
    private float windFrameDuration = 0.15f;
    private float windAnimTime = 0f;

    private Array<TextureAtlas.AtlasRegion> iceFrames;
    private float iceFrameDuration = 0.4f;
    private float iceAnimTime = 0f;

    public GunElementUI(String atlasPath, String lightingAtlasPath, String fireAtlasPath, String windAtlasPath, String iceAtlasPath) {
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        regions = new TextureRegion[ELEMENT_COUNT];
        bounds = new Rectangle[ELEMENT_COUNT];
        visible = new boolean[ELEMENT_COUNT];
        hideEndTime = new long[ELEMENT_COUNT];

        // FIRE animation
        TextureAtlas fireAtlas = new TextureAtlas(Gdx.files.internal(fireAtlasPath));
        fireFrames = new Array<>();
        for (int i = 1; ; i++) {
            TextureAtlas.AtlasRegion region = fireAtlas.findRegion("fire", i);
            if (region == null) break;
            fireFrames.add(region);
        }
        if (fireFrames.size == 0) {
            regions[0] = atlas.findRegion("fire element");
        }

        // WIND animation
        TextureAtlas windAtlas = new TextureAtlas(Gdx.files.internal(windAtlasPath));
        windFrames = new Array<>();
        for (int i = 1; ; i++) {
            String frameName = String.format("%04d", i); // 0001, 0002, ...
            TextureAtlas.AtlasRegion region = windAtlas.findRegion(frameName);
            if (region == null) break;
            windFrames.add(region);
        }
        if (windFrames.size == 0) {
            regions[1] = atlas.findRegion("wind element");
        }

        // LIGHTING animation
        TextureAtlas lightingAtlas = new TextureAtlas(Gdx.files.internal(lightingAtlasPath));
        lightingFrames = new Array<>();
        for (int i = 1; i <= 8; i++) {
            String frameName = String.format("%05d", i);
            TextureAtlas.AtlasRegion region = lightingAtlas.findRegion(frameName);
            if (region != null) {
                lightingFrames.add(region);
            }
        }
        if (lightingFrames.size == 0) {
            regions[2] = atlas.findRegion("lighting element");
        }

        // ICE animation
        TextureAtlas iceAtlas = new TextureAtlas(Gdx.files.internal(iceAtlasPath));
        iceFrames = new Array<>();
        for (int i = 1; ; i++) {
            String frameName = String.format("%04d", i); // 0001, 0002, ...
            TextureAtlas.AtlasRegion region = iceAtlas.findRegion(frameName);
            if (region == null) break;
            iceFrames.add(region);
        }
        if (iceFrames.size == 0) {
            regions[3] = atlas.findRegion("ice element");
        }

        for (int i = 0; i < ELEMENT_COUNT; i++) {
            float x = ICON_X_START + i * ICON_X_STEP;
            bounds[i] = new Rectangle(x, ICON_Y, ICON_SIZE, ICON_SIZE);
            visible[i] = true;
            hideEndTime[i] = 0;
        }
    }

    // Call in your render loop - draws only visible elements
    public void renderAll(Batch batch, float delta) {
        long now = TimeUtils.millis();
        lightingAnimTime += delta;
        fireAnimTime += delta;
        windAnimTime += delta;
        iceAnimTime += delta;
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            if (!visible[i] && now > hideEndTime[i]) {
                visible[i] = true;
            }
            if (visible[i]) {
                if (i == ElementType.LIGHTING.ordinal() && lightingFrames.size > 0) {
                    int frameIndex = (int)(lightingAnimTime / lightingFrameDuration) % lightingFrames.size;
                    TextureRegion frame = lightingFrames.get(frameIndex);
                    Rectangle r = bounds[i];
                    batch.draw(frame, r.x, r.y, r.width, r.height);
                } else if (i == ElementType.FIRE.ordinal() && fireFrames.size > 0) {
                    int frameIndex = (int)(fireAnimTime / fireFrameDuration) % fireFrames.size;
                    TextureRegion frame = fireFrames.get(frameIndex);
                    Rectangle r = bounds[i];
                    batch.draw(frame, r.x, r.y, r.width, r.height);
                } else if (i == ElementType.WIND.ordinal() && windFrames.size > 0) {
                    int frameIndex = (int)(windAnimTime / windFrameDuration) % windFrames.size;
                    TextureRegion frame = windFrames.get(frameIndex);
                    Rectangle r = bounds[i];
                    batch.draw(frame, r.x, r.y, r.width, r.height);
                } else if (i == ElementType.ICE.ordinal() && iceFrames.size > 0) {
                    int frameIndex = (int)(iceAnimTime / iceFrameDuration) % iceFrames.size;
                    TextureRegion frame = iceFrames.get(frameIndex);
                    Rectangle r = bounds[i];
                    batch.draw(frame, r.x, r.y, r.width, r.height);
                } else if (regions[i] != null) {
                    Rectangle r = bounds[i];
                    batch.draw(regions[i], r.x, r.y, r.width, r.height);
                }
            }
        }
    }

    public void renderElementIcon(Batch batch, ElementType type, float x, float y, float width, float height, float delta) {
        if (type == ElementType.LIGHTING && lightingFrames.size > 0) {
            lightingAnimTime += delta;
            int frameIndex = (int)(lightingAnimTime / lightingFrameDuration) % lightingFrames.size;
            TextureRegion frame = lightingFrames.get(frameIndex);
            batch.draw(frame, x, y, width, height);
        } else if (type == ElementType.FIRE && fireFrames.size > 0) {
            fireAnimTime += delta;
            int frameIndex = (int)(fireAnimTime / fireFrameDuration) % fireFrames.size;
            TextureRegion frame = fireFrames.get(frameIndex);
            batch.draw(frame, x, y, width, height);
        } else if (type == ElementType.WIND && windFrames.size > 0) {
            windAnimTime += delta;
            int frameIndex = (int)(windAnimTime / windFrameDuration) % windFrames.size;
            TextureRegion frame = windFrames.get(frameIndex);
            batch.draw(frame, x, y, width, height);
        } else if (type == ElementType.ICE && iceFrames.size > 0) {
            iceAnimTime += delta;
            int frameIndex = (int)(iceAnimTime / iceFrameDuration) % iceFrames.size;
            TextureRegion frame = iceFrames.get(frameIndex);
            batch.draw(frame, x, y, width, height);
        } else {
            int idx = type.ordinal();
            if (regions[idx] != null) {
                batch.draw(regions[idx], x, y, width, height);
            }
        }
    }

    // Overload for backward compatibility (static elements)
    public void renderElementIcon(Batch batch, ElementType type, float x, float y, float width, float height) {
        renderElementIcon(batch, type, x, y, width, height, 0f);
    }

    public void handleClick(float screenX, float screenY) {
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            if (visible[i] && bounds[i].contains(screenX, screenY)) {
                visible[i] = false;
                hideEndTime[i] = TimeUtils.millis() + HIDE_DURATION_MS;
                break;
            }
        }
    }

    public void dispose() {
        atlas.dispose();
    }
}
