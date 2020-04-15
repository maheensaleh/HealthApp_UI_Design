package com.example.hearthhealthhear;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.location.Address;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

public class HistorySingleEntry extends AppCompatActivity {


    String displayname,key;
    TextView username,recordfile_name;
    private Toolbar mytoolbar;
    private FirebaseAuth firebaseAuth;
    private String type;
    String fileName;
    String filePath;
    String address;
    private MediaPlayer mediaPlayer;
    Button play_stop,pause_resume,rename,share,delete;
    Boolean is_paused =false;
    Boolean isstop  = true;
    Intent gomain ;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference databaseReference,All,tmp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single_entry);

        username = (TextView)findViewById(R.id.user_name);

        final Intent getterintent = getIntent();
        type = getterintent.getStringExtra("type");
        displayname = getterintent.getStringExtra("username");
        username.setText("Health AI");
        gomain = new Intent(HistorySingleEntry.this,HeartHistroy.class);
        firebaseAuth = FirebaseAuth.getInstance();

        recordfile_name = (TextView)findViewById(R.id.record_name);
        key = getterintent.getStringExtra("key");
        System.out.println("keys is "+key);
        fileName= getterintent.getStringExtra("file_name");
        filePath = getterintent.getStringExtra("file_path");
        address = getterintent.getStringExtra("address");
        System.out.println("name is "+fileName+"and path is "+filePath);
        recordfile_name.setText(fileName);

        mytoolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        play_stop = (Button)findViewById(R.id.play_stop_history);
        pause_resume = (Button)findViewById(R.id.pause_resume_history);
        rename= (Button)findViewById(R.id.rename_history);
        share= (Button)findViewById(R.id.share_histroy);
        delete=(Button)findViewById(R.id.delete_history);
        pause_resume.setEnabled(false);
        firebaseDatabase = FirebaseDatabase.getInstance();



        if (type.equals("heart")){
            databaseReference = firebaseDatabase.getReference("heart").child(firebaseAuth.getUid());
            All = firebaseDatabase.getReference("heartAll");
            tmp = firebaseDatabase.getReference("heartAll_tmp");}

        else{
            databaseReference = firebaseDatabase.getReference("lungs").child(firebaseAuth.getUid());
            All = firebaseDatabase.getReference("lungsAll");
            tmp = firebaseDatabase.getReference("lungsAll_tmp");
        }}


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

        if (isstop){
            mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(filePath);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    Toast.makeText(HistorySingleEntry.this,"Playing !",Toast.LENGTH_SHORT).show();
                }
            });
            mediaPlayer.prepare();

            isstop=false;
            play_stop.setText("stop");
            pause_resume.setEnabled(true);
        }

        else if (!isstop){
            isstop= true;
            play_stop.setText("play");
            mediaPlayer.stop();
            pause_resume.setEnabled(false);
            Toast.makeText(HistorySingleEntry.this,"Stoped !",Toast.LENGTH_SHORT).show();

        }
    }

    public void pause_resume(View view) {

        if (!is_paused){
            mediaPlayer.pause();
            is_paused=true;
            pause_resume.setText("resume");
            Toast.makeText(HistorySingleEntry.this,"Paused !",Toast.LENGTH_SHORT).show();

        }

        else if(is_paused){
            mediaPlayer.start();
            is_paused=false;
            pause_resume.setText("pause");
            Toast.makeText(HistorySingleEntry.this,"Resumed !",Toast.LENGTH_SHORT).show();

        }
    }

    public void rename_audio_file(View view) {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_namelayout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        dialogBuilder.getWindow().setLayout(800, 600);


        final EditText editText = (EditText) dialogView.findViewById(R.id.new_name_edittext);
        Button button1 = (Button) dialogView.findViewById(R.id.get_newname_button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newname = editText.getText().toString();

                recorded_file edited = new recorded_file(newname,filePath,address);

                databaseReference.child(key).setValue(edited);
                All.child(key).setValue(edited);
                tmp.child(key).setValue(edited);
                dialogBuilder.dismiss();
                recordfile_name.setText(newname);
                gomain  = new Intent(HistorySingleEntry.this,MainOptions.class);
            }
        });




    }

    public void delete_audio_file(View view) {


        databaseReference.child(key).setValue(null);
        All.child(key).setValue(null);
        tmp.child(key).setValue(null);
        Intent gomain = new Intent(HistorySingleEntry.this,MainOptions.class);
        startActivity(gomain);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gomain.putExtra("username",displayname);
        startActivity(gomain);
    }
}
