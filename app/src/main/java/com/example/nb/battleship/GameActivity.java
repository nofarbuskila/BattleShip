package com.example.nb.battleship;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nb.battleship.game_logic.Board;
import com.example.nb.battleship.game_logic.Game;
import com.example.nb.battleship.game_logic.Ship;
import com.example.nb.battleship.game_logic.Tile;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GameActivity extends AppCompatActivity implements QuitDialogFragment.MyDialogListener {

    /*Music && Sound && Vibrate*/
    private static SoundPlayer soundPlayer;
    private static MediaPlayer musicBG;
    private static boolean musicState, soundState, vibrateState;

    private SharedPreferences sp;
    private Toolbar toolbar;
    private Vibrator vibrator;
    private final long[] pattrn = {0, 200};

    /*Visual Game*/
    private GridLayout enemyBoardGL, playerBoardGL;
    private TextView computerTurnTxt, playerScoreTv;
    private EasyFlipView flipLayout;
    private AlertDialog dialog;
    private AlertDialog player_1_dialog;
    private AlertDialog player_2_dialog;
    private android.app.AlertDialog dialog_turn;
    private Button readyBtnDialog;
    private CircleImageView winner_image;
    private User user;

    private final String DIALOG_FRAGMENT_TAG = "dialog_fragment";
    private final int TILE_BIG_SIZE = 84, TILE_SMALL_SIZE = 40;

    private TextView playerTurnName;
    private TileClickListener listener;
    private TileClickListenerTwoPlayers listener2;

    private Chronometer timer;
    private long pauseOffSet;
    private boolean running;

    private Animation btn_press;
    private Animation btn_release;

    /*Logic Game*/
    private Game game;
    private boolean player1Turn = true;
    private boolean isGameEnd = false;
    private String time;
    private int player1Score = 10000, player2Score = 10000, computerScore = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sp = getSharedPreferences("setting", MODE_PRIVATE);
        musicState = sp.getBoolean("music", true);
        soundPlayer = new SoundPlayer(this);
        play();

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        playerScoreTv = findViewById(R.id.score);

        int numOfPlayers = getIntent().getIntExtra("numOfPlayers", 1);
        if(numOfPlayers == 1)
        {
            int[][] mat1 = (int[][]) getIntent().getSerializableExtra("player_1");
            if(game == null)
                game = new Game(this, mat1);
        }
        else
        {
            int[][] mat1 = (int[][]) getIntent().getSerializableExtra("player_1");
            int[][] mat2 = (int[][]) getIntent().getSerializableExtra("player_2");

            if(game == null)
                game = new Game(this, mat1, mat2);
        }


        listener = new TileClickListener();
        listener2 = new TileClickListenerTwoPlayers();


        if(numOfPlayers == 1)
        {
            createVisualGame();
        }
        else
        {
            playerBoardGL = findViewById(R.id.player_one_gl);
            enemyBoardGL = findViewById(R.id.player_one_enemy_gl);
            twoPlayers();
        }
    }


    /*Menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);

        musicState = sp.getBoolean("music", true);
        soundState = sp.getBoolean("sound", true);
        vibrateState = sp.getBoolean("vibrate", true);
        if(musicState) // music on
            menu.getItem(1).setIcon(R.drawable.ic_music_on_white_24dp);
        else        //music off
            menu.getItem(1).setIcon(R.drawable.ic_music_off_white_24dp);

        if(soundState) // sound on
            menu.getItem(2).setIcon(R.drawable.ic_icons8_sound_on);
        else        //sound off
            menu.getItem(2).setIcon(R.drawable.ic_icons8_sound_off);


        //change
        if(vibrateState) // vibrate on
            menu.getItem(3).setIcon(R.drawable.ic_vibration_white_on_24dp);
        else        // vibrate off
            menu.getItem(3).setIcon(R.drawable.ic_vibration_gray_off_24dp);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu:
                showMsg();
                return true;
            case R.id.music_menu:
                music(item);
                return true;
            case R.id.sound_menu:
                sound(item);
                return true;
            case R.id.vibration_menu:
                vibrate(item);
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

    private void sound(MenuItem item){
        if(soundState)
        {
            item.setIcon(R.drawable.ic_icons8_sound_off);
        }
        else
            item.setIcon(R.drawable.ic_icons8_sound_on);

        soundState = !soundState;
    }

    private void vibrate(MenuItem item){
        if(vibrateState)
        {
            item.setIcon(R.drawable.ic_vibration_gray_off_24dp);
        }
        else
            item.setIcon(R.drawable.ic_vibration_white_on_24dp);

        vibrateState = !vibrateState;
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

        pauseTimer();
        if(soundPlayer != null)
            soundPlayer.getSoundPool().release();

        if(musicBG != null)
            musicBG.pause(); //in case we moved to another activity or suspend the app

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("music", musicState);
        editor.putBoolean("sound", soundState);
        editor.putBoolean("vibrate", soundState);
        editor.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isGameEnd)
            StartTimer();

        if(soundPlayer != null)
        {
            if (soundPlayer.getSoundPool() == null)
                soundPlayer.createSoundPool();

            soundPlayer.loadSounds();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(soundPlayer != null)
            if(soundPlayer.getSoundPool() != null)
                soundPlayer.getSoundPool().release();
        if(musicBG != null)
        {
            musicBG.stop();
            musicBG.release();
            musicBG = null;
        }
    }

    /* This function creating the visual screen by calling functions */
    /*Game*/
    public void createVisualGame(){

        createPlayerBoard();
        createEnemyBoard();

        flipLayout = findViewById(R.id.flip_layout);
        playerTurnName = findViewById(R.id.change_turn_txt);
        computerTurnTxt = findViewById(R.id.computer_turn_txt);

        timer = findViewById(R.id.timer);
        StartTimer();
    }

    public void StartTimer(){

        if(!running)
        {
            timer.setBase(SystemClock.elapsedRealtime() - pauseOffSet);
            timer.start();
            running = true;
        }
    }

    public void pauseTimer(){

        if(running)
        {
            timer.stop();
            pauseOffSet = SystemClock.elapsedRealtime() - timer.getBase();
            running = false;
        }
    }

    public void computerTurn(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Tile computerTile = game.enemyTurn();
                changePlayerTilePic(computerTile);
                game.setTurn();
                flipLayout.flipTheView();
            }
        }, 1500);
    }

    //The move of computer
    public void changePlayerTilePic(final Tile tile){

        if(game.checkIfHitShip(tile, game.getPlayerBoard()))
        {
            boolean isShipSink = game.checkIfShipSink(tile.isShip(), game.getPlayerBoard());
            tile.setBackgroundResource(R.drawable.explode_animation);
            AnimationDrawable drawable_target = (AnimationDrawable) tile.getBackground();
            drawable_target.start();
            if(soundState)
                soundPlayer.playExplodeSound();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tile.setBackgroundResource(R.drawable.fire_animation);
                    AnimationDrawable drawable_fire = (AnimationDrawable) tile.getBackground();
                    drawable_fire.start();
                }
            }, 1040);

            //check if enemy win
            if(game.checkWinning(game.getPlayerBoard()))
            { //dialog here
                isGameEnd = true;
                pauseTimer();
                winDialog(0);
            }

        }
        else {
            tile.setBackgroundResource(R.drawable.btn_x);
            if(soundState)
                soundPlayer.playMissSound();

            computerScore -= 100;
        }
    }

    private void setPicOfShip(Board board, int shipNum){
        Ship  ship = board.getShipByNumber(shipNum);
        boolean isVertical = ship.isVertical();
        ArrayList<Tile> tilesToChangePic = ship.getShipList();

        ImageView ship1 = findViewById(R.id.ship_1);
        ImageView ship2 = findViewById(R.id.ship_2);
        ImageView ship3 = findViewById(R.id.ship_3);
        ImageView ship4 = findViewById(R.id.ship_4);
        ImageView ship5 = findViewById(R.id.ship_5);

        switch (shipNum)
        {
            case 1:
                if(isVertical)
                {
                    tilesToChangePic.get(0).setBackgroundResource(R.drawable.v_ship2_1);
                    tilesToChangePic.get(1).setBackgroundResource(R.drawable.v_ship2_2);
                }
                else
                {
                    tilesToChangePic.get(0).setBackgroundResource(R.drawable.ship2_1);
                    tilesToChangePic.get(1).setBackgroundResource(R.drawable.ship2_2);
                }
                ship1.setBackgroundResource(R.drawable.ship2_red);
                break;

            case 2:
                if(isVertical)
                {
                    tilesToChangePic.get(0).setBackgroundResource(R.drawable.v_ship3_1);
                    tilesToChangePic.get(1).setBackgroundResource(R.drawable.v_ship3_2);
                    tilesToChangePic.get(2).setBackgroundResource(R.drawable.v_ship3_3);
                }
                else
                {
                    tilesToChangePic.get(0).setBackgroundResource(R.drawable.ship3_1);
                    tilesToChangePic.get(1).setBackgroundResource(R.drawable.ship3_2);
                    tilesToChangePic.get(2).setBackgroundResource(R.drawable.ship3_3);
                }
                ship2.setBackgroundResource(R.drawable.ship3_red);
                break;

            case 3:
                if(isVertical)
                {
                    tilesToChangePic.get(0).setBackgroundResource(R.drawable.v_ship3_1);
                    tilesToChangePic.get(1).setBackgroundResource(R.drawable.v_ship3_2);
                    tilesToChangePic.get(2).setBackgroundResource(R.drawable.v_ship3_3);
                }
                else
                {
                    tilesToChangePic.get(0).setBackgroundResource(R.drawable.ship3_1);
                    tilesToChangePic.get(1).setBackgroundResource(R.drawable.ship3_2);
                    tilesToChangePic.get(2).setBackgroundResource(R.drawable.ship3_3);
                }
                ship3.setBackgroundResource(R.drawable.ship3_red);
                break;

            case 4:
                if(isVertical)
                {
                    tilesToChangePic.get(0).setBackgroundResource(R.drawable.v_ship4_1);
                    tilesToChangePic.get(1).setBackgroundResource(R.drawable.v_ship4_2);
                    tilesToChangePic.get(2).setBackgroundResource(R.drawable.v_ship4_3);
                    tilesToChangePic.get(3).setBackgroundResource(R.drawable.v_ship4_4);
                }
                else
                {
                    tilesToChangePic.get(0).setBackgroundResource(R.drawable.ship4_1);
                    tilesToChangePic.get(1).setBackgroundResource(R.drawable.ship4_2);
                    tilesToChangePic.get(2).setBackgroundResource(R.drawable.ship4_3);
                    tilesToChangePic.get(3).setBackgroundResource(R.drawable.ship4_4);
                }
                ship4.setBackgroundResource(R.drawable.ship4_red);
                break;

            case 5:
                if(isVertical)
                {
                    tilesToChangePic.get(0).setBackgroundResource(R.drawable.v_ship5_1);
                    tilesToChangePic.get(1).setBackgroundResource(R.drawable.v_ship5_2);
                    tilesToChangePic.get(2).setBackgroundResource(R.drawable.v_ship5_3);
                    tilesToChangePic.get(3).setBackgroundResource(R.drawable.v_ship5_4);
                    tilesToChangePic.get(4).setBackgroundResource(R.drawable.v_ship5_5);
                }
                else
                {
                    tilesToChangePic.get(0).setBackgroundResource(R.drawable.ship5_1);
                    tilesToChangePic.get(1).setBackgroundResource(R.drawable.ship5_2);
                    tilesToChangePic.get(2).setBackgroundResource(R.drawable.ship5_3);
                    tilesToChangePic.get(3).setBackgroundResource(R.drawable.ship5_4);
                    tilesToChangePic.get(4).setBackgroundResource(R.drawable.ship5_5);
                }
                ship5.setBackgroundResource(R.drawable.ship5_red);
                break;
        }
    }

    //The move of soundPlayer
    public void changeComputerTilePic(final Tile tile){

        tile.setEnabled(false);

        if(game.checkIfHitShip(tile, game.getEnemyBoard()))
        {
            boolean isShipSink = game.checkIfShipSink(tile.isShip(), game.getEnemyBoard());

            tile.setBackgroundResource(R.drawable.target_animation);
            AnimationDrawable drawable_target = (AnimationDrawable) tile.getBackground();
            drawable_target.start();
            if(soundState)
                soundPlayer.playRadarSound();

            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tile.setBackgroundResource(R.drawable.explode_animation);
                    AnimationDrawable drawable_target = (AnimationDrawable) tile.getBackground();
                    drawable_target.start();

                    if(soundState)
                        soundPlayer.playExplodeSound();
                    if(vibrateState)
                        vibrator.vibrate(pattrn, -1);

                }
            }, 1800);

            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tile.setBackgroundResource(R.drawable.fire_animation);
                    AnimationDrawable drawable_fire = (AnimationDrawable) tile.getBackground();
                    drawable_fire.start();
                }
            }, 2540);

            if(isShipSink)
            {
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setPicOfShip(game.getEnemyBoard(), tile.isShip());
                    }
                }, 2800);
                if(game.checkWinning(game.getEnemyBoard()))
                {
                    pauseTimer();
                    time = timer.getText().toString();
                    isGameEnd = true;

                    winDialog(1);
                }
            }
        }
        else {

            tile.setBackgroundResource(R.drawable.target_animation);
            AnimationDrawable drawable_target = (AnimationDrawable) tile.getBackground();
            drawable_target.start();

            if(soundState)
                soundPlayer.playRadarSound();

            Handler handler3 = new Handler();
            handler3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tile.setBackgroundResource(R.drawable.btn_x);
                    if(soundState)
                        soundPlayer.playMissSound();
                    player1Score -= 100; //add
                    playerScoreTv.setText(player1Score + ""); //add
                }
            }, 1800);
        }

    }

    /* Create the small board of the soundPlayer */
    public void createPlayerBoard(){

        int boardSize = game.getPlayerBoard().getBOARD_SIZE();

        playerBoardGL = findViewById(R.id.player_one_gl);
        playerBoardGL.setRowCount(boardSize);
        playerBoardGL.setColumnCount(boardSize);

        Tile[][] tiles = game.getPlayerBoard().getLogicBoard();

        for (int i = 0; i < boardSize ; i++) {

            for (int j = 0; j < boardSize; j++) {

                Tile tile = tiles[i][j];

                if(tile.isShip() != 0)
                    tile.setBackgroundResource(R.drawable.ship_frame);

                else
                    tile.setBackgroundResource(R.drawable.btn);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams(new ActionBar.LayoutParams(TILE_SMALL_SIZE, TILE_SMALL_SIZE));
                tile.setLayoutParams(params);
                tiles[i][j].setEnabled(false);
                playerBoardGL.addView(tile);
            }
        }
    }

    /* Create the big board of the enemy/computer */
    public void createEnemyBoard(){

        int boardSize = game.getEnemyBoard().getBOARD_SIZE();

        enemyBoardGL = findViewById(R.id.player_one_enemy_gl);
        enemyBoardGL.setRowCount(boardSize);
        enemyBoardGL.setColumnCount(boardSize);

        Tile[][] tiles = game.getEnemyBoard().getLogicBoard();

        for (int i = 0; i < boardSize ; i++)
        {
            for (int j = 0; j < boardSize; j++)
            {
                Tile tile = tiles[i][j];
                tile.setBackgroundResource(R.drawable.btn);
                tile.setOnClickListener(listener);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(new ActionBar.LayoutParams(TILE_BIG_SIZE, TILE_BIG_SIZE));
                tile.setLayoutParams(params);
                enemyBoardGL.addView(tile);
            }
        }
    }

    private class TileClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(game.getIsPlayerTurn() && !isGameEnd)
            {
                changeComputerTilePic((Tile) v);
                game.setTurn();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isGameEnd)
                        {
                            computerTurnTxt.setText(R.string.computer_turn_txt);
                            flipLayout.flipTheView();
                            computerTurn();
                        }
                    }
                }, 10); //3000
            }
        }
    }


    /*Winning Dialog*/
    private void winDialog(final int winner){

        final AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.win_dialog, null);

        TextView scoreTv = mView.findViewById(R.id.score);

        builder.setView(mView);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        },2000);

        int winScore = 0;
        if(winner == 0)
            winScore = computerScore;
        else if(winner == 1)
            winScore = player1Score;
        else if (winner == 2)
            winScore = player2Score;


        AnimateCounter animateCounter = new AnimateCounter.Builder(scoreTv)
                .setCount(0, winScore)
                .setDuration(4000)
                .build();
        animateCounter.execute();

        Button homeBtn = mView.findViewById(R.id.home_btn);
        Button recordsBtn = mView.findViewById(R.id.records_btn);
        winner_image = mView.findViewById(R.id.winner_image);
        if(winner == 1)
        {
            loadUser();
            winner_image.setImageBitmap(user.getPhoto());
            String name = user.getName();
            ArrayList<Record> records = loadTavleRecords();
            hallOfFame(records, name, player1Score, time);
        }
        else if(winner != 2)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_pic);
            winner_image.setImageBitmap(bitmap);
        }
        else
        {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.computer_circle);
            winner_image.setImageBitmap(bitmap);
        }

        /*>>>>>>>>>>ANIMATION FOR BUTTONS<<<<<<<<<<*/
        btn_press = AnimationUtils.loadAnimation(this, R.anim.btn_pressed);
        btn_release = AnimationUtils.loadAnimation(this, R.anim.btn_release);
        final BtnAnimation btn_animation = new BtnAnimation();

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacingShipsActivity1.activity.finish();
                dialog.dismiss();
                finish();
            }
        });

        recordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacingShipsActivity1.activity.finish();
                dialog.dismiss();
                finish();
                Intent intent = new Intent(GameActivity.this, TableRecordsActivity.class);
                startActivity(intent);
            }
        });

        homeBtn.setOnTouchListener(btn_animation);
        recordsBtn.setOnTouchListener(btn_animation);

    } // computre = 0, player_1 = 1, player_2 = 2

    public class BtnAnimation implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.startAnimation(btn_press);
                btn_press.setFillAfter(true);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.startAnimation(btn_release);
            }
            return false;
        }
    }

    /*Table Records*/
    private ArrayList<Record> loadTavleRecords() {
        ArrayList<Record> records = null;

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

        return records;
    }

    private void hallOfFame(ArrayList<Record> records, String name, int score, String time) {
        ArrayList<Record> recordsToCheck = new ArrayList<>();

        for (Record record: records)
        {
            if(record.getScore() < score)
                recordsToCheck.add(record);
            if (record.getScore() == score)
                if(record.getTime().compareTo(time) > -1)
                    recordsToCheck.add(record);
        }

        if(recordsToCheck.size() > 0)
        {
            int k = records.indexOf(recordsToCheck.get(0));
            for (int i = recordsToCheck.size() - 1; i >= 1; i--)
            {
                Record previousRecord = recordsToCheck.get(i - 1);
                recordsToCheck.get(i).setName(previousRecord.getName());
                recordsToCheck.get(i).setScore(previousRecord.getScore());
                recordsToCheck.get(i).setTime(previousRecord.getTime());
            }

            records.get(k).setName(name);
            records.get(k).setScore(score);
            records.get(k).setTime(time);

            saveRecords(records);
        }

    }


    private void saveRecords(ArrayList<Record> records) {
        try {
            FileOutputStream fos = openFileOutput("records", MODE_PRIVATE);
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

    /*Fragment dialog - Exit the game*/
    private void showMsg(){
        QuitDialogFragment fragment = new QuitDialogFragment();
        fragment.show(getFragmentManager(),DIALOG_FRAGMENT_TAG);
    }

    @Override
    public boolean onPositiveBtnClicked() {
        PlacingShipsActivity1.activity.finish();
        finish();
        return true;
    }

    @Override
    public boolean onNegativeBtnClicked() {
        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void loadUser(){

        try {
            FileInputStream fis = openFileInput("User");
            ObjectInputStream ois = new ObjectInputStream(fis);

            user = (User) ois.readObject();

            ois.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(user == null)
            user = new User("UserName", BitmapFactory.decodeResource(this.getResources(), R.drawable.player_pic));

    }


    /*Game of two players*/
    private void twoPlayers() {
        createVisualGame1();
            if(player1Turn)
            {
                drawSmallBoard(game.getPlayerBoard());
                drawBigBoard(game.getEnemyBoard());
            }
            else
            {
                drawSmallBoard(game.getEnemyBoard());
                drawBigBoard(game.getPlayerBoard());
            }

    }

    public void drawSmallBoard(Board board){
        int boardSize = board.getBOARD_SIZE();

        playerBoardGL.removeAllViews();
        playerBoardGL.setRowCount(boardSize);
        playerBoardGL.setColumnCount(boardSize);


        Tile[][] tiles = board.getLogicBoard();

        for (int i = 0; i < boardSize ; i++)
            for (int j = 0; j < boardSize; j++)
            {
                Tile tile = tiles[i][j];
                if(tile.isHit())
                {
                    if(tile.isShip() == 0)
                        tile.setBackgroundResource(R.drawable.btn_x);
                    else
                    {
                        tile.setBackgroundResource(R.drawable.fire_animation);
                        AnimationDrawable drawable_fire = (AnimationDrawable) tile.getBackground();
                        drawable_fire.start();
                    }
                }
                else if(tile.isShip() != 0)
                    tile.setBackgroundResource(R.drawable.ship_frame);

                else
                    tile.setBackgroundResource(R.drawable.btn);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams(new ActionBar.LayoutParams(TILE_SMALL_SIZE, TILE_SMALL_SIZE));
                tile.setLayoutParams(params);
                tiles[i][j].setEnabled(false);
                playerBoardGL.removeAllViews();
            }
    }

    public void drawBigBoard(Board board){

        int boardSize = board.getBOARD_SIZE();
        enemyBoardGL.removeAllViews();
        enemyBoardGL.setRowCount(boardSize);
        enemyBoardGL.setColumnCount(boardSize);

        Tile[][] tiles = board.getLogicBoard();

        for (int i = 0; i < boardSize ; i++)
        {
            for (int j = 0; j < boardSize; j++)
            {
                Tile tile = tiles[i][j];
                if(tile.isHit())
                {
                    if(tile.isShip() == 0)
                        tile.setBackgroundResource(R.drawable.btn_x);
                    else
                    {
                        tile.setBackgroundResource(R.drawable.fire_animation);
                        AnimationDrawable drawable_fire = (AnimationDrawable) tile.getBackground();
                        drawable_fire.start();
                    }
                }

                else
                    tile.setBackgroundResource(R.drawable.btn);

                tile.setOnClickListener(listener2);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(new ActionBar.LayoutParams(TILE_BIG_SIZE, TILE_BIG_SIZE));
                tile.setLayoutParams(params);
                enemyBoardGL.addView(tile);
            }
        }
    }

    public void createVisualGame1(){

        flipLayout = findViewById(R.id.flip_layout);
        playerTurnName = findViewById(R.id.change_turn_txt);
        computerTurnTxt = findViewById(R.id.computer_turn_txt);

        timer = findViewById(R.id.timer);
        StartTimer();
    }

    private class TileClickListenerTwoPlayers implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(!isGameEnd)
            {
                changeComputerTilePic((Tile) v);
                player1Turn = !player1Turn;

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isGameEnd)
                        {
                            if(player1Turn)
                                showDialog(getString(R.string.p1_turn));
                            else
                                showDialog(getString(R.string.p2_turn));
                        }
                    }
                }, 1000); //3000
            }
        }
    }

    private void showDialog(String msg){
        //create dialog for one player option
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(GameActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.change_turn_fragment, null);
        builder.setView(mView);
        dialog_turn = builder.create();
        dialog_turn.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_turn.show();
        TextView msgTv = mView.findViewById(R.id.change_turn_txt);
        msgTv.setText(msg);

        readyBtnDialog = mView.findViewById(R.id.ready_btn_dialog);
        readyBtnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_turn.hide();
                if(player1Turn)
                {
                    playerBoardGL.removeAllViews();
                    drawSmallBoard(game.getPlayerBoard());
                    drawBigBoard(game.getEnemyBoard());
                }
                else
                {
                    enemyBoardGL.removeAllViews();
                    drawSmallBoard(game.getEnemyBoard());
                    drawBigBoard(game.getPlayerBoard());
                }
            }
        });
    }

}


