package io.github.eco_warrior.sprite.tools;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.tool;

public class PipeWrench extends tool {

    public PipeWrench(Vector2 position, float scale) {
        super(
            "atlas/tools/tools.atlas",
            "pipe_wrenchx32",
            position,
            scale,
            "sound_effects/socket_wrench.mp3");
    }

}
