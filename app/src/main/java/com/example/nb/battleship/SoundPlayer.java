package com.example.nb.battleship;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundPlayer {
    private static SoundPool soundPool = null;
    private int explodeSound;
    private int missSound;
    private int radarSound;
    private Context context;

    public SoundPlayer(Context context) {
        this.context = context;
        createSoundPool();
        loadSounds();
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }

    public void playExplodeSound(){
        soundPool.play(explodeSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playMissSound(){
        soundPool.play(missSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playRadarSound(){
        soundPool.play(radarSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void createSoundPool() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    }

    public void loadSounds(){
        explodeSound = soundPool.load(context, R.raw.explode_small, 0);
        missSound = soundPool.load(context, R.raw.miss_beep, 0);
        radarSound = soundPool.load(context, R.raw.radar, 0);
    }



}
