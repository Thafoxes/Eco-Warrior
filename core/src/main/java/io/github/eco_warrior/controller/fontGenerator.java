package io.github.eco_warrior.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class fontGenerator {
    private BitmapFont font;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    private SpriteBatch uiBatch;

    public fontGenerator() {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/"));
    }
}
