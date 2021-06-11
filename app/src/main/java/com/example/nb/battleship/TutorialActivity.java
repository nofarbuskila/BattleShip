package com.example.nb.battleship;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nb.battleship.game_tutorial.Tab1Tutorial;
import com.example.nb.battleship.game_tutorial.Tab2Tutorial;
import com.example.nb.battleship.game_tutorial.Tab3Tutorial;
import com.example.nb.battleship.game_tutorial.Tab4Tutorial;

public class TutorialActivity extends AppCompatActivity {

    private static MediaPlayer musicBG;
    private boolean musicState;

    private SharedPreferences sp;
    private Toolbar toolbar;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        sp = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        musicState = sp.getBoolean("music", true);
        play();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Tab1Tutorial tab1 = new Tab1Tutorial();
                    return tab1;
                case 1:
                    Tab2Tutorial tab2 = new Tab2Tutorial();
                    return tab2;
                case 2:
                    Tab3Tutorial tab3 = new Tab3Tutorial();
                    return tab3;
                case 3:
                    Tab4Tutorial tab4 = new Tab4Tutorial();
                    return tab4;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }

    /*Menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tutorial_menu, menu);

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

}

