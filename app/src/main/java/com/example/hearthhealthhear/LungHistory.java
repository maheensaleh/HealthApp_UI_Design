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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LungHistory extends AppCompatActivity {

    String displayname;
    TextView username;
    private Toolbar mytoolbar;
    private List<String> keys;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private ChildEventListener childEventListener;
    private List<recorded_file> heart_history;
    private heart_adapter adapter;
    private ListView mylistview;
    Boolean resume_status=false;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lung_history);
        mProgress = new ProgressDialog(this);

        mytoolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);
//
        username = (TextView)findViewById(R.id.user_name);

        final Intent getdisplayname = getIntent();
        displayname = getdisplayname.getStringExtra("username");
        username.setText("Heart AI");

        load_data();


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
                Intent tosignin  = new Intent(LungHistory.this, Signin.class);
                Toast.makeText(LungHistory.this, "logging out", Toast.LENGTH_SHORT).show();
                startActivity(tosignin);
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void load_data ( ){

        Toast.makeText(LungHistory.this,"loading data .....",Toast.LENGTH_SHORT).show();
        heart_history= new ArrayList<>();
        keys =new ArrayList<>();
        adapter = new heart_adapter(this,R.layout.lungs_listview,heart_history);
        mylistview = (ListView)findViewById(R.id.lungs_history_listview) ;
        mylistview.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("lungs").child(firebaseAuth.getUid());
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot dataSnapshot,  String s) {

                recorded_file newdata = dataSnapshot.getValue(recorded_file.class);
                Toast.makeText(LungHistory.this,"value is "+newdata.toString(),Toast.LENGTH_LONG).show();
                    keys.add(dataSnapshot.getKey());
                    adapter.add(newdata);
                    dataSnapshot.getKey();

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
                if (dataSnapshot.getValue()==null){
                }
                else{
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mProgress.setMessage("Loading records ...");
        mProgress.show();
        databaseReference.addChildEventListener(childEventListener);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null){
                    Toast.makeText(LungHistory.this,"No Past Records !",Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent goto_thisrecord = new Intent(LungHistory.this,HistorySingleEntry.class);
                goto_thisrecord.putExtra("file_name", heart_history.get(i).file_name);
                goto_thisrecord.putExtra("file_path", heart_history.get(i).file_path);
                goto_thisrecord.putExtra("username",displayname);
                goto_thisrecord.putExtra("key",keys.get(i));
                goto_thisrecord.putExtra("type","lungs");
                System.out.println("sending " + heart_history.get(i)  );
                resume_status = true;
                startActivity(goto_thisrecord);
                finish();
            }
        });
    }

}
