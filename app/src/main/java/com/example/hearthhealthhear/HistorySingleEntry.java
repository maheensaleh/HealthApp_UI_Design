package com.example.hearthhealthhear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.Serializable;

public class HistorySingleEntry extends AppCompatActivity {


    String displayname;
    TextView username,recordfile_name;
    private Toolbar mytoolbar;
    private FirebaseAuth firebaseAuth;
    String fileName;
    String filePath;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single_entry);

        username = (TextView)findViewById(R.id.user_name);

        final Intent getterintent = getIntent();
        displayname = getterintent.getStringExtra("username");
        username.setText(displayname);

        firebaseAuth = FirebaseAuth.getInstance();

        recordfile_name = (TextView)findViewById(R.id.record_name);
        fileName= getterintent.getStringExtra("file_name");
        filePath = getterintent.getStringExtra("file_path");
        System.out.println("name is "+fileName+"and path is "+filePath);
        recordfile_name.setText(fileName);

        mytoolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);
//

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
                Intent tosignin  = new Intent(HistorySingleEntry.this, Signin.class);
                Toast.makeText(HistorySingleEntry.this, "logging out", Toast.LENGTH_SHORT).show();
                startActivity(tosignin);
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void play_audio(View view) throws IOException {


        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(filePath);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.prepare();





    }
}
