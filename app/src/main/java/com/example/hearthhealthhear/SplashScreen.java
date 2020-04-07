package com.example.hearthhealthhear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SplashScreen extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private Intent mainintent;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    //

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);


        //authentication instance
        firebaseAuth = FirebaseAuth.getInstance(); // initiate the authentication objject

        //authentication listener
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // things to do when listener is trigered
                //  the variable firebaseAuth tells whether the user is signedin or not
                FirebaseUser current_user = firebaseAuth.getCurrentUser();

                if (current_user!= null){

                    mainintent = new Intent(SplashScreen.this,MainOptions.class);
                    System.out.println("yes");


                }

                else{
                    //user not signed in
                    // now display the firebase sign in ui7jjj
                    mainintent = new Intent(SplashScreen.this,Signin.class);
                    System.out.println("no");

                }
            }
        };

        //attach a listener to authencation
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);



        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */

//                Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                SplashScreen.this.startActivity(mainintent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}