package io.github.eco_warrior.sprite.gardening_equipments;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Tool;

public class RayGun extends Tool {
    public enum RayGunMode {
        USELESS, BLAZING, BREEZING, ICE, VOLTAIC
    }

    private RayGunMode mode = RayGunMode.USELESS;

    public RayGun(Vector2 position, float scale) {
        // The base region name and frame count, just like WateringCan
        super(
            "atlas/ray_gun/ray_gun.atlas",
            "ray_gun", // You'd need to update your atlas to use: ray_gun, ray_gun_1, ray_gun_2, etc.
            5, // one for each mode
            position,
            scale
        );
    }

    public void setMode(RayGunMode mode) {
        this.mode = mode;
        setFrame(mode.ordinal());
    }

    public RayGunMode getMode() {
        return mode;
    }
}
