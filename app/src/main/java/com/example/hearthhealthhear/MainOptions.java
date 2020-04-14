package com.example.hearthhealthhear;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainOptions extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    TextView username;
    FirebaseAuth firebaseAuth;
    FirebaseUser current_user;
    String current_user_name;
    String current_user_email;
    String displayname="Health AI";
    private Toolbar mytoolbar;
    Button heart_button;
    List<String> durls;
    private ProgressDialog mProgress;
    String fold_name;

    FirebaseDatabase firebaseDatabase;
    //heart
    DatabaseReference databaseReference_htmp;
    DatabaseReference databaseReference_h;
    ChildEventListener childEventListener_htmp,childEventListener_h;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_options);
        mytoolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        mProgress = new ProgressDialog(this);
        String fold_name;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference_htmp = firebaseDatabase.getReference("heartAll_tmp");
        databaseReference_h = firebaseDatabase.getReference("heartAll");

        username = (TextView)findViewById(R.id.user_name);
        username.setText(displayname);
        heart_button = (Button)findViewById(R.id.heart_button);
        heart_button.setText("heart");
        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser();
        current_user_name = current_user.getDisplayName();


    }





    // for option menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1,menu);

        if (current_user.getEmail().equals("salehmaheen@gmail.com") || current_user.getEmail().equals("samarjahan01n1965@gmail.com")){
            inflater.inflate(R.menu.menu_profile,menu);
            inflater.inflate(R.menu.sync,menu);
        }
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                Intent tosignin  = new Intent(MainOptions.this, Signin.class);
                Toast.makeText(MainOptions.this, "logging out", Toast.LENGTH_SHORT).show();
                startActivity(tosignin);
                return true;
            case R.id.dHeart:
                detach_heart_listenrs();
                namefolder_h();

                return true;

            case R.id.dLungs:
                return true;

            case R.id.sHeart:
                detach_heart_listenrs();

                syncHeart();


            default:
                return super.onContextItemSelected(item);
        }
    }

    private void syncHeart() {

        childEventListener_h = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                recorded_file newdata = dataSnapshot.getValue(recorded_file.class);
                databaseReference_htmp.push().setValue(newdata);
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

        mProgress.setMessage("Heart Sync ...");
        mProgress.show();
        databaseReference_h.addChildEventListener(childEventListener_h);


    }

    public void select_heart(View view) {
        Intent intenttoheart = new Intent(MainOptions.this,Heart.class);
        intenttoheart.putExtra("username",displayname);
        startActivity(intenttoheart);
    }

    public void select_lungs(View view) {
        Intent intenttolungs = new Intent(MainOptions.this,Lungs.class);
        intenttolungs.putExtra("username",displayname);
        startActivity(intenttolungs);
    }

    public void select_records(View view) {
        Intent intenttorecords = new Intent(MainOptions.this,RecordsPopUp.class);
        intenttorecords.putExtra("username",displayname);
        startActivity(intenttorecords);
    }

    public void select_about(View view) {
        Intent intenttoabout = new Intent(MainOptions.this,About.class);
        intenttoabout.putExtra("username",displayname);
        startActivity(intenttoabout);
    }

    public String namefolder_h() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.foldername_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        dialogBuilder.getWindow().setLayout(800, 600);


        final EditText editText = (EditText) dialogView.findViewById(R.id.foldername_edittext);
        Button button1 = (Button) dialogView.findViewById(R.id.foldername_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fold_name = editText.getText().toString();
                dialogBuilder.dismiss();
                Toast.makeText(MainOptions.this,"Your files will be downloaded soon !",Toast.LENGTH_LONG).show();
                load_data_h(fold_name);
            }
        });

        return fold_name;

    }


    public void load_data_h(String setname){


        Toast.makeText(MainOptions.this,"loading data .....",Toast.LENGTH_SHORT).show();
        childEventListener_htmp = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                recorded_file newdata = dataSnapshot.getValue(recorded_file.class);
                String u = newdata.getFile_path().toString();
                download_File(MainOptions.this,newdata.getFile_name(),".mp3",DIRECTORY_DOWNLOADS+"/HEART/"+setname+"/",u);
                databaseReference_htmp.child(dataSnapshot.getKey()).setValue(null);
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
        databaseReference_htmp.addChildEventListener(childEventListener_htmp);
        databaseReference_htmp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null){
                    Toast.makeText(MainOptions.this,"All data downloaded !",Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void download_File(Context context, String filename, String ext,String dest,String link) {

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(link);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, dest, filename + ext);
        downloadManager.enqueue(request);

    }

    @Override
    protected void onPause() {
        super.onPause();
        detach_heart_listenrs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgress = new ProgressDialog(this);
        String fold_name;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference_htmp = firebaseDatabase.getReference("heartAll_tmp");
        databaseReference_h = firebaseDatabase.getReference("heartAll");

        username = (TextView)findViewById(R.id.user_name);
        username.setText(displayname);
        heart_button = (Button)findViewById(R.id.heart_button);
        heart_button.setText("heart");
        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser();
        current_user_name = current_user.getDisplayName();    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void detach_heart_listenrs(){
        if (childEventListener_h!=null) {
            databaseReference_h.removeEventListener(childEventListener_h);
            childEventListener_h=null;
        }

        if (childEventListener_htmp!=null) {
            databaseReference_htmp.removeEventListener(childEventListener_htmp);
            childEventListener_htmp=null;
        }
    }
}
