package io.github.eco_warrior.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

import java.util.ArrayList;
import java.util.Arrays;

public class CanBin extends WasteBin {


    private ArrayList<ERecycleMap> acceptingMaterial = new ArrayList<>(
        Arrays.asList( ERecycleMap.tin_cans, ERecycleMap.cans)
    );
    public CanBin(Vector2 pos) {
        super(
            "sprite/bins/bins.atlas",
            "cans_bin",
            pos,
            5f ,
            "sound_effects/correct.mp3",
            "sound_effects/wrong.mp3",
            "sound_effects/hitting_bin.mp3"
            );
    }


}
