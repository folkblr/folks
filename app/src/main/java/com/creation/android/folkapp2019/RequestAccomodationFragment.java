package com.creation.android.folkapp2019;


import android.content.Context;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestAccomodationFragment extends Fragment {

    String[] descriptionData = {"Request", "Awating\nApproval", "Confirm\nOccupancy",};
    Button send_request;
    RadioGroup select_berth_rg;
    RadioButton berth_rb;
    private String berth = "";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String mUserName;
    private String mUserId;
    private String mCurrentId;

    private EditText mMessageView;
    private ProgressBar mMessageProgress;

    public RequestAccomodationFragment() {
        // Required empty public constructor
    }

    // Declare Context variable at class level in Fragment
    private Context mContext;

    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mUserId = mAuth.getCurrentUser().getUid();
        // mUserName=mAuth.getCurrentUser()


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.activity_request_accomodation, container, false);
        final StateProgressBar stateProgressBar = (StateProgressBar) view.findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);

        send_request = view.findViewById(R.id.send_request);
        select_berth_rg = view.findViewById(R.id.select_berth_rg);

        mMessageView = (EditText) view.findViewById(R.id.request_note_et);
        // mMessageProgress = (ProgressBar) view.findViewById(R.id.messageProgress);

        db = FirebaseFirestore.getInstance();
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
                        Toast.makeText(getActivity(), "Berth : " + berth, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.middle_rb:
                        berth = "MIDDLE.....";
                        Toast.makeText(getActivity(), "Berth : " + berth, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.lower_rb:
                        berth = "LOWER";
                        Toast.makeText(getActivity(), "Berth : " + berth, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        //Toast.makeText(getActivity(), "Berth : " + berth, Toast.LENGTH_SHORT).show();


        //mUserId =getActivity().getIntent().getStringExtra("user_id");
        //mUserName = getActivity().getIntent().getStringExtra("user_name");

        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = mMessageView.getText().toString();
                //berth = berth_rb.getText().toString();
                //mMessageProgress.setVisibility(View.VISIBLE);
                db.collection("FolkMember")
                        .document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final String user_name = documentSnapshot.getString("name");
                        Map<String, Object> notificationMessage = new HashMap<>();
                        notificationMessage.put("message", message);
                        notificationMessage.put("from", user_name);
                        //notificationMessage.put("Name: ",)
                        notificationMessage.put("Berth", berth);


                        db.collection("FolkMember/" + mUserId + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                Toast.makeText(requireContext(), "Notification Sent.", Toast.LENGTH_LONG).show();
                                mMessageView.setText("");
                                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                                FragmentTransaction fr = getFragmentManager().beginTransaction();
                                //fr.replace(R.id.main_container, new ConfirmOccupancy());
                                fr.commit();
                                // mMessageProgress.setVisibility(View.INVISIBLE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(requireContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                //mMessageProgress.setVisibility(View.INVISIBLE);

                            }
                        });
                    }
                });



            }
        });

        return view;
    }
}
