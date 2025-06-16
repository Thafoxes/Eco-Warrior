package io.github.eco_warrior.controller.Sapling;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.tool;
import io.github.eco_warrior.enums.SaplingType;

public class BaseSaplingController extends tool {


    private SaplingType saplingType;

    /**
     * Constructor for BaseSapling.
     *  If you want to create your own method of sampling, you can extend this class. Dont do it from tool.
     * @param position The position of the sapling in the game world.
     * @param scale The scale factor for the sapling sprite.
     */
    public BaseSaplingController(Vector2 position, String regionName, float scale, SaplingType saplingType) {
        super(
            "atlas/saplings/saplings.atlas",
            regionName,
            position,
            scale,
            null);
        this.saplingType = saplingType;
    }

    public SaplingType getSaplingType() {
        return saplingType;
    }

    public Rectangle getCollisionRect() {
        return new Rectangle(
            getPosition().x - getSprite().getWidth() / 2,
            getPosition().y - getSprite().getHeight() / 2,
            getSprite().getWidth(),
            getSprite(). getHeight()
        );
    }
}
