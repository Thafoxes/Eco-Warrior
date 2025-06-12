package io.github.eco_warrior.sprite.gardening_equipments.sapling_variant;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.tool;

public class BaseSapling extends tool {

    /**
     * Constructor for BaseSapling.
     *  If you want to create your own method of sampling, you can extend this class. Dont do it from tool.
     * @param position The position of the sapling in the game world.
     * @param scale The scale factor for the sapling sprite.
     */
    public BaseSapling( Vector2 position, String regionName, float scale) {
        super(
            "atlas/saplings/saplings.atlas",
            regionName,
            position,
            scale,
            null);
    }
}
