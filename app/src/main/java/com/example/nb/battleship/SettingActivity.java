package com.example.nb.battleship;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.varunest.sparkbutton.SparkButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ghyeok.stickyswitch.widget.StickySwitch;

public class SettingActivity extends AppCompatActivity {
    private static MediaPlayer musicBG;
    private boolean musicState, soundState, vibrateState;

    private Switch soundsSwitch, musicSwitch, vibrateSwitch;
    private SharedPreferences sp;

    private CircleImageView profileImg;
    private TextView profileUserName;
    private User user;

    private Toolbar toolbar;

    private final int CAMERA_REQUEST = 1;
    private Bitmap bitmap;
    private CircleImageView imageBtn;
    private EditText et;

    private Animation btn_press;
    private Animation btn_release;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        loadUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        soundState = sp.getBoolean("sound", true);
        musicState = sp.getBoolean("music", true);
        vibrateState = sp.getBoolean("vibrate", true);

        play();

        soundsSwitch = findViewById(R.id.sound_switch);
        soundsSwitch.setChecked(soundState);
        soundsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (soundState) {
                    //unmute audio
                    AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    aManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);

                    Toast.makeText(SettingActivity.this, "Sounds Off", Toast.LENGTH_SHORT).show();

                }
                else {
                    AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    aManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                    Toast.makeText(SettingActivity.this, "Sounds On", Toast.LENGTH_SHORT).show();
                }

                soundState = !soundState;
            }
        });

        musicSwitch = findViewById(R.id.music_switch);
        musicSwitch.setChecked(musicState);
        musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (musicState) {
                    //unmute audio
                    AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    aManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);

                    if(musicBG != null)
                        if(musicBG.isPlaying())
                            musicBG.pause();

                    Toast.makeText(SettingActivity.this, "Music Off", Toast.LENGTH_SHORT).show();

                }
                else {
                    AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    aManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);

                    if(musicBG != null)
                        if(!musicBG.isPlaying())
                            musicBG.start();

                    Toast.makeText(SettingActivity.this, "Music On", Toast.LENGTH_SHORT).show();
                }

                musicState = !musicState;
            }
        });

        vibrateSwitch = findViewById(R.id.vibrate_switch);
        vibrateSwitch.setChecked(vibrateState);
        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (vibrateState) {
                    //unmute audio
                    AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    aManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);

                    Toast.makeText(SettingActivity.this, "Vibrate Off", Toast.LENGTH_SHORT).show();
                }
                else {
                    AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    aManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);

                    Toast.makeText(SettingActivity.this, "Vibrate On", Toast.LENGTH_SHORT).show();
                }

                vibrateState = !vibrateState;
            }
        });


        if (user != null)
        {
            profileImg = findViewById(R.id.profile_img);
            profileImg.setImageBitmap(user.getPhoto());
            profileUserName = findViewById(R.id.profile_name_tv);
            profileUserName.setText(user.getName());
        }

        /*>>>>>>>>>>ANIMATION FOR BUTTONS<<<<<<<<<<*/
        btn_press = AnimationUtils.loadAnimation(this, R.anim.btn_pressed);
        btn_release = AnimationUtils.loadAnimation(this, R.anim.btn_release);
        BtnAnimation btn_animation = new BtnAnimation();


        Button editBtn = findViewById(R.id.edit_profile_btn);

        editBtn.setOnTouchListener(btn_animation);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBuildProfileDialog();
            }
        });

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

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("sound", soundState);
        editor.putBoolean("music", musicState);
        editor.putBoolean("vibrate", vibrateState);
        editor.commit();

        if(musicBG != null)
            musicBG.pause(); //in case we moved to another activity or suspend the app
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(sp != null)
        {
            soundState = sp.getBoolean("sound", true);
            musicState = sp.getBoolean("music", true);
            vibrateState = sp.getBoolean("vibrate", true);

            soundsSwitch.setChecked(soundState);
            musicSwitch.setChecked(musicState);
            vibrateSwitch.setChecked(vibrateState);

            play();
        }
    }

    /*Music background*/
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

    /*Menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == R.id.main_menu)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
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

    private void showBuildProfileDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
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
        if(user.getPhoto() != null)
            imageBtn.setImageBitmap(user.getPhoto());
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
                profileImg.setImageBitmap(user.getPhoto());
                profileUserName.setText(user.getName());
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

    private void save(User user){

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode==RESULT_OK)
        {
            bitmap = (Bitmap)data.getExtras().get("data");
            imageBtn.setImageBitmap(rotateImage(bitmap, 0));//-90 for selfie
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

}


