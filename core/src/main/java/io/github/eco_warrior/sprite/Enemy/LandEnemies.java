package io.github.eco_warrior.sprite.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import io.github.eco_warrior.entity.Trees;
import io.github.eco_warrior.entity.gameSprite;

public class LandEnemies extends gameSprite {

    public LandEnemies(String atlasPath, String regionBaseName, int frameCount, Vector2 position, float scale) {
        super(atlasPath,
            regionBaseName,
            frameCount,
            position,
            scale);
    }
}
