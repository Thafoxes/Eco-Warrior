package io.github.eco_warrior;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.box2d.World;
import io.github.eco_warrior.controller.buttonGenerator;
import io.github.eco_warrior.mainmenu.MainMenuScreen;
import io.github.eco_warrior.screen.instructions.L1Instructions;
import io.github.eco_warrior.screen.instructions.L3Instructions;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private buttonGenerator buttonGenerator;
    private static int level = 1;
    @Override
    public void create() {
        buttonGenerator = new buttonGenerator();
        setScreen(new LevelTwoScreen(this));
    }

    public buttonGenerator getButtonFactory() {
        return buttonGenerator;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        Main.level = level;
    }
}
