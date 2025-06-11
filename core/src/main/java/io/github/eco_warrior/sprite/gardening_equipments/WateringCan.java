package io.github.eco_warrior.sprite.gardening_equipments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;

public class WateringCan extends gameSprite {

    public enum WateringCanState {
        EMPTY,
        FILLED
    }

    private final Sound fillSound1;
    private final Sound fillSound2;

    public int waterLevel = WateringCanState.EMPTY.ordinal();

    public WateringCan(Vector2 position, float scale) {
        super(
            "atlas/watering_can/watering_can.atlas",
            "watering_can",
            2,
            position,
            scale);

        fillSound1 = Gdx.audio.newSound(Gdx.files.internal("sound_effects/fill_watering_can1.mp3"));
        fillSound2 = Gdx.audio.newSound(Gdx.files.internal("sound_effects/fill_watering_can2.mp3"));
    }

    //update the watering can state when it interacts with a water fountain
    public void updateWateringCan(gameSprite waterFountain) {
        if (waterLevel == WateringCanState.EMPTY.ordinal() && getCollisionRect().overlaps(waterFountain.getCollisionRect())) {
            if (Math.random() < 0.5) {
                fillSound1.play(1f);
            } else {
                fillSound2.play(1f);
            }
            waterLevel = WateringCanState.FILLED.ordinal();

            setFrame(waterLevel);
        }
    }
}
