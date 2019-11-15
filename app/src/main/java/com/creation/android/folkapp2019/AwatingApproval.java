package com.creation.android.folkapp2019;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.Locale;

public class AwatingApproval extends AppCompatActivity {
    String[] descriptionData = {"Request", "Awating\nApproval", "Confirm\nOccupancy",};
    private CountDownTimer mCountDownTimer;
    private static final long START_TIME_IN_MILLIS = 2700000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;
    private TextView timerValue;
    StateProgressBar stateProgressBar ;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //auth
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String currentFolkBoyId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awating_approval);

        final StateProgressBar stateProgressBar = (StateProgressBar)findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        checkifReqAccepted();



    }
    private void checkifReqAccepted() {

        String currentFolkBoyId = firebaseAuth.getCurrentUser().getUid();

        CollectionReference folkBoyNotificationsColRef
                = firestore.collection("FolkBoyNotifications");

        final ProgressDialog progressDialog = new ProgressDialog(AwatingApproval.this);
        progressDialog.setTitle("Checking if request accepted");
        progressDialog.show();

        folkBoyNotificationsColRef
                .document(currentFolkBoyId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            Toast.makeText(AwatingApproval.this, "Request Accepted", Toast.LENGTH_SHORT).show();

                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                            startActivity(new Intent(AwatingApproval.this,ConfirmOccupancy.class));
                            progressDialog.dismiss();


                        }
                        else{
                            Toast.makeText(AwatingApproval.this, "request not accepted", Toast.LENGTH_SHORT).show();


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


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

