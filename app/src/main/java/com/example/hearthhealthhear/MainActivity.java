package com.example.hearthhealthhear;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

  Button select,play,stop;
  TextView audiofilename;
  private Uri audiofile_uri;
  private MediaPlayer mediaPlayer;
  private Boolean play_stop_status=false;
  private Boolean play_pause_status=false;
  //record or select - play- stop - test

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    select = (Button)findViewById(R.id.record_button);
    play = (Button)findViewById(R.id.play_button);
    stop = (Button)findViewById(R.id.stop_button);
    audiofilename = (TextView) findViewById(R.id.filename_view);
  }

  public void select_audiofile(View view) {
    Intent selectaudio = new Intent(Intent.ACTION_GET_CONTENT);
    selectaudio.setType("audio/*");
    selectaudio.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
    startActivityForResult(Intent.createChooser(selectaudio,"Complete with"),0);
//    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//    startActivityForResult(intent, 0);
  }

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
}
