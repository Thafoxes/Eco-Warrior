package io.github.eco_warrior.sprite.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class UpgradePotionButton extends PurchaseButton {

    public UpgradePotionButton(Vector2 position, float scale) {
        super("atlas/purchase_buttons/buttons.atlas",
            "potent_potion_unpress",
            1,
            position,
            scale,
            3);
    }

    @Override
    protected void loadAnimation() {
        // Load animations directly here
        atlas = new TextureAtlas(Gdx.files.internal(filePath));

        animationMap.put(ButtonStage.NOT_PRESSED, new Animation<>(0f, atlas.findRegions("potent_potion_unpress"), Animation.PlayMode.NORMAL));
        animationMap.put(ButtonStage.PRESSED, new Animation<>(0f, atlas.findRegions("potent_potion_press"), Animation.PlayMode.NORMAL));
    }

    @Override
    protected void loadAudio() throws RuntimeException {
        this.clickSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/Stone_button_unpress.mp3"));
    }
}
