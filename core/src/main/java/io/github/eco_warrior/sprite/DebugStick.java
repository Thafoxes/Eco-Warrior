package io.github.eco_warrior.sprite;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Tool;

public class DebugStick extends Tool {

    public DebugStick(Vector2 position, float scale) {
        super(
            "atlas/debug_stick/debug_stick.atlas",
            "Debug_Stick",
            position,
            scale);
    }
}
