package com.creation.android.folkapp2019;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kofigyan.stateprogressbar.StateProgressBar;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ConfirmOccupancy extends AppCompatActivity {

    String[] descriptionData = {"Request", "Awating\nApproval", "Confirm\nOccupancy",};
    //Timer vars
    private CountDownTimer mCountDownTimer;
    private static final long START_TIME_IN_MILLIS = 2700000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mTimerRunning;
    private TextView timerValue;

    //fireStore
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String currentFolkBoy;
    String currentFbEmail;

    //widgets
    TextView textViewBedNo, textViewRemaingTime, textViewBerthPref, textViewMsgToFg;
    View view;


    //vars
    String reqAcceptedtime;
    Boolean acommodationAlreadyCancled=false;

    boolean bedlareadyDeleted = true;


    // Declare Context variable at class level in Fragment
    private Context mContext;
    private int cnt =0;
    private FragmentTransaction fr;




    public ConfirmOccupancy() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_occupancy);
        final StateProgressBar stateProgressBar =findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentFolkBoy = firebaseAuth.getCurrentUser().getUid();
        currentFbEmail = firebaseAuth.getCurrentUser().getEmail();
        JodaTimeAndroid.init(this);

        textViewBedNo = findViewById(R.id.textViewBedNo);
        textViewRemaingTime = findViewById(R.id.textViewRemaningTime);
        textViewBerthPref = findViewById(R.id.textView_berth_pref);
        textViewMsgToFg = findViewById(R.id.textView_msg_to_fg);


        loadFolkBoyNotification();



    }

    private void loadFolkBoyNotification() {
        firestore.collection("FolkBoyNotifications")
                .document(currentFolkBoy)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String bedNo = documentSnapshot.getString("allotedBedNo");
                            String berthPref = documentSnapshot.getString("fb_berth_pref");
                            String msg = documentSnapshot.getString("fb_message");

                            //populate widgets
                            textViewBedNo.setText("Bed No:" + bedNo);
                            textViewBerthPref.setText(berthPref);
                            textViewMsgToFg.setText(msg);
                            Toast.makeText(ConfirmOccupancy.this, "bed No: " + bedNo, Toast.LENGTH_SHORT).show();

                            final String reqAcceptedTime
                                    = documentSnapshot.getString("reqAcceptedTime");


                            mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    mTimeLeftInMillis = millisUntilFinished;
                                    try {
//                                        updateRemainingTimeText(reqAcceptedTime);
                                        newUpdateTimer(reqAcceptedTime);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
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
                        } else {
                            Toast.makeText(ConfirmOccupancy.this, "Req not accepted", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                    }
                });
    }



    public void newUpdateTimer(String reqAcceptedtime) throws ParseException {
        Log.d(TAG, "updateRemainingTimeText: in");

        cnt++;
        Log.d(TAG, "newUpdateTimer: cnt" + cnt);

//        reqAcceptedtime = "06/14/2019 22:00:00";

        DateTime currentTime = DateTime.now();

//        String currentTime = "06/14/2019 22:43:00";

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date dateReqAcceptedTime = null;
        Date dateCurrentTime = null;

        try {
//            dateDefault45minTime = sdfForMin.parse(default45MinTime);
//            dateReqAcceptedTime = sdf.parse(reqAcceptedTime);
//            dateCurrentTime = sdf.parse(currentTime);

            DateTimeZone dateTimeZone = DateTimeZone.forID("Asia/Calcutta");


            DateTime dtDateReqAcceptedTime_ = new DateTime(reqAcceptedtime);
            DateTime dtDateCurrentTime = new DateTime(dateCurrentTime);
            dtDateCurrentTime.withZone(dateTimeZone);

//            int currentTime_reqAccetedTime = Minutes.minutesBetween(dtDateCurrentTime, dtDateReqAcceptedTime_).getMinutes() % 60;

//            String formattedCurrentTime_reqAcceptedTime = sdf.format(currentTime_reqAccetedTime);


//            Date DateFormattedCurrentTime_reqAcceptedTime = sdfForMin.parse(formattedCurrentTime_reqAcceptedTime);
//            DateTime dtDateFormattedCurrentTime_reqAcceptedTime
//                    = new DateTime(DateFormattedCurrentTime_reqAcceptedTime);
//
//            Log.d(TAG, "" +
//                            "\ndays in between: " + Days.daysBetween(dtDateCurrentTime,dtDateReqAcceptedTime_).getDays() + " days"
//                            + "\nhours in between: " + Hours.hoursBetween(dtDateCurrentTime, dtDateReqAcceptedTime_).getHours() + " hours"
//                            + "\nmin in between : " + Minutes.minutesBetween(dtDateCurrentTime, dtDateReqAcceptedTime_).getMinutes() % 60 + " minutes"
//                            + "\nsec in between : " + Seconds.secondsBetween(dtDateCurrentTime, dtDateReqAcceptedTime_).getSeconds() % 60 + " seconds"
//
//                            +"\n-----"
//                            + "Time remaining: default45time - (currentTime - reqAcceptedTime"
////
//            );


            org.joda.time.Duration duration2 = new Duration(currentTime, dtDateReqAcceptedTime_);
            Log.d(TAG, "" +

                    "\ncurrent time: " + currentTime
                    + "\n dtDate req accpeted time: " + dtDateReqAcceptedTime_ +

                    "\n duration in min: " + duration2.getStandardMinutes());

            //  acommodationAlreadyCancled = false;


            if (acommodationAlreadyCancled==false) {

                if (-duration2.getStandardMinutes() >= 1) {
                    Log.d(TAG, "time up");
                    textViewRemaingTime.setText("00:00:00");
                    cancelAccomodation();
                }
                else {

                    long remainningTime = 44 + duration2.getStandardMinutes();
                    Log.d(TAG, "time remaninig: " + remainningTime);


                    long remaining_sec = 60 + (duration2.getStandardSeconds() % 60);

                    textViewRemaingTime.setText("00:" + remainningTime + ":" + remaining_sec);
                }
            } else{
                startActivity(new Intent(ConfirmOccupancy.this,ConfirmOccupancy.class));

            }


        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e.toString());
        }
    }

    private void cancelAccomodation() {
        //delete online folk boy notification
        firestore.collection("FolkBoyNotifications")
                .document(currentFolkBoy)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "folk boy noti deleted", Toast.LENGTH_SHORT).show();
                        acommodationAlreadyCancled = true;


