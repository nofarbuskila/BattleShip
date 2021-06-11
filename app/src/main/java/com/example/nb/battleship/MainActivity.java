package com.example.nb.battleship;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.varunest.sparkbutton.SparkButton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static MediaPlayer musicBG;
    private static boolean musicState;

    private ImageView imageBtn;
    private final int CAMERA_REQUEST = 1;
    private Bitmap bitmap;
    private User user;
    private EditText et;
    private SharedPreferences sp;

    private Animation btn_press;
    private Animation btn_release;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        musicState = sp.getBoolean("music", true);
        play();

        //First time we open the app this dialog will appear
        if(sp.getBoolean("first_run", true))
        {
            showBuildProfileDialog();
            initialTableRecoreds();
        }

        /*ANIMATION FOR BUTTONS*/
        btn_press = AnimationUtils.loadAnimation(this, R.anim.btn_pressed);
        btn_release = AnimationUtils.loadAnimation(this, R.anim.btn_release);
        final BtnAnimation btn_animation = new BtnAnimation();

        /*TUTORIAL BUTTON*/
        Button tutorialBtn = findViewById(R.id.tutorial_btn);
        tutorialBtn.setOnTouchListener(btn_animation);
        tutorialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
                startActivity(intent);
            }
        });

        /*PLAY BUTTON*/
        final Button playBtn = findViewById(R.id.play_btn);
        playBtn.setOnTouchListener(btn_animation);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //create dialog for one player option
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.num_of_players_dialog, null);
                builder.setView(mView);
                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();


                //create listener for number Of Players buttons
                //numOfPlayersBtn playersBtn = new numOfPlayersBtn();

                final Button onePlayer = mView.findViewById(R.id.one_player_btn);
                Button twoPlayers = mView.findViewById(R.id.two_players_btn);

                onePlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, PlacingShipsActivity1.class);
                        intent.putExtra("numOfPlayers", 1);
                        startActivity(intent);
                    }
                });

                twoPlayers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, PlacingShipsActivity2.class);
                        intent.putExtra("numOfPlayers", 2);
                        startActivity(intent);
                    }
                });
            }
        });

        /*SETTING BUTTON*/
        Button settingBtn = findViewById(R.id.settings_btn);
        settingBtn.setOnTouchListener(btn_animation);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        /*RECORDS BUTTON*/
        Button recordsBtn = findViewById(R.id.records_btn);
        recordsBtn.setOnTouchListener(btn_animation);
        recordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TableRecordsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showBuildProfileDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.register_dialog, null);
        builder.setView(mView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dialog.getWindow().setLayout(900, 1200);

        TextView titleTv = mView.findViewById(R.id.title);
        final TextView cameraHelpTv = mView.findViewById(R.id.camera_help_tv);
        et = mView.findViewById(R.id.name_edit_text);
        imageBtn = mView.findViewById(R.id.image_btn);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        titleTv.setPaintFlags(titleTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        cameraHelpTv.setVisibility(View.VISIBLE);
        cameraHelpTv.setText(R.string.note_dialog);

        final SparkButton saveBtn = mView.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et.getText().toString();
                imageBtn.buildDrawingCache();
                bitmap = imageBtn.getDrawingCache();

                user = new User(name, bitmap);
                save(user);
                saveBtn.playAnimation();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 700);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode==RESULT_OK)
        {
            bitmap = (Bitmap)data.getExtras().get("data");
            imageBtn.setImageBitmap(rotateImage(bitmap, 0));//-90 for selfie
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("first_run", false);
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
    }

    private void stopPlayer() {
        if (musicBG != null)
        {
            musicBG.release();
            musicBG = null;
        }
    }

    private void save(User user){

        if(user == null)
            user = new User("UserName", BitmapFactory.decodeResource(this.getResources(), R.drawable.player_pic));

        try {
            FileOutputStream fos = openFileOutput("User", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(user);

            oos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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

    //rotate Function
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void initialTableRecoreds(){
        ArrayList<Record> records = new ArrayList<>();

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
