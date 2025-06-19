package io.github.eco_warrior.sprite.gardening_equipments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Tool;

public class RayGun extends Tool {
    public enum RayGunMode {
        USELESS, BLAZING, BREEZING, ICE, VOLTAIC
    }

    private RayGunMode mode = RayGunMode.USELESS;
    private final Sound blazingSound;
    private final Sound breezingSound;
    private final Sound iceSound;
    private final Sound voltaicSound;
    private final Sound uselessSound; // Optional, if you want a sound for USELESS mode

    public RayGun(Vector2 position, float scale) {
        // The base region name and frame count, just like WateringCan
        super(
            "atlas/ray_gun/ray_gun.atlas",
            "ray_gun", // You'd need to update your atlas to use: ray_gun, ray_gun_1, ray_gun_2, etc.
            5, // one for each mode
            position,
            scale
        );
        blazingSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/fire_gun.mp3"));
        breezingSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/wind_gun.mp3"));
        iceSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/ice_gun.mp3"));
        voltaicSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/lightning_gun.mp3"));
        uselessSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/useless_gun.mp3")); // Optional
    }

    public void setMode(RayGunMode mode) {
        this.mode = mode;
        setFrame(mode.ordinal());
    }

    public RayGunMode getMode() {
        return mode;
    }

    public void playModeSound() {
        switch (mode) {
            case BLAZING:
                if (blazingSound != null) blazingSound.play();
                break;
            case BREEZING:
                if (breezingSound != null) breezingSound.play();
                break;
            case ICE:
                if (iceSound != null) iceSound.play();
                break;
            case VOLTAIC:
                if (voltaicSound != null) voltaicSound.play();
                break;
            default:
                uselessSound.play();
                break;
        }
    }

    public void dispose() {
        if (blazingSound != null) blazingSound.dispose();
        if (breezingSound != null) breezingSound.dispose();
        if (iceSound != null) iceSound.dispose();
        if (voltaicSound != null) voltaicSound.dispose();
        if (uselessSound != null) uselessSound.dispose();
        super.dispose();
    }

}
