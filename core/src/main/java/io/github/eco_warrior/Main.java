package io.github.eco_warrior;

import com.badlogic.gdx.Game;
import io.github.eco_warrior.controller.buttonGenerator;
import io.github.eco_warrior.mainmenu.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private buttonGenerator buttonGenerator;
    @Override
    public void create() {
        buttonGenerator = new buttonGenerator();
        setScreen(new MainMenuScreen(this));
    }

    public buttonGenerator getButtonFactory() {
        return buttonGenerator;
    }
}
