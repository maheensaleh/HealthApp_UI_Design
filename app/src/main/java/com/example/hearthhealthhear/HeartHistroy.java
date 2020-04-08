package com.example.hearthhealthhear;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HeartHistroy extends AppCompatActivity {

    String displayname;
    TextView username;
    private Toolbar mytoolbar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private ChildEventListener childEventListener;
    private List<recorded_file> heart_history;
    private heart_adapter adapter;
    private ListView mylistview;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_histroy);

        mProgress = new ProgressDialog(this);

        mytoolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);
//
        username = (TextView)findViewById(R.id.user_name);

        final Intent getdisplayname = getIntent();
        displayname = getdisplayname.getStringExtra("username");
        username.setText(displayname);


        heart_history= new ArrayList<>();
        adapter = new heart_adapter(this,R.layout.heart_listview,heart_history);
        mylistview = (ListView)findViewById(R.id.heart_history_listview) ;
        mylistview.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("heart").child(firebaseAuth.getUid());
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                recorded_file newdata = dataSnapshot.getValue(recorded_file.class);
//                String some = newdata.getAddress();
//                tester.setText("Address "+some);
                adapter.add(newdata);
                mProgress.dismiss();
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

        mProgress.setMessage("Loading records ...");
        mProgress.show();
        databaseReference.addChildEventListener(childEventListener);


        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent goto_thisrecord = new Intent(HeartHistroy.this,HistorySingleEntry.class);
                goto_thisrecord.putExtra("file_name", heart_history.get(i).file_name);
                goto_thisrecord.putExtra("file_path", heart_history.get(i).file_path);
                goto_thisrecord.putExtra("username",displayname);
                System.out.println("sending " + heart_history.get(i)  );
                startActivity(goto_thisrecord);
            }
        });






    }

    // for option menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1,menu);
        return super.onCreateOptionsMenu(menu);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                Intent tosignin  = new Intent(HeartHistroy.this, Signin.class);
                Toast.makeText(HeartHistroy.this, "logging out", Toast.LENGTH_SHORT).show();
                startActivity(tosignin);
            default:
                return super.onContextItemSelected(item);
        }
    }

}
