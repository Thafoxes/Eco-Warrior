package io.github.eco_warrior.mainmenu;

public class SettingsManager {
    private static SettingsManager instance; // Singleton instance
    private float musicVolume = 0.5f; // Default music volume
    private float clickSoundVolume = 0.5f; // Default click sound volume

    // Private constructor to enforce singleton pattern
    private SettingsManager() {}

    // Method to get the singleton instance
    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    // Getter and setter for music volume
    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    // Getter and setter for click sound volume
    public float getClickSoundVolume() {
        return clickSoundVolume;
    }

    public void setClickSoundVolume(float clickSoundVolume) {
        this.clickSoundVolume = clickSoundVolume;
    }
}
