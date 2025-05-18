package io.github.eco_warrior.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicManager {
    private static MusicManager instance; // Singleton instance
    private Music backgroundMusic;
    private float musicVolume = 0.5f; // Default music volume

    public MusicManager() {
        // Load the background music (only once)
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Background_Music/Menu.mp3"));
        backgroundMusic.setLooping(true); // Make the music loop
        backgroundMusic.setVolume(musicVolume); // Set initial volume
    }

    // Get the singleton instance
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    // Start playing the music
    public void playMusic() {
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    // Pause the music
    public void pauseMusic() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    // Stop the music
    public void stopMusic() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    // Set the music volume
    public void setMusicVolume(float volume) {
        musicVolume = volume;
        backgroundMusic.setVolume(musicVolume);
    }

    // Get the current music volume
    public float getMusicVolume() {
        return musicVolume;
    }

    // Dispose the music when no longer needed
    public void dispose() {
        backgroundMusic.dispose();
    }
}
