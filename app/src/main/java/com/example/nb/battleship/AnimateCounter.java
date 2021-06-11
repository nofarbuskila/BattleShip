package com.example.nb.battleship;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Interpolator;
import android.widget.TextView;

public class AnimateCounter {

    private TextView mView;
    private long mDuration;
    private float mStartValue;
    private float mEndValue;
    private int mPrecision;
    private Interpolator mInterpolator;
    private ValueAnimator mValueAnimator;
    private AnimateCounterListner mListener;

    public void execute() {
        mValueAnimator = ValueAnimator.ofFloat(mStartValue, mEndValue);
        mValueAnimator.setDuration(mDuration);
        mValueAnimator.setInterpolator((TimeInterpolator) mInterpolator);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float current = Float.valueOf(mValueAnimator.getAnimatedValue().toString());
                mView.setText(String.format("%." + mPrecision + "f", current));
            }
        });


        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) {
                    mListener.onAnimateCounterEnd();
                }
            }
        });
        mValueAnimator.start();
    }

    public static class Builder {
        private long mDuration = 3000;
        private float mStartValue = 0;
        private float mEndValue = 10;
        private int mPrecision = 0;
        private Interpolator mInterpolator = null;
        private TextView mView;

        public Builder(TextView view) {
            if (view == null)
                throw new IllegalArgumentException("View can not be null");
            mView = view;
        }

        public Builder setCount(final int start, final int end) {
            if (start == end)
            {
                throw new IllegalArgumentException("Start and end must be differnt");
            }

            mStartValue = start;
            mEndValue = end;
            mPrecision = 0;
            return this;

        }

        public Builder setCount(final int start, final int end, final int precision){
            if(Math.abs((start - end)) < 0.001)
            {
                throw new IllegalArgumentException("Start and end must be diiffrent");
            }

            if(precision < 0){
                throw new IllegalArgumentException("Precision cant be nagative");
            }

            mStartValue = start;
            mEndValue = end;
            mPrecision = 0;
            return this;


        }

        public Builder setDuration(long duration){
            if(duration <= 0)
                throw new IllegalArgumentException("Duration cant be nagative");

            mDuration = duration;
            return this;
        }

        public Builder setmInterpolator(Interpolator interpolator){
            mInterpolator = interpolator;
            return this;
        }

        public AnimateCounter build(){
            return new AnimateCounter(this);
        }


    }

    private AnimateCounter(Builder builder){
        mView = builder.mView;
        mDuration = builder.mDuration;
        mStartValue = builder.mStartValue;
        mEndValue = builder.mEndValue;
        mPrecision = builder.mPrecision;
        mInterpolator = builder.mInterpolator;
    }

    public void stop(){
        if(mValueAnimator.isRunning()){
            mValueAnimator.cancel();
        }
    }

    public void setAnimatorCounterListner(AnimateCounterListner listner){
        mListener = listner;
    }

    public interface  AnimateCounterListner{
        void onAnimateCounterEnd();
    }
}
