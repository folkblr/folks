package com.creation.android.folkapp2019;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RequestAccomodationActivity extends AppCompatActivity {
    private String berth = "";
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    private String mUserName;
    private String mUserId;
    private String mCurrentId;

    private EditText mMessageView;
    private ProgressBar mMessageProgress;
    String[] descriptionData = {"Request", "Awating\nApproval", "Confirm\nOccupancy",};
    Button send_request;
    RadioGroup select_berth_rg;
    RadioButton berth_rb;

    String userEmail;
    StateProgressBar stateProgressBar;

    String folkGuideId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_accomodation);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mUserId = mAuth.getCurrentUser().getUid();
        mUserName = mAuth.getCurrentUser().getDisplayName();
        userEmail = mAuth.getCurrentUser().getEmail();
        // mUserName=mAuth.getCurrentUser()
        Toast.makeText(RequestAccomodationActivity.this, "userName : " + mUserName, Toast.LENGTH_SHORT).show();




        final StateProgressBar stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);

        send_request = findViewById(R.id.send_request);
        select_berth_rg = findViewById(R.id.select_berth_rg);

        mMessageView = (EditText) findViewById(R.id.request_note_et);
        // mMessageProgress = (ProgressBar) view.findViewById(R.id.messageProgress);

        mFirestore = FirebaseFirestore.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
        mCurrentId = mAuth.getCurrentUser().getEmail();
        final String user_id = mAuth.getCurrentUser().getUid();


        select_berth_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup select_berth_rg, @IdRes int i) {
                berth_rb = select_berth_rg.findViewById(i);
                berth = berth_rb.getText().toString();
                switch (i) {
                    case R.id.upper_rb:
                        berth = "UPPER Berth";
                        Toast.makeText(RequestAccomodationActivity.this, "Berth : " + berth, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.middle_rb:
                        berth = "MIDDLE Berth";
                        Toast.makeText(RequestAccomodationActivity.this, "Berth : " + berth, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.lower_rb:
                        berth = "LOWER Berth";
                        Toast.makeText(RequestAccomodationActivity.this, "Berth : " + berth, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        checkIfAccomodationAlreadyRequested();


        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = mMessageView.getText().toString();
                //berth = berth_rb.getText().toString();
                //mMessageProgress.setVisibility(View.VISIBLE);
                mFirestore.collection("FolkMember")
                        .document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final String user_name = documentSnapshot.getString("name");
                        Map<String, Object> notificationMessage = new HashMap<>();
                        notificationMessage.put("message", message);
                        notificationMessage.put("from", user_name);
                        //notificationMessage.put("Name: ",)
                        notificationMessage.put("Berth", berth);


                        mFirestore.collection("FolkMember/" + mUserId + "/Notifications")
                                .add(notificationMessage)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                Toast.makeText(RequestAccomodationActivity.this, "Notification Sent.", Toast.LENGTH_LONG).show();
                                mMessageView.setText("");
                                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                                Intent intent=new Intent(RequestAccomodationActivity.this,AwatingApproval.class);
                                startActivity(intent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(RequestAccomodationActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                //mMessageProgress.setVisibility(View.INVISIBLE);

                            }
                        });
                    }
                });
                final String[] folk_guide_id = {""};

                // get folk boy's folk guide id.
                mFirestore.collection("FolkMember").document(mUserId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                folk_guide_id[0]
                                        = documentSnapshot.getString("folk_guide_id");

                                if (folk_guide_id[0].equals("")) {
                                    Toast.makeText(RequestAccomodationActivity.this, "folk Guide not assigned", Toast.LENGTH_SHORT).show();
                                } else {
                                    sendNotificationToFolkGuide(folk_guide_id);
                                    Toast.makeText(RequestAccomodationActivity.this, "folk guide id" + folk_guide_id[0], Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



            }

        });


    }
    private void checkIfAccomodationAlreadyRequested() {
//        getFolkGuideIdOfOnlineFolkBoy();
//        Log.d(TAG, "checkIfAccomodationAlreadyRequested: in");
//
//        if(folkGuideId==null){
//            Log.d(TAG, "checkIfAccomodationAlreadyRequested: folkGuideId(null) : " + folkGuideId);
//        }
//        else{
//            getFolkGuideIdOfOnlineFolkBoy();
//            Log.d(TAG, "checkIfAccomodationAlreadyRequested: folkGuideId (not NUll):  " + folkGuideId);
//        }
//
//        Log.d(TAG, "checkIfAccomodationAlreadyRequested: folkGuideId: "+ folkGuideId);
        DocumentReference folkBoyDocRef ;
        folkBoyDocRef = mFirestore.collection("FolkMember").document(mUserId);


        final String[] folk_guide_id = {""};
        folkBoyDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        folk_guide_id[0]
                                = documentSnapshot.getString("folk_guide_id");

                        if (folk_guide_id[0].equals("")) {
                            Toast.makeText(RequestAccomodationActivity.this, "folk Guide not assigned", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RequestAccomodationActivity.this, "folk guide id" + folk_guide_id[0], Toast.LENGTH_SHORT).show();

                            //progressBar
                            final ProgressDialog progressDialog = new ProgressDialog(RequestAccomodationActivity.this);
                            progressDialog.setTitle("Checking Database");
                            progressDialog.show();
                            //


                            CollectionReference folkGuideNotificationColRef
                                    = mFirestore.collection("FolkGuideNotifications")
                                    .document(folk_guide_id[0])
                                    .collection("Notifications");

                            DocumentReference folkGuideNotificationDocRef
                                    = mFirestore.collection("FolkGuideNotifications")
                                    .document(folk_guide_id[0])
                                    .collection("Notifications")
                                    .document(userEmail + mUserId);

                            final String fb_id = mAuth.getCurrentUser().getUid();

                            folkGuideNotificationDocRef
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {


                                                Toast.makeText(RequestAccomodationActivity.this, "accomodation alreday requested", Toast.LENGTH_SHORT).show();

                                                //stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);

                                                startActivity(new Intent(RequestAccomodationActivity.this,ConfirmOccupancy.class));

                                                progressDialog.dismiss();
                                            } else {
                                                Toast.makeText(RequestAccomodationActivity.this, "doc, doesn't exist, you can request accomodatation", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    })
//
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e);
                                        }
                                    });


                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequestAccomodationActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                    }
                });
        //

