package com.example.hearthhealthhear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Heart extends AppCompatActivity {

    private Toolbar mytoolbar;
    TextView username_view;
    String displayname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        mytoolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        Intent getdisplayname = getIntent();
        displayname = getdisplayname.getStringExtra("username");

        username_view=(TextView)findViewById(R.id.user_name);
        username_view.setText(displayname);






    }

    public void record_heart(View view) {
    }
}
