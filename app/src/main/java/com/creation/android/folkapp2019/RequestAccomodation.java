package com.creation.android.folkapp2019;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kofigyan.stateprogressbar.StateProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestAccomodation extends Fragment {

    String[] descriptionData = {"Request", "Awating\nApproval", "Confirm\nOccupancy",};

    public RequestAccomodation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_request_accomodation, container, false);
        final StateProgressBar stateProgressBar = (StateProgressBar)view.findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);

        Button send_request=view.findViewById(R.id.send_request);
        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                FragmentTransaction fr=getFragmentManager().beginTransaction();
                fr.replace(R.id.main_container,new ConfirmOccupancy());
                fr.commit();         }
        });

        return view;
    }

}
