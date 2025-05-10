package io.github.eco_warrior;

import com.badlogic.gdx.Game;
import io.github.eco_warrior.controller.ButtonFactory;
import io.github.eco_warrior.screen.ResultScreen;

import java.awt.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private ButtonFactory buttonFactory;
    @Override
    public void create() {
        buttonFactory = new ButtonFactory();
        setScreen(new FirstLevelScreen(this));
    }

    public ButtonFactory getButtonFactory() {
        return buttonFactory;
    }
}
