package com.example.hearthhealthhear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainOptions extends AppCompatActivity {

    TextView username;
    FirebaseAuth firebaseAuth;
    FirebaseUser current_user;
    String current_user_name;
    String current_user_email;
    private Toolbar mytoolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_options);
        mytoolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        username = (TextView)findViewById(R.id.user_name);
        firebaseAuth = FirebaseAuth.getInstance();
        current_user = firebaseAuth.getCurrentUser();
        current_user_name = current_user.getDisplayName();

        if (current_user_name==null){
            current_user_email= current_user.getEmail();
            username.setText(current_user_email);
        }
        else if (current_user_email==null) {
            username.setText(current_user_name);
        }
        else{
            username.setText("welcome user");
        }


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
}
