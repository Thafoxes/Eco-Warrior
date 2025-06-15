package io.github.eco_warrior.entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class Trees extends gameSprite {
    private final Sound digSound = Gdx.audio.newSound(Gdx.files.internal("sound_effects/Gravel_dig1.mp3"));;
    protected boolean isStageTransitionScheduled = false;
    public boolean isMatureTree = false;
    public int health = 4;
    public Vector2 adjustedPosition;

    public Trees(String atlasPath, String regionBaseName, int frameCount, Vector2 position, float scale) {
        super(atlasPath,
            regionBaseName,
            frameCount,
            position,
            scale);

        this.adjustedPosition = new Vector2(position.x + 30f, position.y - 20f);
    }

    public void diggingSound() {
        digSound.play();
    }

    public void treeObliteration () {}
}
