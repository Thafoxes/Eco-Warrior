package io.github.eco_warrior.sprite.Bins;

import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.ERecycleMap;

import java.util.ArrayList;
import java.util.Arrays;

public class CanBin extends BinBase {


    private ArrayList<ERecycleMap> acceptingMaterial = new ArrayList<>(
        Arrays.asList( ERecycleMap.tin_cans, ERecycleMap.cans)
    );
    public CanBin(Vector2 pos) {
        super(
            "metal_bin",
            pos,
            "sound_effects/correct.mp3",
            "sound_effects/wrong.mp3",
            "sound_effects/hitting_bin.mp3"
            );
    }

    @Override
    public String getBinType() {
        return "Metal";
    }

    @Override
    public boolean isCorrectCategory(ERecycleMap categoryPile){
        for(ERecycleMap m : acceptingMaterial){
            if(m.equals(categoryPile)){
                return true;
            }else{
                return false;
            }
        }
        return false;

    }
}
