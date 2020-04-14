package com.example.hearthhealthhear;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    Button dbutton;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<String> existing;

    DriveServiceHelper driveServiceHelper;
    GoogleApiClient mGoogleApiClient;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_options);
        mytoolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        mProgress = new ProgressDialog(this);
        dbutton = (Button)findViewById(R.id.getdata);
        dbutton.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("heart").child(firebaseAuth.getUid());


//
        username = (TextView)findViewById(R.id.user_name);
        username.setText(displayname);
        heart_button = (Button)findViewById(R.id.heart_button);
        heart_button.setText("heart");
        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser();
        current_user_name = current_user.getDisplayName();


        System.out.println("000000000000000000000000000000000");
        System.out.println("usernae is "+current_user_name);
        System.out.println("email is "+current_user.getEmail());
        if (current_user.getEmail().equals("salehmaheen@gmail.com")){
            dbutton.setVisibility(View.VISIBLE);
//            requestSignin();

        }

        File exiting = getExternalFilesDir(DIRECTORY_DOWNLOADS);
        existing = new ArrayList<>();
        for (int ii=0;ii<exiting.listFiles().length;ii++){
            System.out.println("folder "+ exiting.listFiles()[ii]);
            for (int iii=0;iii<exiting.listFiles()[ii].listFiles().length;iii++){
                System.out.println(exiting.listFiles()[ii].listFiles()[iii].getName());
                existing.add(exiting.listFiles()[ii].listFiles()[iii].getName());
            }
        }
//        System.out.println("exiting ++++"+exiting.listFiles());
//        existing = new ArrayList<>();
//        for (int i= 0 ;i<exiting.listFiles().length;i++){
//
//            existing.add(exiting.listFiles()[i].getName());
//
//        }
        System.out.println("amount "+existing.size());
        System.out.println("existing "+existing);




//        if (current_user_name==null || current_user_name.equals("")){
////            current_user_email= current_user.getEmail();
////            username.setText(current_user_email);
////            displayname=current_user_email;
//            String email_id = current_user.getEmail();
//            int inde = 0;
//            for (int i =0; i<email_id.length();i++){
//                System.out.println("---------------------------");
//                System.out.println("this "+email_id.charAt(i));
//                if (email_id.substring(i,i+1).equals("@")){
//                    System.out.println("got it");
//                    inde = i;
//                    break;
//                }
//            }
//
//            current_user_email = current_user.getEmail();
//            displayname = email_id.substring(0,inde);
//            username.setText(displayname);
//
//        }
//        else if (current_user_email==null) {
//            username.setText(current_user_name);
//            displayname=current_user_name;
//        }
//        else{
//            username.setText("welcome user");
//        }


    }

    //for drive

    private void requestSignin() {

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE ))
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this,signInOptions);
        startActivityForResult(client.getSignInIntent(),1);



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case 1:
                if (resultCode==RESULT_OK){

                    handleSignInIntent(data);
                }

                break;
        }
    }

    private void handleSignInIntent(Intent data) {

        GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {

                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(MainOptions.this, Collections.singleton((DriveScopes.DRIVE_FILE)));
                credential.setSelectedAccount(googleSignInAccount.getAccount());

                Drive googleDriveServices = new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName("admindrive")
                        .build();

                driveServiceHelper = new DriveServiceHelper(googleDriveServices);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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
                Intent tosignin  = new Intent(MainOptions.this, Signin.class);
                Toast.makeText(MainOptions.this, "logging out", Toast.LENGTH_SHORT).show();
                startActivity(tosignin);
            default:
                return super.onContextItemSelected(item);
        }
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

    public void getdata(View view) {

//        File exiting = getExternalFilesDir(DIRECTORY_DOWNLOADS+"/health/");
//        System.out.println("exiting ++++"+exiting.listFiles());
//        for (int i= 0 ;i<exiting.listFiles().length;i++){
//
//            System.out.println(i+" "+exiting.listFiles()[i]);
//
//        }


        Toast.makeText(MainOptions.this,"loading data .....",Toast.LENGTH_SHORT).show();
//        ArrayAdapter<recorded_file> heart_history;
//        heart_history= new ArrayList<recorded_file>();
        durls =new ArrayList<>();
//        adapter = new heart_adapter(this,R.layout.heart_listview,heart_history);
//        mylistview = (ListView)findViewById(R.id.heart_history_listview) ;
//        mylistview.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("lungs").child(firebaseAuth.getUid());
        ChildEventListener childEventListener;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                recorded_file newdata = dataSnapshot.getValue(recorded_file.class);
//                String some = newdata.getAddress();
//                tester.setText("Address "+some);
                durls.add(newdata.getFile_path());
                String u = newdata.getFile_path().toString();
//                driveServiceHelper.createfile(newdata.getFile_path(),newdata.getFile_name());
                if (!existing.contains(newdata.getFile_name()+".mp3")){
                    System.out.println("yessssss");
                    System.out.println("boolena "+existing.contains(newdata.getFile_name()+".mp3"));
                    System.out.println("filename "+newdata.getFile_name());
                    System.out.println("downloading this "+newdata.getFile_name());
                    download_File(MainOptions.this,newdata.getFile_name(),".mp3",DIRECTORY_DOWNLOADS+"/health1/",u);
                    existing.add(newdata.getFile_name()+".mp3");
                }
                System.out.println("running");


//                adapter.add(newdata);
//                dataSnapshot.getKey();
                mProgress.dismiss();
                System.out.println("urls "+durls);
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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null){
                    Toast.makeText(MainOptions.this,"No Past Records !",Toast.LENGTH_SHORT).show();
                    System.out.println("no urls to frtch");
//                    mProgress.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void download_File(Context context, String filename, String ext,String dest,String link){

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(link);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,dest,filename+ext);
        downloadManager.enqueue(request);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
