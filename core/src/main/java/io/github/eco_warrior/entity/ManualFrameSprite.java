package io.github.eco_warrior.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * @deprecated Use current gameSprite for ManualFrameSprite
 */
@Deprecated
public class ManualFrameSprite{

    private TextureRegion[] frames;
    private Sprite sprite;
    private int currentIndex;


    public ManualFrameSprite(String atlasPath, String regionName, int frameCount, Vector2 position, float Scale) {

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        frames = new TextureRegion[frameCount];
        for(int i = 0; i < frameCount; i++){
           String frameName = String.format("%s%02d", regionName, i);
           frames[i] = atlas.findRegion(frameName);
           if(frames[i] == null){
               throw new NullPointerException("frames[" + i + "] is null" + frameName);
           }

        }

        currentIndex = 0;
        sprite = new Sprite(frames[currentIndex]);
        sprite.setSize(sprite.getWidth() * Scale, sprite.getHeight() * Scale);
        sprite.setPosition(position.x, position.y);



    }

    public void setFrame(int index){
        if(index < 0 || index >= frames.length){
            throw new IndexOutOfBoundsException("index is out of bound");
        }
        this.currentIndex = index;
        this.sprite.setRegion(frames[currentIndex]);
    }

    public void draw(SpriteBatch batch){
        this.sprite.draw(batch);
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    public int GetCurrentIndex() {
        return this.currentIndex;
    }

    public int getTotalFrames(){
        return this.frames.length;
    }
}
