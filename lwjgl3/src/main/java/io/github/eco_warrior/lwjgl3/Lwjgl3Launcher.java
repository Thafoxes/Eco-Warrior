package io.github.eco_warrior.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.eco_warrior.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {


        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("Uncaught exception in thread: " + thread.getName(), throwable);
        });

        // Rest of your application code
        try {
            if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
            createApplication();
            // Your application code here
        } catch (Exception e) {
            logger.error("Application error", e);
        }
    }

    private static void createApplication() {
        new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Eco Warrior");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        configuration.setWindowedMode(1280, 720);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("iconx128.png", "iconx64.png", "iconx32.png");
        return configuration;
    }
}
