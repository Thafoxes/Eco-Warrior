package io.github.eco_warrior.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ERecycleMap;

public class BlueBin extends WasteBin {

    private ERecycleMap acceptingMaterial = ERecycleMap.newspaper;
    public BlueBin(Vector2 pos) {
        super(
            "sprite/bins/bins.atlas",
            "paper_bin",
            pos,
            5f ,
            "sound_effects/correct.mp3",
            "sound_effects/wrong.mp3",
            "sound_effects/hitting_bin.mp3"
            );
    }


    @Override
    public boolean isCorrectCategory(ERecycleMap categoryPile){
        if(acceptingMaterial.equals(categoryPile)){
            return true;
        }else{
            return false;
        }
    }
}
