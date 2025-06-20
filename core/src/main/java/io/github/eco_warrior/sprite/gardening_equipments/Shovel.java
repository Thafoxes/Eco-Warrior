package io.github.eco_warrior.sprite.gardening_equipments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.Tool;

public class Shovel extends Tool {

    private final Sound shovelSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/pan.mp3"));

    public Shovel(Vector2 position, float scale) {
        super(
            "atlas/gardening_equipments/equipments.atlas",
            "shovel",
            position,
            scale);

        initializeAudio();
    }

    private void initializeAudio() {
        shovelSound.setLooping(0, false);
        shovelSound.setVolume(0, 0.5f);
    }

    public void playSound() {
        shovelSound.play(0.5f);
    }

}
