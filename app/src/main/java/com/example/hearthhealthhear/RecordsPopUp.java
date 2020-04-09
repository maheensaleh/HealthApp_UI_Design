package com.example.hearthhealthhear;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RecordsPopUp extends Activity {

    Intent popdown;
    String username;
    FirebaseAuth firebaseAuth;
    TextView username_view;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_pop_up);
        username_view = (TextView)findViewById(R.id.user_name);
        username_view.setText("Heart AI");


}

    public void gotto_lungs_his(View view) {

        popdown= new Intent(RecordsPopUp.this,LungHistory.class);
        popdown.putExtra("username",username);
        startActivity(popdown);
        finish();

    }


    public void goto_heart_his(View view) {

        popdown= new Intent(RecordsPopUp.this,HeartHistroy.class);
        popdown.putExtra("username",username);
        startActivity(popdown);
        finish();
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
                Intent tosignin  = new Intent(RecordsPopUp.this, Signin.class);
                Toast.makeText(RecordsPopUp.this, "logging out", Toast.LENGTH_SHORT).show();
                startActivity(tosignin);
                finish();
            default:
                return super.onContextItemSelected(item);
        }
    }

}
