package io.github.eco_warrior.sprite.UI;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.controller.FontGenerator;

public class Currency {

    private int moneyAmount;
    private float displayedMoney = 0f;

    private TextureAtlas atlas;
    private TextureRegion coinRegion;
    private FontGenerator fontGenerator;
    private Vector2 position;
    private float scale = 0.5f;
    private OrthographicCamera camera;

    private float scaleEffect = 1f;
    private float scaleTimer = 0f;

    private Sound coinSound;

    public Currency(Vector2 position, float scale, OrthographicCamera camera) {
        this.moneyAmount = 0;
        this.position = position;
        this.scale = scale;
        this.camera = camera;

        this.atlas = new TextureAtlas("atlas/coin/robux.atlas");
        // Use the first region or a named region, e.g., "coin_pixel"
        this.coinRegion = atlas.findRegion("robux");
        this.fontGenerator = new FontGenerator(24, Color.WHITE, Color.BLACK);
        this.coinSound = com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("sound_effects/orb.mp3"));
    }

    public void update(float delta) {
        // Update pop effect scale
        if (scaleTimer > 0) {
            scaleTimer -= delta;
            scaleEffect = 1f + (scaleTimer / 0.3f) * 0.5f;
        } else {
            scaleEffect = 1f;
        }

        // Smooth number interpolation
        displayedMoney = moneyAmount;
    }

    public void draw(SpriteBatch batch) {
        float coinWidth = coinRegion.getRegionWidth() * scale * scaleEffect;
        float coinHeight = coinRegion.getRegionHeight() * scale * scaleEffect;

        batch.draw(coinRegion, position.x, position.y, coinWidth, coinHeight);

        Vector2 textPos = new Vector2(position.x + coinWidth + 70, position.y + coinHeight - 15); // Move text more right and up
        fontGenerator.fontDraw(batch, ((int) displayedMoney) + " $", camera, textPos);
    }

    public void addMoney(int amount) {
        this.moneyAmount += amount;
        this.scaleEffect = 1.5f;
        this.scaleTimer = 0.3f; // Reset pop duration
        if (coinSound != null) coinSound.play(.5f);

        System.out.println(moneyAmount);
    }

    public void spendMoney(int amount) {
        if (this.moneyAmount >= amount) {
            this.moneyAmount -= amount;
            this.scaleEffect = 1.5f;
            this.scaleTimer = 0.3f; // Reset pop duration
        }
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public void dispose() {
        if (atlas != null) atlas.dispose();
        if (coinSound != null) coinSound.dispose();
    }
}
