package com.creation.android.folkapp2019;


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
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    private Toolbar toolbar;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
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
        if (id == R.id.item1_id) {
            Toast.makeText(requireContext(), "Item 1", Toast.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.home_fragment_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
