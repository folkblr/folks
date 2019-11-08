package com.creation.android.folkapp2019;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private Toolbar toolbar;

    Button sign_out_btn;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private String user_id;
    private TextView username_tv;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user_id=mAuth.getCurrentUser().getUid();
            username_tv=view.findViewById(R.id.username_id);
        db.collection("FolkMember")
                .document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String user_name=documentSnapshot.getString("name");
                username_tv.setText(user_name);
            }
        });

        sign_out_btn=view.findViewById(R.id.sign_out_btn_id);
        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> tokenMapRemove = new HashMap<>();
                tokenMapRemove.put("folk_guide_token_id", FieldValue.delete());

                db.collection("FolkGuide").document(user_id).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseAuth.getInstance().signOut();
                        //getActivity().finish();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);


                    }
                });
                         }
        });
        toolbar =view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {


        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.Accomodation_icon_id) {
            Toast.makeText(requireContext(), "Accomodation", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(),RequestAccomodationActivity.class));

            //return true;
        }
        if (id == R.id.item2_id) {
            Toast.makeText(requireContext(), "Item 2", Toast.LENGTH_SHORT).show();
            //return true;
        }
        if (id == R.id.more_id) {
            Toast.makeText(requireContext(), "More", Toast.LENGTH_SHORT).show();
            //return true;
        }
        return super.onOptionsItemSelected(item);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_fragment_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
