package io.github.eco_warrior.sprite.tools;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Tool;

public class DuctTape extends Tool {

    public DuctTape(Vector2 position, float scale) {
        super(
            "atlas/tools/tools.atlas",
            "duct_tape",
            position,
            scale,
            "sound_effects/tape_tearing.mp3");
    }

}
