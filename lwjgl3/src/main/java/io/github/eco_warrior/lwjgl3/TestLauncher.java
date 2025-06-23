package io.github.eco_warrior.lwjgl3;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.eco_warrior.tests.BombPeckerTestScreen;

public class TestLauncher extends Game {
    @Override
    public void create() {
        setScreen(new BombPeckerTestScreen());
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Worm Visual Test");
        config.setWindowedMode(1280, 720);
        config.useVsync(true);
        config.setForegroundFPS(60);

        new Lwjgl3Application(new TestLauncher(), config);
    }

}