//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setTitle("Checking Database");
//        progressDialog.show();

//        CollectionReference folkGuideNotificationColRef
//                = mFirestore.collection("FolkGuideNotifications")
//                .document(folk_guide_id[0])
//                .collection("Notifications");

        String fb_id = mAuth.getCurrentUser().getUid();
//
//        folkGuideNotificationColRef
//                .whereEqualTo("fb_id", fb_id)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        Toast.makeText(RequestAccomodationActivity.this, "Accomodation Already Requested", Toast.LENGTH_SHORT).show();
//
////                        FragmentTransaction fr = getFragmentManager().beginTransaction();
////                        fr.replace(R.id.main_container, new AwaitingApprovalFragment());
////                        fr.commit();
//
//                        progressDialog.dismiss();
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: " + e);
//                    }
//                });


    }

    private void getFolkGuideIdOfOnlineFolkBoy() {

        if(folkGuideId==null){
            Log.d(TAG, "getFolkGuideIdOfOnlineFolkBoy: folkguideId: " + folkGuideId);
        }
        else{
            getFolkGuideIdOfOnlineFolkBoy();
            Log.d(TAG, "getFolkGuideIdOfOnlineFolkBoy: folkGuideId(not null) : " + folkGuideId);
        }

        mFirestore.collection("FolkMember").document(mUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        folkGuideId
                                = documentSnapshot.getString("folk_guide_id");

                        if (folkGuideId.equals("")) {
                            Toast.makeText(RequestAccomodationActivity.this, "folk Guide not assigned", Toast.LENGTH_SHORT).show();
                        } else {

//                            checkIfAccomodationAlreadyRequested(folk_guide_id);
//                            sendNotificationToFolkGuide(folk_guide_id, notificationMessage);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequestAccomodationActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void sendNotificationToFolkGuide(String[] folk_guide_id) {


        String message = mMessageView.getText().toString();
        Map<String, Object> notificationMessage = new HashMap<>();

        notificationMessage.put("fb_id", mUserId);
        notificationMessage.put("fb_message", message);
        notificationMessage.put("fb_email", mCurrentId);
        //notificationMessage.put("Name: ",)
        notificationMessage.put("fb_berth_pref", berth);


        mFirestore.collection("FolkGuideNotifications/" + folk_guide_id[0] + "/Notifications")
                .document(userEmail + mUserId)
                .set(notificationMessage);
        Toast.makeText(RequestAccomodationActivity.this, "" + notificationMessage, Toast.LENGTH_SHORT).show();
    }

}
