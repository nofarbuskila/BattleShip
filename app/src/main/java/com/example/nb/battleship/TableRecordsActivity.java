package com.example.nb.battleship;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class TableRecordsActivity extends AppCompatActivity{

    private static MediaPlayer musicBG;
    private static boolean musicState;

    private SharedPreferences sp;
    private Toolbar toolbar;
    private ArrayList<Record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_records);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        musicState = sp.getBoolean("music", true);
        play();

        ListView listView = findViewById(R.id.list_records);

        loadTableRecords();
        if(records == null)
        {
            initialTableRecoreds();
        }

        RecordAdapter recordAdapter = new RecordAdapter(records, this);
        listView.setAdapter(recordAdapter);

    }

    private void loadTableRecords() {
        try {
            FileInputStream fis = openFileInput("records");
            ObjectInputStream ois = new ObjectInputStream(fis);

            records = (ArrayList<Record>) ois.readObject();

            ois.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*Menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.table_records_menu, menu);

        musicState = sp.getBoolean("music", true);
        if(musicState) // music on
            menu.getItem(1).setIcon(R.drawable.ic_music_on_white_24dp);
        else        //music off
            menu.getItem(1).setIcon(R.drawable.ic_music_off_white_24dp);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.main_menu:
                finish();
                return true;
            case R.id.music_menu:
                music(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void music(MenuItem item){
        if(musicState)
        {
            item.setIcon(R.drawable.ic_music_off_white_24dp);

        }
        else
            item.setIcon(R.drawable.ic_music_on_white_24dp);

        musicState = !musicState;
        play();

    }

    private void play() {
        if (musicBG == null)
        {
            musicBG = MediaPlayer.create(this, R.raw.music_babkground_game);
            musicBG.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });

            musicBG.setLooping(true);
        }

        if(musicState)
            musicBG.start();
        else
            musicBG.pause();
    }

    private void stopPlayer() {
        if (musicBG != null)
        {
            musicBG.release();
            musicBG = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("music", musicState);
        editor.commit();

        if(musicBG != null)
            musicBG.pause(); //in case we moved to another activity or suspend the app
    }

    @Override
    protected void onResume() {
        super.onResume();

        musicState = sp.getBoolean("music", true);
        play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(musicBG != null)
        {
            musicBG.stop();
            musicBG.release();
            musicBG = null;
        }
    }

    private void initialTableRecoreds(){
        records = new ArrayList<>();

        Record r1 = new Record(1, "Guest", 0, "00:00");
        Record r2 = new Record(2, "Guest", 0, "00:00");
        Record r3 = new Record(3, "Guest", 0, "00:00");
        Record r4 = new Record(4, "Guest", 0, "00:00");
        Record r5 = new Record(5, "Guest", 0, "00:00");

        records.add(r1);
        records.add(r2);
        records.add(r3);
        records.add(r4);
        records.add(r5);

        try {
            FileOutputStream fos = openFileOutput("records",MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(records);
            oos.flush();
            oos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
