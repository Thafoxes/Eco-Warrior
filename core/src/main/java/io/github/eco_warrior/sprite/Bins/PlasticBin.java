package io.github.eco_warrior.sprite.Bins;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.ERecycleMap;

public class PlasticBin extends BinBase {


    private ERecycleMap acceptingMaterial = ERecycleMap.plastic_bottle;
    public PlasticBin(Vector2 pos) {
        super(
            "plastic_bin",
            pos,
            "sound_effects/correct.mp3",
            "sound_effects/wrong.mp3",
            "sound_effects/hitting_bin.mp3"
            );
    }

    @Override
    public String getBinType() {
        return "Plastic";
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
