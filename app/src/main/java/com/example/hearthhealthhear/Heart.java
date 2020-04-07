package com.example.hearthhealthhear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anand.brose.graphviewlibrary.GraphView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

public class Heart extends AppCompatActivity {

    private Toolbar mytoolbar;
    TextView username_view;
    String displayname;
    private ProgressDialog mProgress;
    EditText file_name_get;


    //for firebase
    public FirebaseDatabase firebaseDatabase;
    public DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;


    //for heart recording and wave
    public static final String SCALE = "scale";
    public static final String OUTPUT_DIRECTORY = "VoiceRecorder";
    public static final String OUTPUT_FILENAME = "recorder.mp3";
    private static final int MY_PERMISSIONS_REQUEST_CODE = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    int scale = 8;
    private GraphView graphView;
    private VoiceRecorder recorder;
    private List samples;
    private boolean is_paused = false;
    private Button pause_resume,Brecord_heart,Bstop_heart,Btest_heart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        mProgress = new ProgressDialog(this);
        file_name_get = (EditText)findViewById(R.id.file_name_edittext);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("heart").child(firebaseAuth.getUid());
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("heartRecordings");
//
//        prog_bar = (ProgressBar) findViewById(R.id.saving_recording_progress);
//        prog_bar.setVisibility(View.INVISIBLE);

        pause_resume = (Button)findViewById(R.id.pause_resume_heart_record_button);
        Brecord_heart = (Button)findViewById(R.id.record_heart_button);
        Bstop_heart = (Button)findViewById(R.id.stop_heart_record_button);
        Btest_heart = (Button)findViewById(R.id.test_heart_button);
        Btest_heart.setEnabled(false);
        Bstop_heart.setEnabled(false);
        pause_resume.setEnabled(false);

        mytoolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        Intent getdisplayname = getIntent();
        displayname = getdisplayname.getStringExtra("username");

        username_view=(TextView)findViewById(R.id.user_name);
        username_view.setText(displayname);

        //for heart recording and waveform
        graphView = (GraphView) findViewById(R.id.graphView);
        graphView.setGraphColor(Color.rgb(30,136,229));
        graphView.setCanvasColor(Color.rgb(255,255,255));
        graphView.setTimeColor(Color.rgb(20, 136, 229));
        recorder = VoiceRecorder.getInstance();
        if (recorder.isRecording()) {
            ((Button) findViewById(R.id.control)).setText(getResources().getString(R.string.stop));
            recorder.startPlotting(graphView);
        }
        if (savedInstanceState != null) {
            scale = savedInstanceState.getInt(SCALE);
            graphView.setWaveLengthPX(scale);
            if (!recorder.isRecording()) {
                samples = recorder.getSamples();
                graphView.showFullGraph(samples);
            }
        }

    }

    //-------- following functions are for heart recording and waveform---------//


    public void record_heart(View view) {

        if (file_name_get.getText().equals("")) {
            Toast.makeText(Heart.this, "Enter recording name to proceed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Heart.this, "recording audio", Toast.LENGTH_SHORT).show();
            if (checkRecordPermission() && checkStoragePermission()) {

                graphView.reset();
                String filepath = Environment.getExternalStorageDirectory().getPath();
                File file = new File(filepath, OUTPUT_DIRECTORY);
                if (!file.exists()) {
                    file.mkdirs();
                }
                recorder.setOutputFilePath(file.getAbsoluteFile() + "/" + file_name_get.getText()+".mp3");
                recorder.startRecording();
                recorder.startPlotting(graphView);
                Brecord_heart.setEnabled(false);
                Bstop_heart.setEnabled(true);
                pause_resume.setEnabled(true);

            } else {
                requestPermissions();
            }
        }
    }



    public void pause_resume_heart(View view) {

        if (!is_paused){
            is_paused=true;
            Toast.makeText(Heart.this,"Recording Paused !",Toast.LENGTH_SHORT);
            recorder.pauseRecording();// to pause recording
            pause_resume.setText("resume");
        }

        else{
            is_paused=false;
            Toast.makeText(Heart.this,"Recording Resumed !",Toast.LENGTH_SHORT);
            recorder.continueRecording();//to resume recording
            pause_resume.setText("pause");
        }

    }


    public void stop_heart(View view) {
            graphView.stopPlotting();
            samples = recorder.stopRecording();
            graphView.showFullGraph(samples);
            Brecord_heart.setEnabled(true);
            Bstop_heart.setEnabled(false);
            pause_resume.setEnabled(false);
            Btest_heart.setEnabled(true);
    }


    public void test_heart(View view) {


        mProgress.setMessage("Saving audio");
        mProgress.show();
        final Uri path ;

        Toast.makeText(Heart.this, "testing heart for diseasse...", Toast.LENGTH_SHORT).show();
        Uri recording_uri = recorder.get_recording_uri();
        System.out.println("path is   " + recording_uri);
        final StorageReference audio_ref = storageReference.child(recording_uri.getLastPathSegment());
        audio_ref.putFile(recording_uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                audio_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        recorded_file for_database = new recorded_file(file_name_get.getText().toString(),uri.toString());
                        Toast.makeText(Heart.this, "Recording saved !", Toast.LENGTH_SHORT).show();
                        databaseReference.push().setValue(for_database);
                        mProgress.dismiss();
                        Intent showResult = new Intent(Heart.this,Result.class);
                        showResult.putExtra("displayname",displayname);
                        startActivity(showResult);
                    }
                });
            }
        });

        finishActivity(0);


    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SCALE, scale);
        super.onSaveInstanceState(outState);
    }

    public void requestPermissions(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CODE);

        } else {
            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CODE);
            // MY_PERMISSIONS_REQUEST_CODE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    private boolean checkRecordPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }



//    ------------------


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
                Intent tosignin  = new Intent(Heart.this, Signin.class);
                Toast.makeText(Heart.this, "logging out", Toast.LENGTH_SHORT).show();
                startActivity(tosignin);
            default:
                return super.onContextItemSelected(item);
        }
    }


}
