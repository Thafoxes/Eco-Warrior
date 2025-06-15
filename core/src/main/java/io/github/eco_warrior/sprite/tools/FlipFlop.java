package io.github.eco_warrior.sprite.tools;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;

public class FlipFlop extends gameSprite {

    private static String slapSoundEffectPath = "sound_effects/hard_slap.mp3";

    public FlipFlop(Vector2 position) {
        super(
            "atlas/flip_flop/flip-flop.atlas",
            "flip-flop",
            position,
            3f,
            slapSoundEffectPath
        );
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        // Dispose of any additional resources if needed
    }

}
