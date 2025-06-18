package io.github.eco_warrior.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameSprite extends spriteGenerator {
    private Sprite sprite;
    private Rectangle collisionRect;
    private Sound correctSoundFX;
    private Sound wrongSoundFx;
    private Sound hittingSFX;
    private String atlasPath;
    private float scale = 1f;
    private Vector2 initPosition;

    //for manual frame
    private int frameCount;

    /**
     * No frame to worried about, just a single sprite. With audio effects.
     */
    public GameSprite(String atlasPath, String regionName, Vector2 position, float scale , String correctSoundPath, String wrongSoundPath, String hittingSoundPath) {
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
        this.initPosition = position;

        if(correctSoundPath != null){
            this.correctSoundFX = Gdx.audio.newSound(Gdx.files.internal(correctSoundPath));
        }
        if(wrongSoundPath != null){
            this.wrongSoundFx = Gdx.audio.newSound(Gdx.files.internal(wrongSoundPath));
        }
        if(hittingSoundPath != null){
            this.hittingSFX = Gdx.audio.newSound(Gdx.files.internal(hittingSoundPath));
        }


    }

    /**
     * No frame to worried about, just a single sprite. No sound effects.
     */
    public GameSprite(String atlasPath,  Vector2 position, float scale) {
        this(atlasPath, "", position, scale, null, null, null);
    }

    /**manual animation sprite design
     *
     * @param atlasPath
     * @param regionBaseName
     * @param frameCount
     * @param position
     * @param scale
     */
    public GameSprite(String atlasPath, String regionBaseName, int frameCount, Vector2 position, float scale) {

        this.frameCount = frameCount;
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(regionBaseName);
        if(regions == null || regions.size < frameCount){
            throw new NullPointerException("Not enough regions found for " + regionBaseName);
        }

        this.frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = regions.get(i);
        }

        this.currentFrameIndex = 0;
        this.sprite = new Sprite(frames[currentFrameIndex]);
        this.scale = scale;
        this.sprite.setSize(sprite.getWidth() * this.scale, sprite.getHeight() * this.scale);
        this.sprite.setPosition(position.x, position.y);
        this.initPosition = position;

        this.collisionRect = new Rectangle(position.x, position.y, sprite.getWidth(), sprite.getHeight());
    }

    /**
     * Default scale with correct and wrong sound effects.
     * */
    public GameSprite(String atlasPath, String regionName, Vector2 position , String correctSoundPath, String wrongSoundPath) {
       this(atlasPath, regionName, position, 1f, correctSoundPath, wrongSoundPath, null);
    }

    /**
     * Default scale with no sound effects.
     * */
    public GameSprite(String atlasPath, String regionName, Vector2 position) {
        this(atlasPath, regionName, position, 1f, null, null, null);
    }

    /**
     * Custom scale with no sound effects.
     * */
    public GameSprite(String atlasPath, String regionName, Vector2 position, float scale) {
        this(atlasPath, regionName, position, scale, null, null, null);
    }

    /**
     * Default scale with only correct sound effects.
     * */
    public GameSprite(String atlasPath, String regionName, Vector2 position, float scale, String correctSoundPath) {
        this(atlasPath, regionName, position, scale, correctSoundPath, null, null);
    }

    /**
     * Green collision hitbox, Red sprite hitbox
     * @param shapeRenderer
     */
    public void drawDebug(ShapeRenderer shapeRenderer) {
        if (sprite != null) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(
                sprite.getX(),
                sprite.getY(),
                sprite.getWidth(),
                sprite.getHeight()
            );
        }
    }

    /**
     * draw debug with custom color
     * @param shapeRenderer
     * @param debugColor
     */
    public void drawDebug(ShapeRenderer shapeRenderer, Color debugColor) {
        if (collisionRect != null) {
            shapeRenderer.setColor(debugColor);
            shapeRenderer.rect(
                collisionRect.x,
                collisionRect.y,
                collisionRect.width,
                collisionRect.height
            );
        }
    }

    /**
     for manual frame change index
     *
     */
    public void setFrame(int index){
        if (index >= 0 && index < frames.length) {
            currentFrameIndex = index;
            sprite.setRegion(frames[index]);
        }
    }

    /**
     * reset the frame back to 0
     */
    public void resetFrame(){
        currentFrameIndex = 0;
        sprite.setRegion(frames[currentFrameIndex]);
    }

    public void render(SpriteBatch batch) {
        // Draw the sprite
        this.sprite.draw(batch);
    }

    /**
     * for manual frame get index
     *
     */
    public int getCurrentFrame() {
        return currentFrameIndex;
    }

    public int getFrameCount() {
        return frames.length;
    }

    /**
     *  for manual frame shift to next frame.
     */
    public void nextFrame(){
        currentFrameIndex = ++currentFrameIndex % frameCount;
        sprite.setRegion(frames[currentFrameIndex]);
    }

    public void update(float delta){
        this.sprite.setPosition(this.collisionRect.x, this.collisionRect.y);
    }

    /**
     * Normal draw, if you want animation you have to custom design it yourself. Refer to WasteBin.java
     * @param batch
     */
    public void draw(SpriteBatch batch){
        this.sprite.draw(batch);
    }



    public boolean isPressed(Vector2 cursorPosition){
        if(this.collisionRect.contains(cursorPosition)){
            hittingSFX.play();

            return true;
        }
        return false;
    }

    public Vector2 getInitPosition(){
        return this.initPosition;
    }

    public void playCorrectSound(){
        if(correctSoundFX != null){
            correctSoundFX.play();

        }
//        soundPlayed = true;

    }

    public void playSound(){
        if(correctSoundFX != null){
            correctSoundFX.play();

        }

    }

    public void playWrongSound(){

        if (wrongSoundFx != null) {
            wrongSoundFx.play();
        }
//        soundPlayed = true;

    }

    public Vector2 getPosition(){
        Vector2 spritePosition = new Vector2(this.sprite.getX(), this.sprite.getY());
        return spritePosition;
    }

    public void setPosition(Vector2 newPosition){
        this.sprite.setPosition(newPosition.x, newPosition.y);
        this.collisionRect.setPosition(newPosition.x, newPosition.y);
    }

    public Rectangle getCollisionRect(){
        return this.collisionRect;
    }

    public Sprite getSprite(){
        return this.sprite;
    }

    public void dispose(){
        if(wrongSoundFx != null){
            wrongSoundFx.dispose();
        }
        if (correctSoundFX != null) {
            correctSoundFX.dispose();
        }
        if (hittingSFX != null) {
            hittingSFX.dispose();
        }
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