//                        getActivity().finish();
//                        getFragmentManager()
//                                .beginTransaction()
//
//                                .replace(R.id.main_container, new RequestAccomodationFragment())
//                                .commit();
                    }
                });

        //delete booked bed
        firestore.collection("BookedBed")
                .document()
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "booked bed deleted", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "booked bed deleted" );
                        acommodationAlreadyCancled = true;

                    }
                });

        final String[] folk_guide_id = {""};
        //delete online fb notification from Folk guide's notification
        firestore.collection("Users").document(currentFolkBoy)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        folk_guide_id[0]
                                = documentSnapshot.getString("folk_guide_id");

                        if (folk_guide_id[0].equals("")) {
                            Toast.makeText(mContext, "folk Guide not assigned", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "deleting noti from fg's " + folk_guide_id[0], Toast.LENGTH_SHORT).show();

                            //delete fb's noti from fg's noti
                            firestore.collection("FolkGuideNotifications")
                                    .document(folk_guide_id[0])
                                    .collection("Notifications")
                                    .document(currentFbEmail + currentFolkBoy)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(mContext, "fb's noti from fg's noti deleted", Toast.LENGTH_SHORT).show();
                                            acommodationAlreadyCancled = true;
                                        }
                                    });

                        }
                    }
                });

        //
    }


}
