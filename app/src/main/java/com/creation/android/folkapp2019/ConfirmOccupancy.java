package com.creation.android.folkapp2019;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmOccupancy extends Fragment {

    String[] descriptionData = {"Request", "Awating\nApproval", "Confirm\nOccupancy",};
    private CountDownTimer mCountDownTimer;
    private static final long START_TIME_IN_MILLIS = 2700000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;
    private TextView timerValue;



    public ConfirmOccupancy() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_confirm_occupancy, container, false);
        final StateProgressBar stateProgressBar = (StateProgressBar)view.findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);



        return view;
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                //prog();

            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                //book_btn.setText("");
                //book_btn.setVisibility(View.INVISIBLE);
                //mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerRunning = true;
        //mButtonReset.setVisibility(View.INVISIBLE);
    }
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        timerValue.setText(timeLeftFormatted);
        //prog();
        //pb.setProgress(minutes);
    }

}
