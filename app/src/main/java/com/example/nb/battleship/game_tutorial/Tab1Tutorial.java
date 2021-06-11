package com.example.nb.battleship.game_tutorial;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.Locale;

import com.example.nb.battleship.R;

public class Tab1Tutorial extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tutorial_tab1, container, false);

        AnimationDrawable drawableSwipe;
        ImageView swipeIv = rootView.findViewById(R.id.gif_swipe);

        drawableSwipe = (AnimationDrawable) swipeIv.getBackground();
        drawableSwipe.start();

        return rootView;
    }
}
