package com.example.hearthhealthhear;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
//import com.android.volley.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
//import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Heart extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private Toolbar mytoolbar;
    TextView username_view;
    String displayname;
    private ProgressDialog mProgress;
    EditText file_name_get;
    String edit_box_name = null;


    //for firebase
    public FirebaseDatabase firebaseDatabase;
    public DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference heartAll;
    private DatabaseReference heartAll_tmp;


    //for current location
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    List<Address> address;
    LatLng latLng;
    Boolean gotlocation = false;



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
    File file;
    Uri recording_uri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        initialize_gps();

        mProgress = new ProgressDialog(this);
        file_name_get = (EditText)findViewById(R.id.file_name_edittext);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("heart").child(firebaseAuth.getUid());
        heartAll = firebaseDatabase.getReference("heartAll");
        heartAll_tmp = firebaseDatabase.getReference("heartAll_tmp");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("heartRecordings");

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

      if (file_name_get.getText().toString().trim().equals("")) {
        Toast.makeText(Heart.this, "Enter recording name to proceed", Toast.LENGTH_SHORT).show();
      } else {
            System.out.println("filename-----------"+file_name_get.getText().toString());
            edit_box_name = file_name_get.getText().toString();
            Toast.makeText(Heart.this, "recording audio", Toast.LENGTH_SHORT).show();
            if (checkRecordPermission() && checkStoragePermission()) {

                graphView.reset();
                String filepath = Environment.getExternalStorageDirectory().getPath();
                file = new File(filepath, OUTPUT_DIRECTORY);
                Date ctime  = (Date) Calendar.getInstance().getTime();
                recorder.setOutputFilePath(file.getAbsoluteFile() + "/" + file_name_get.getText()+"_"+ctime+".mp4");
                if (!file.exists()) {
                    file.mkdirs();
                }
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



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pause_resume_heart(View view) {

        if (!is_paused){
            is_paused=true;
            Toast.makeText(Heart.this,"Recording Paused !",Toast.LENGTH_SHORT);
            recorder.pauseRecording();// to pause recording
            pause_resume.setText("resume");
            Bstop_heart.setEnabled(false);
        }

        else{
            is_paused=false;
            Toast.makeText(Heart.this,"Recording Resumed !",Toast.LENGTH_SHORT);
            recorder.continueRecording();//to resume recording
            pause_resume.setText("pause");
            Bstop_heart.setEnabled(true);
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
        recording_uri = recorder.get_recording_uri();
        System.out.println("path is   " + recording_uri);
        final StorageReference audio_ref = storageReference.child(recording_uri.getLastPathSegment());
        audio_ref.putFile(recording_uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                audio_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                      System.out.println("uri to "+uri.toString());
                      String addr = "";
                      try{
                          addr =  address.toString();
                      }
                      catch (Exception e){
                        addr = "not permitted bu user";
                      }
                      recorded_file for_database = new recorded_file(edit_box_name,uri.toString(),addr);
                        Toast.makeText(Heart.this, "Recording saved !", Toast.LENGTH_SHORT).show();
//                        databaseReference.push().setValue(for_database);
                        String k = databaseReference.push().getKey();
                        System.out.println("key is "+k);
                        databaseReference.child(k).setValue(for_database);
                        heartAll.child(k).setValue(for_database);
                        heartAll_tmp.child(k).setValue(for_database);
                        addItemToSheet(file_name_get.getText().toString(),addr,uri.toString(),mProgress);
                        mProgress.dismiss();
                        Intent intent = new Intent(Heart.this,Result.class);
                        intent.putExtra("displayname",displayname);

                        startActivity(intent);
                        finish();

                    }
                });
            }
        });

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
                finish();
            default:
                return super.onContextItemSelected(item);
        }
    }

    ///////////////// get current location ///////////////
    public void initialize_gps(){

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void getaddr(LatLng latLng) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            address = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);


        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("this is address "+address);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //msecs
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (!gotlocation) {
            mLastLocation = location;
            //Place current location marker
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            System.out.println("now --------------");
            getaddr(latLng);
            gotlocation= true;
        }


    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Heart.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.


                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    ///// api google sheet

    public  void addaudio(String uri,ProgressDialog mProgress){
//        SimpleMultiPartRequest
        SimpleMultiPartRequest simpleMultiPartRequest = new SimpleMultiPartRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzNOMXA_Us8VEuxdz6DGX1dOAEySK-nJFDU-CZ1B0hAlppGnDRq/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("listenong....");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.error.VolleyError error) {
                System.out.println("not listenong erroe -----");
            }
        });
        simpleMultiPartRequest.addFile("audio1",recording_uri.toString());

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(simpleMultiPartRequest);

//        MyApplication
    }

    //This is the part where data is transafeered from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addItemToSheet(String fname, String flocation,String download_link, ProgressDialog progressDialog) {

        final String filename = fname ;
        final String location = flocation;
        final String d_link = download_link;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbzFFORSLRBKXJuNogoOdcaFZ95N3sg0Nix8Ut-IGeZbO68aroo/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                error -> {

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action","addItem");
                parmas.put("filename",filename);
                parmas.put("location",location);
                parmas.put("download_link",d_link);
                System.out.println("sending d link"+d_link);

              return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }


}
