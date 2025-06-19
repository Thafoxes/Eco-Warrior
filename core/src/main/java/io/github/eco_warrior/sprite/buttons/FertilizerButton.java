package io.github.eco_warrior.sprite.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class FertilizerButton extends PurchaseButton {

    public FertilizerButton(Vector2 position, float scale) {
        super("atlas/purchase_buttons/buttons.atlas",
            "fertilizer_button_unpress",
            1,
            position,
            scale,
            4);
    }

    @Override
    protected void loadAnimation() {
        // Load animations directly here
        atlas = new TextureAtlas(Gdx.files.internal(filePath));

        animationMap.put(ButtonStage.NOT_PRESSED, new Animation<>(0f, atlas.findRegions("fertilizer_button_unpress"), Animation.PlayMode.NORMAL));
        animationMap.put(ButtonStage.PRESSED, new Animation<>(0f, atlas.findRegions("fertilizer_button_press"), Animation.PlayMode.NORMAL));
    }

    @Override
    protected void loadAudio() throws RuntimeException {
        this.clickSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/Stone_button_unpress.mp3"));
    }
}
