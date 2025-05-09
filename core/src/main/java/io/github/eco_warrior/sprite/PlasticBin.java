package io.github.eco_warrior.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

public class PlasticBin extends WasteBin {


    private ERecycleMap acceptingMaterial = ERecycleMap.plastic_bottle;
    public PlasticBin(Vector2 pos) {
        super(
            "sprite/bins/bins.atlas",
            "plastic_bin",
            pos,
            5f ,
            "sound_effects/correct.mp3",
            "sound_effects/wrong.mp3",
            "sound_effects/hitting_bin.mp3"
            );
    }



}
