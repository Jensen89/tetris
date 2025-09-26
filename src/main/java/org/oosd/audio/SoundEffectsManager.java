package org.oosd.audio;

import javafx.scene.media.AudioClip;
import org.oosd.config.GameConfig;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundEffectsManager {
    private static SoundEffectsManager instance;
    private Map<String, AudioClip> soundEffects;
    private boolean isInitialized = false;

    public enum SoundEffect {
        LINE_CLEAR("erase-line.wav"),
        PIECE_ROTATE("move-turn.wav");

        private final String filename;

        SoundEffect(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
        }
    }

    private SoundEffectsManager() {
        initializeSounds();
    }

    public static SoundEffectsManager getInstance() {
        if (instance == null) {
            instance = new SoundEffectsManager();
        }
        return instance;
    }

    private void initializeSounds() {
        soundEffects = new HashMap<>();

        try {
            for (SoundEffect effect : SoundEffect.values()) {
                URL soundResource = getClass().getResource("/audio/" + effect.getFilename());
                if (soundResource != null) {
                    AudioClip clip = new AudioClip(soundResource.toString());
                    clip.setVolume(0.7);
                    soundEffects.put(effect.name(), clip);
                    System.out.println("Loaded sound effect: " + effect.getFilename());
                } else {
                    System.err.println("Could not find sound file: " + effect.getFilename());
                }
            }
            isInitialized = true;
            System.out.println("Sound effects initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize sound effects: " + e.getMessage());
        }
    }

    public void playSound(SoundEffect effect) {
        if (!isInitialized || !GameConfig.getInstance().areSoundEffectsEnabled()) {
            return;
        }

        AudioClip clip = soundEffects.get(effect.name());
        if (clip != null) {
            try {
                clip.play();
            } catch (Exception e) {
                System.err.println("Failed to play sound effect " + effect.getFilename() + ": " + e.getMessage());
            }
        }
    }

    public void setVolume(double volume) {
        if (!isInitialized) return;

        double clampedVolume = Math.max(0.0, Math.min(1.0, volume));
        for (AudioClip clip : soundEffects.values()) {
            clip.setVolume(clampedVolume);
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}