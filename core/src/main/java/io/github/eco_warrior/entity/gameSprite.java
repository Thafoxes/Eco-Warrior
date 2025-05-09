package io.github.eco_warrior.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class gameSprite extends AbsBin {
    private Sprite sprite;
    private Rectangle collisionRect;
    private Sound correctSoundFX;
    private Sound wrongSoundFx;
    private Sound hittingSFX;
    private boolean soundPlayed = false;
    private String atlasPath;
    private float scale = 1f;

    //debug mthod
    private ShapeRenderer shapeRenderer;


    public gameSprite(String atlasPath, String regionName, Vector2 position, float scale , String correctSoundPath, String wrongSoundPath, String hittingSoundPath) {
        this.atlasPath = atlasPath;
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(this.atlasPath));
        TextureRegion region;
        try {
            region = new TextureRegion(atlas.findRegion(regionName));
        }catch (Exception e) {
            throw new NullPointerException("sprite is null");
        }

        this.sprite = new Sprite(region);
        this.scale = scale;
        this.sprite.setSize(this.sprite.getWidth() * this.scale, this.sprite.getHeight() * this.scale);
        this.sprite.setPosition(position.x, position.y);
        this.collisionRect =  new Rectangle(position.x, position.y, sprite.getWidth(), sprite.getHeight());

        if(correctSoundPath != null){
            this.correctSoundFX = Gdx.audio.newSound(Gdx.files.internal(correctSoundPath));
        }
        if(wrongSoundPath != null){
            this.wrongSoundFx = Gdx.audio.newSound(Gdx.files.internal(wrongSoundPath));
        }
        if(hittingSoundPath != null){
            this.hittingSFX = Gdx.audio.newSound(Gdx.files.internal(hittingSoundPath));
        }

        //debug method
        shapeRenderer = new ShapeRenderer();
    }

    public gameSprite(String atlasPath, String regionName, Vector2 position , String correctSoundPath, String wrongSoundPath) {
       this(atlasPath, regionName, position, 1f, correctSoundPath, wrongSoundPath, null);
    }

    public gameSprite(String atlasPath, String regionName, Vector2 position) {
        this(atlasPath, regionName, position, 1f, null, null, null);
    }
    public gameSprite(String atlasPath, String regionName, Vector2 position, float scale) {
        this(atlasPath, regionName, position, scale, null, null, null);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        if (collisionRect != null) {
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(
                collisionRect.x,
                collisionRect.y,
                collisionRect.width,
                collisionRect.height
            );
        }
    }

    public void update(float delta){
        this.sprite.setPosition(this.collisionRect.x, this.collisionRect.y);
    }

    public void draw(SpriteBatch batch){
        this.sprite.draw(batch);
    }

    public boolean checkCollision(Rectangle rect){
        return this.collisionRect.overlaps(rect);
    }

    public boolean isPressed(Vector2 cursorPosition){
        if(this.collisionRect.contains(cursorPosition)){
            hittingSFX.play();

            return true;
        }
        return false;
    }

    public void playCorrectSound(){
        if(correctSoundFX != null){
            correctSoundFX.play();

        }
//        soundPlayed = true;

    }

    public void playWrongSound(){

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
            return this.correctSoundFX;

        }
        throw new NullPointerException("correctSoundFX is null");
    }

    public Sound getWrongSoundFx() {
        if(wrongSoundFx != null) return this.wrongSoundFx;
        throw new NullPointerException("wrongSoundFx is null");
    }

    public Sound getHittingSFX() {
       if (hittingSFX != null) return this.hittingSFX;
       throw new NullPointerException("hittingSFX is null");
    }

    public float getScale() {
        return this.scale;
    }

    public boolean isOffScreen() {
        return this.sprite.getX() + this.sprite.getWidth() < 0;
    }

    public float getMidX(){
        return this.sprite.getWidth() / 2;
    }

    public float getMidY(){
        return this.sprite.getHeight() / 2;
    }


}
