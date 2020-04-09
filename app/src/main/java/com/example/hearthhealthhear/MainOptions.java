package com.example.hearthhealthhear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    String displayname="Health AI";
    private Toolbar mytoolbar;
    Button heart_button;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_options);
        mytoolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);
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
}
