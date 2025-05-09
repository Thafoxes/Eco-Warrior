package io.github.eco_warrior.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class gameSprite {
    private Sprite sprite;
    private Rectangle collisionRect;
    private Sound correctSoundFX;
    private Sound wrongSoundFx;
    private Sound hittingSFX;
    private boolean soundPlayed = false;
    private String atlasPath;
    private float scale = 1f;



    public gameSprite(String atlasPath, String regionName, Vector2 position, float scale , String correctSoundPath, String wrongSoundPath, String hittingSoundPath) {
        this.atlasPath = atlasPath;
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(this.atlasPath));
        this.sprite = atlas.createSprite(regionName);
        if(sprite == null){
            throw new NullPointerException("sprite is null");
        }

        this.scale = scale;
        sprite.setScale(this.scale);
        sprite.setPosition(position.x, position.y);
        this.collisionRect =  new Rectangle(position.x, position.y, sprite.getWidth() * scale, sprite.getHeight() * scale);
        if(!correctSoundPath.equals("") || correctSoundPath != null){
            this.correctSoundFX = Gdx.audio.newSound(Gdx.files.internal(correctSoundPath));
        }
        if(!wrongSoundPath.equals("") || wrongSoundPath != null){
            this.wrongSoundFx = Gdx.audio.newSound(Gdx.files.internal(wrongSoundPath));
        }
        if(!hittingSoundPath.equals("") || hittingSoundPath != null){
            this.hittingSFX = Gdx.audio.newSound(Gdx.files.internal(hittingSoundPath));
        }
    }

    public gameSprite(String atlasPath, String regionName, Vector2 position , String correctSoundPath, String wrongSoundPath) {
       new gameSprite(atlasPath, regionName, position, 1f, correctSoundPath, wrongSoundPath, null);
    }

    public gameSprite(String atlasPath, String regionName, Vector2 position) {
        new gameSprite(atlasPath, regionName, position, 1f, null, null, null);
    }
    public gameSprite(String atlasPath, String regionName, Vector2 position, float scale) {
        new gameSprite(atlasPath, regionName, position, scale, null, null, null);
    }


    public void update(float delta){
        sprite.setPosition(collisionRect.x, collisionRect.y);
    }

    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    public boolean checkCollision(Rectangle rect){
        return rect.overlaps(rect);
    }

    public boolean isPressed(Vector2 cursorPosition){
        if(this.collisionRect.contains(cursorPosition)){
            hittingSFX.play();

            return true;
        }
        return false;
    }

    public void correctSound(){
        if(correctSoundFX != null){
            correctSoundFX.play();

        }
//        soundPlayed = true;

    }

    public void wrongSound(){

        if (wrongSoundFx != null) {
            wrongSoundFx.play();
        }
//        soundPlayed = true;

    }

    public void resetSound(){
//        soundPlayed = false;

    }

    public Rectangle getCollisionRect(){
        return this.collisionRect;
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    public void dispose(){
        wrongSoundFx.dispose();
        correctSoundFX.dispose();
        hittingSFX.dispose();
    }

    public Sound getCorrectSoundFX() {
        if(correctSoundFX != null){
            return correctSoundFX;

        }
        throw new NullPointerException("correctSoundFX is null");
    }

    public Sound getWrongSoundFx() {
        if(wrongSoundFx != null) return wrongSoundFx;
        throw new NullPointerException("wrongSoundFx is null");
    }

    public Sound getHittingSFX() {
       if (hittingSFX != null) return hittingSFX;
       throw new NullPointerException("hittingSFX is null");
    }

    public float getScale() {
        return scale;
    }
}
