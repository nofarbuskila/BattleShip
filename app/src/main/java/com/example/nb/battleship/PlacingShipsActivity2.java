package com.example.nb.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;


public class PlacingShipsActivity2 extends AppCompatActivity {

    public static Activity activity;

    private static MediaPlayer musicBG;
    private static boolean musicState;

    private SharedPreferences sp;
    private GridLayout playerBoardGL;

    private final int BOARD_SIZE = 10;
    private final int TILE_SIZE = 84;

    private int[][] player_1_board = new int[BOARD_SIZE][BOARD_SIZE];
    private int[][] player_2_board = new int[BOARD_SIZE][BOARD_SIZE];
    private Intent intent;

    private boolean player2Turn = false;

    private Toolbar toolbar;

    private AlertDialog dialog;
    private Button readyBtnDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placing_ships_1);

        activity = this;
        intent = new Intent(this, GameActivity.class);
        sp = getSharedPreferences("setting", MODE_PRIVATE);
        musicState = sp.getBoolean("music", true);
        play();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        playerBoardGL = findViewById(R.id.board_gl);

        Button generateBtn = findViewById(R.id.random_btn);
        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!player2Turn)
                {
                    resetMat(player_1_board);
                    placeRandomShipsOnBoard(player_1_board, 5);
                    drawVisualBoard(player_1_board);
                }
                else
                {
                    resetMat(player_2_board);
                    placeRandomShipsOnBoard(player_2_board, 5);
                    drawVisualBoard(player_2_board);
                }
            }
        });

        Button readyBtn = findViewById(R.id.ready_btn);
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!player2Turn)
                {
                    player2Turn = true;
                    intent.putExtra("player_1", player_1_board);
                    showDialog(getString(R.string.manage_board_p2));
                    playerTwo();
                }
                else
                {
                    intent.putExtra("numOfPlayers", 2);
                    intent.putExtra("player_2", player_2_board);
                    startActivity(intent);
                }
            }
        });


        showDialog(getString(R.string.manage_board_p1));
        playerOne();

    }

    private void showDialog(String msg){
        //create dialog for one player option
        AlertDialog.Builder builder = new AlertDialog.Builder(PlacingShipsActivity2.this);
        View mView = getLayoutInflater().inflate(R.layout.change_turn_fragment, null);
        builder.setView(mView);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView msgTv = mView.findViewById(R.id.change_turn_txt);
        msgTv.setText(msg);

        readyBtnDialog = mView.findViewById(R.id.ready_btn_dialog);
        readyBtnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
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
    protected void onStop() {
        super.onStop();
        stopPlayer();
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

    private void playerOne(){
        placeRandomShipsOnBoard(player_1_board, 5);
        drawVisualBoard(player_1_board);
    }

    private void playerTwo(){
        placeRandomShipsOnBoard(player_2_board, 5);
        drawVisualBoard(player_2_board);
    }

    /*create visual board */
    private void drawVisualBoard(int[][] mat){

        playerBoardGL.removeAllViews();

        for (int i = 0; i < mat.length; i++) {

            for (int j = 0; j < mat.length; j++) {

                ImageView imageView = new ImageView(PlacingShipsActivity2.this);

                if(mat[i][j] != 0)
                    imageView.setBackgroundResource(R.drawable.ship_frame);

                else
                    imageView.setBackgroundResource(R.drawable.btn);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams(new ActionBar.LayoutParams(TILE_SIZE, TILE_SIZE));
                imageView.setLayoutParams(params);
                imageView.setEnabled(false);
                playerBoardGL.addView(imageView);
            }
        }
    }

    /*create logic board randomly functions*/
    private void resetMat(int[][] mat){

        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                mat[i][j] = 0;

    }

    private void placeRandomShipsOnBoard(int[][] myBoard, int mNumOfShips) {
        for (int i = mNumOfShips; i >= 1; i--) {
            if (i >= 3)
                placeShipsOnBoard(myBoard, i, i);
            else
                placeShipsOnBoard(myBoard, i + 1, i);
        }
    }

    private void placeShipsOnBoard(int[][] myBoard, int sizeOfShips, int shipNum) {
        Random r = new Random();
        boolean validPlace = false;

        while (!validPlace) {
            int startPositionI = r.nextInt(10);
            int startPositionJ = r.nextInt(10);

            validPlace = tryPlaceSubmarinesOnBoard(myBoard, startPositionI, startPositionJ, sizeOfShips, shipNum);
        }
    }

    private boolean tryPlaceSubmarinesOnBoard(int[][] myBoard, int startPositionI, int startPositionJ, int sizeOfShip, int shipNum) {
        boolean validPlace = false;

        if (myBoard[startPositionI][startPositionJ] == 0) { //empty space for start position

            Random r = new Random();
            boolean vertical = r.nextBoolean();

            if (vertical) {
                if (canPlaceVerticalDown(myBoard, startPositionI, startPositionJ, sizeOfShip)) { //vertical down
                    for (int i = startPositionI; i <= startPositionI + sizeOfShip - 1; i++)
                        myBoard[i][startPositionJ] = shipNum;

                    validPlace = true;

                } else if (canPlaceVerticalUp(myBoard, startPositionI, startPositionJ, sizeOfShip)) //vertical up
                {
                    for (int i = startPositionI; i >= startPositionI - sizeOfShip + 1; i--)
                        myBoard[i][startPositionJ] = shipNum;


                    validPlace = true;
                }
            } else    // if( !vertical)
            {

                if (canPlaceHorizontalLeft(myBoard, startPositionI, startPositionJ, sizeOfShip)) //horizontal left
                {
                    for (int j = startPositionJ; j >= startPositionJ - sizeOfShip + 1; j--)
                        myBoard[startPositionI][j] = shipNum;


                    validPlace = true;

                } else if (canPlaceHorizontalRight(myBoard, startPositionI, startPositionJ, sizeOfShip)) //horizontal right
                {
                    for (int j = startPositionJ; j <= startPositionJ + sizeOfShip - 1; j++)
                        myBoard[startPositionI][j] = shipNum;

                    validPlace = true;

                }
            }
        }

        return validPlace;
    }

    private boolean canPlaceVerticalDown(int[][] myBoard, int startPositionI, int startPositionJ, int sizeOfShip) {

        if (startPositionI + sizeOfShip - 1 < 10)
            for (int i = startPositionI; i <= startPositionI + sizeOfShip - 1; i++) {
                if (myBoard[i][startPositionJ] != 0)
                    return false;
            }
        else
            return false;

        return true;
    }

    private boolean canPlaceVerticalUp(int[][] myBoard, int startPositionI, int startPositionJ, int sizeOfShip) {

        if (startPositionI - sizeOfShip + 1 >= 0) {
            for (int i = startPositionI; i >= startPositionI - sizeOfShip + 1; i--) {
                if (myBoard[i][startPositionJ] != 0)
                    return false;
            }
        } else
            return false;

        return true;
    }

    private boolean canPlaceHorizontalRight(int[][] myBoard, int startPositionI, int startPositionJ, int sizeOfShip) {

        if (startPositionJ + sizeOfShip - 1 < 10) {
            for (int j = startPositionJ; j <= startPositionJ + sizeOfShip - 1; j++) {
                if (myBoard[startPositionI][j] != 0)
                    return false;
            }
        } else
            return false;

        return true;
    }

    private boolean canPlaceHorizontalLeft(int[][] myBoard, int startPositionI, int startPositionJ, int sizeOfShip) {

        if (startPositionJ - sizeOfShip + 1 >= 0) {
            for (int j = startPositionJ; j >= startPositionJ - sizeOfShip + 1; j--) {
                if (myBoard[startPositionI][j] != 0)
                    return false;
            }
        } else
            return false;

        return true;
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
