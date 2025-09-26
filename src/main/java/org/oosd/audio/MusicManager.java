package org.oosd.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.oosd.config.GameConfig;

import java.net.URL;

public class MusicManager {
    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private boolean isInitialized = false;

    private MusicManager() {
        initializeMusic();
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    private void initializeMusic() {
        try {
            URL musicResource = getClass().getResource("/audio/background.mp3");
            if (musicResource != null) {
                Media media = new Media(musicResource.toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setVolume(0.5);
                isInitialized = true;
                System.out.println("Music initialized successfully");
            } else {
                System.err.println("Could not find background.mp3 in resources/audio/");
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize music: " + e.getMessage());
        }
    }

    public void play() {
        if (isInitialized && mediaPlayer != null && GameConfig.getInstance().isMusicEnabled()) {
            try {
                mediaPlayer.play();
                System.out.println("Music started playing");
            } catch (Exception e) {
                System.err.println("Failed to play music: " + e.getMessage());
            }
        }
    }

    public void pause() {
        if (isInitialized && mediaPlayer != null) {
            try {
                mediaPlayer.pause();
                System.out.println("Music paused");
            } catch (Exception e) {
                System.err.println("Failed to pause music: " + e.getMessage());
            }
        }
    }

    public void stop() {
        if (isInitialized && mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                System.out.println("Music stopped");
            } catch (Exception e) {
                System.err.println("Failed to stop music: " + e.getMessage());
            }
        }
    }

    public void toggle() {
        if (isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    public boolean isPlaying() {
        return isInitialized && mediaPlayer != null &&
               mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public void setVolume(double volume) {
        if (isInitialized && mediaPlayer != null) {
            mediaPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume)));
        }
    }

    public void updateFromConfig() {
        GameConfig config = GameConfig.getInstance();
        if (config.isMusicEnabled()) {
            play();
        } else {
            stop();
        }
    }

    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
    }
}