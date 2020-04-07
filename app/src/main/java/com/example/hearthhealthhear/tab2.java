package com.example.hearthhealthhear;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class
tab2 extends Fragment {

    private TextView tester;
    private ChildEventListener childEventListener;
    private List<recorded_file> lungs_history;
    private lungs_adapter adapter;
    private ListView mylistview;


    Integer entries;

    Intent gotoprop;

    private OnFragmentInteractionListener mListener;

    public tab2() {
        // Required empty public constructor
    }

    public static tab2 newInstance(String param1, String param2) {
        tab2 fragment = new tab2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    // MAIN WORK.//
    @Override  // this method shows the content of the  fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);
//        tester = (TextView) view.findViewById(R.id.);
//        tester.setText("maheen");

//        databaseReference
        List<recorded_file> props = new ArrayList<>();
        adapter = new lungs_adapter(getContext(),R.layout.lungs_listview,props);
        mylistview = (ListView) view.findViewById(R.id.lung_history_listview) ;
        mylistview.setAdapter(adapter);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("lungs").child(firebaseAuth.getUid());
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                recorded_file newdata = dataSnapshot.getValue(recorded_file.class);
//                String some = newdata.getAddress();
//                tester.setText("Address "+some);
                adapter.add(newdata);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);







        return view;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
