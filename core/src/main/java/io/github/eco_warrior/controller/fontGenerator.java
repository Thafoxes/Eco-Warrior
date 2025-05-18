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

public class fontGenerator {
    private BitmapFont font;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private Color fontColor = Color.BLACK;
    private Color borderColor = Color.BLACK;

    private SpriteBatch uiBatch;

    public fontGenerator(Integer size,Color fontColor, Color borderColor, String fontPath) {
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

    public fontGenerator(Integer size,Color fontColor, Color borderColor) {
        if(fontColor != null){
            this.fontColor = fontColor;
        }
        if(borderColor != null){
            this.borderColor = borderColor;
        }

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Square One.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = size;


        parameter.color = this.fontColor;
        parameter.borderWidth = 1;
        parameter.borderColor = this.borderColor;

        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;

        font = generator.generateFont(parameter);

    }

    public fontGenerator() {
        int size = 32;
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Square One.ttf"));
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

    public void dispose(){
        generator.dispose();
        font.dispose();

    }


}
