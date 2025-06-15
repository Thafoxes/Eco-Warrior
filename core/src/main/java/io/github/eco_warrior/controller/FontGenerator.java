package io.github.eco_warrior.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.enums.textEnum;

import java.util.HashMap;
import java.util.Map;

public class FontGenerator {
    private BitmapFont font;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private Color fontColor = Color.BLACK;
    private Color borderColor = Color.BLACK;
    private Map<Integer, BitmapFont> fontCache = new HashMap<>();


    private SpriteBatch uiBatch;

    public FontGenerator(Integer size, Color fontColor, Color borderColor, String fontPath) {
        if(fontColor != null){
            this.fontColor = fontColor;
        }
        if(borderColor != null){
            this.borderColor = borderColor;
        }

        generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = size;


        parameter.color = this.fontColor;
        parameter.borderWidth = 1;
        parameter.borderColor = this.borderColor;

        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;

        font = generator.generateFont(parameter);

    }

    /**
     * Default font generator with size, font color, and border color.
     * @param size
     * @param fontColor
     * @param borderColor
     */
    public FontGenerator(Integer size, Color fontColor, Color borderColor) {
        if(fontColor != null){
            this.fontColor = fontColor;
        }
        if(borderColor != null){
            this.borderColor = borderColor;
        }

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/TT Octosquares Trial Medium.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = size;


        parameter.color = this.fontColor;
        parameter.borderWidth = 1;
        parameter.borderColor = this.borderColor;

        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;

        font = generator.generateFont(parameter);

    }

    /**
     * Default font generator with size 32, black font color, and black border color.
     */
    public FontGenerator() {
        int size = 32;
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/TT Octosquares Trial Medium.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = size;


        parameter.color = this.fontColor;
        parameter.borderWidth = 1;
        parameter.borderColor = this.borderColor;

        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;

        font = generator.generateFont(parameter);

    }

    public BitmapFont getFont() {
        return font;
    }

    /***
     * Draws the text with WORLD alignment and border options.
     * Object position alignment won't work nicely with this method.
     * @param uiBatch The SpriteBatch used for drawing.
     * @param text The text to draw.
     * @param camera The camera used for projection.
     * @param position The position to draw the text at.
     * @param alignText The alignment of the text (LEFT, RIGHT, X_CENTER).
     * @param borderText The border position of the text (TOP, BOTTOM, Y_MIDDLE).
     *
     * @param uiBatch
     * @param text
     * @param camera
     * @param position
     * @param alignText
     * @param borderText
     */
    public void fontDraw(SpriteBatch uiBatch, String text , OrthographicCamera camera, Vector2 position, textEnum alignText, textEnum borderText){
        GlyphLayout layout = new GlyphLayout(font, text);
        uiBatch.begin();
        uiBatch.setProjectionMatrix(camera.combined);

        switch(alignText){
            case RIGHT:
                position.x -= layout.width + 10f;
                break;
            case X_CENTER:
                position.x = (position.x - layout.width) / 2f;
                break;
            case LEFT:
                position.x = 10f;
                break;
            default:
                break;

        }
        switch(borderText){
            case TOP:
                position.y -= layout.height + 10f;
                break;
            case BOTTOM:
                position.y = layout.height + 10f;
                break;
            case Y_MIDDLE:
                position.y =  (position.y - layout.height)/ 2f;
                break;
            default:
                break;

        }

        font.draw(uiBatch, text , position.x, position.y);

        uiBatch.end();
    }

    /***
     *
     * @param uiBatch
     * @param text
     * @param camera
     * @param position
     * no need align just position
     */
    public void fontDraw(SpriteBatch uiBatch, String text , OrthographicCamera camera, Vector2 position){
        GlyphLayout layout = new GlyphLayout(font, text);
        uiBatch.begin();
        uiBatch.setProjectionMatrix(camera.combined);

        position.x = position.x - layout.width;


        font.draw(uiBatch, text , position.x, position.y);

        uiBatch.end();
    }

    /***
     * for object font draw, it will start drawing from the left bottom corner
     * @param uiBatch
     * @param text
     * @param camera
     * @param position
     */
    public void objFontDraw(SpriteBatch uiBatch, String text , OrthographicCamera camera, Vector2 position){
        uiBatch.begin();
        uiBatch.setProjectionMatrix(camera.combined);


        font.draw(uiBatch, text , position.x, position.y);

        uiBatch.end();
    }

    /***
     * Custom font size, the text will be drawn from the left bottom corner. adjust
     * the position accordingly.
     * Example:
     * bin.getSprite().getX() + bin.getSprite().getWidth() / 2f;
     * @param uiBatch
     * @param text
     * @param camera
     * @param position
     */
    public void objFontDraw(SpriteBatch uiBatch, String text , int size, OrthographicCamera camera, Vector2 position){
        if (!fontCache.containsKey(size)) {
            // Only generate a new font if we don't have this size cached
            parameter.size = size;
            BitmapFont newFont = generator.generateFont(parameter);
            fontCache.put(size, newFont);
        }

        // Use the cached font
        BitmapFont currentFont = fontCache.get(size);
        // Store the current font
        BitmapFont oldFont = font;
        font = currentFont;
        position.x = position.x - new GlyphLayout(font, text).width/2; // Center the text horizontally

        objFontDraw(uiBatch, text , camera, position);

        font = oldFont;
    }



    public void dispose(){
        generator.dispose();
        font.dispose();

    }


}
