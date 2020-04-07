package com.example.hearthhealthhear;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anand.brose.graphviewlibrary.GraphView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  Button select,play,stop;
  TextView audiofilename;
  private Uri audiofile_uri;
  private MediaPlayer mediaPlayer;
  private Boolean play_stop_status=false;
  private Boolean play_pause_status=false;
//  record or select - play- stop - test

  //for wave form
//  public static final String SCALE = "scale";
//  public static final String OUTPUT_DIRECTORY = "VoiceRecorder";
//  public static final String OUTPUT_FILENAME = "recorder.mp3";
//  private static final int MY_PERMISSIONS_REQUEST_CODE = 0;
//  private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
//  int scale = 8;
//  private GraphView graphView;
//  private VoiceRecorder recorder;
//  private List samples;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    select = (Button)findViewById(R.id.record_button);
    play = (Button)findViewById(R.id.play_button);
    stop = (Button)findViewById(R.id.stop_button);
    audiofilename = (TextView) findViewById(R.id.filename_view);

    //for waveform
//    graphView = (GraphView) findViewById(R.id.graphView);
//    graphView.setGraphColor(Color.rgb(255,255,255));
//    graphView.setCanvasColor(Color.rgb(20,20,20));
//    graphView.setTimeColor(Color.rgb(255, 255, 255));
//    recorder = VoiceRecorder.getInstance();
//    if (recorder.isRecording()) {
//      ((Button) findViewById(R.id.control)).setText(getResources().getString(R.string.stop));
//      recorder.startPlotting(graphView);
//    }
//    if (savedInstanceState != null) {
//      scale = savedInstanceState.getInt(SCALE);
//      graphView.setWaveLengthPX(scale);
//      if (!recorder.isRecording()) {
//        samples = recorder.getSamples();
//        graphView.showFullGraph(samples);
//      }
//    }



  }

  public void select_audiofile(View view) {
    Intent selectaudio = new Intent(Intent.ACTION_GET_CONTENT);
    selectaudio.setType("audio/*");
    selectaudio.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
    startActivityForResult(Intent.createChooser(selectaudio,"Complete with"),0);
//    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//    startActivityForResult(intent, 0);
  }
//
  public void play_audio(View view) throws IOException {

    if (play_stop_status==false) { //playing
      mediaPlayer = new MediaPlayer();
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mediaPlayer.setDataSource(getApplicationContext(), audiofile_uri);
      mediaPlayer.prepare();
      mediaPlayer.start();
      play_stop_status=true;
      Toast.makeText(MainActivity.this,"playing recording",Toast.LENGTH_SHORT).show();
      play.setText("pause");
      play_pause_status=true;

    }
    else if (play_pause_status==true){ //pausing
      mediaPlayer.pause();
      play.setText("play");
      Toast.makeText(MainActivity.this,"recording paused",Toast.LENGTH_SHORT).show();
      play_pause_status=false;
    }

    else if (play_pause_status==false){
      mediaPlayer.start();
      Toast.makeText(MainActivity.this,"playing recording",Toast.LENGTH_SHORT).show();
      play.setText("pause");
      play_pause_status=true;

    }


  }



  public void stop_audio(View view) {
    mediaPlayer.stop();
    play.setText("play");
    play_stop_status=false;
    play_pause_status=false;
    Toast.makeText(MainActivity.this,"recording stopped",Toast.LENGTH_SHORT).show();

  }
//
//  public void test_pause(View view){
//    recorder.pauseRecording();
//  }
//  public void continue_rec(View view){
//    recorder.continueRecording();
//
//  }


  /**
   * Dispatch incoming result to the correct fragment.
   *
   * @param requestCode
   * @param resultCode
   * @param data
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode==0){
      audiofile_uri = data.getData();
      System.out.println("uri : "+audiofile_uri);
      audiofilename.setText("Audio file selected !");

    }



  }

//  //for waveform
//  @Override
//  protected void onSaveInstanceState(Bundle outState) {
//    outState.putInt(SCALE, scale);
//    super.onSaveInstanceState(outState);
//  }
//
//
//  public void controlClick(View v) {
//    Toast.makeText(MainActivity.this,"recording audio",Toast.LENGTH_SHORT).show();
//    if (recorder.isRecording()) {
//      System.out.println("-------------"+1);
//      ((Button) findViewById(R.id.control)).setText(this.getResources().getString(R.string.record));
//      graphView.stopPlotting();
//      samples = recorder.stopRecording();
//      graphView.showFullGraph(samples);
//    } else if(checkRecordPermission()&&checkStoragePermission()){
//
//      graphView.reset();
//      String filepath = Environment.getExternalStorageDirectory().getPath();
//      File file = new File(filepath, OUTPUT_DIRECTORY);
//      if (!file.exists()) {
//        file.mkdirs();
//      }
//
//
//      recorder.setOutputFilePath(file.getAbsoluteFile() + "/" + OUTPUT_FILENAME);
//      recorder.startRecording();
//      recorder.startPlotting(graphView);
//      ((Button) findViewById(R.id.control)).setText(this.getResources().getString(R.string.stop));
//    }else{
//      System.out.println("-------------"+3);
//      requestPermissions();
//    }
//  }
//
//
//
//  public void zoomOut(View v) {
//    scale = scale - 1;
//    if (scale < 2) {
//      scale = 2;
//    }
//    graphView.setWaveLengthPX(scale);
//    if (!recorder.isRecording()) {
//      graphView.showFullGraph(samples);
//    }
//  }
//
//
//
//
//  public void requestPermissions(){
//    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//      Manifest.permission.RECORD_AUDIO)) {
//
//      // Show an explanation to the user *asynchronously* -- don't block
//      // this thread waiting for the user's response! After the user
//      // sees the explanation, try again to request the permission.
//      ActivityCompat.requestPermissions(this,
//        new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
//        MY_PERMISSIONS_REQUEST_CODE);
//
//    } else {
//      // No explanation needed, we can request the permission.
//
//      ActivityCompat.requestPermissions(this,
//        new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
//        MY_PERMISSIONS_REQUEST_CODE);
//      // MY_PERMISSIONS_REQUEST_CODE is an
//      // app-defined int constant. The callback method gets the
//      // result of the request.
//    }
//  }
//
//  private boolean checkRecordPermission() {
//    return ContextCompat.checkSelfPermission(this,
//      Manifest.permission.RECORD_AUDIO)
//      == PackageManager.PERMISSION_GRANTED;
//  }
//
//  private boolean checkStoragePermission() {
//    return ContextCompat.checkSelfPermission(this,
//      Manifest.permission.WRITE_EXTERNAL_STORAGE)
//      == PackageManager.PERMISSION_GRANTED;
//  }

}
