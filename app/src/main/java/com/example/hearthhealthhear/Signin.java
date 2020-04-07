package com.example.hearthhealthhear;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class Signin extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private Intent fromsigin;
    private String profilepic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        firebaseAuth = FirebaseAuth.getInstance(); // initiate the authentication objject
        //authentication providers list
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
        );
        //authentication listener
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // things to do when listener is trigered
                //  the variable firebaseAuth tells whether the user is signedin or not
                FirebaseUser current_user = firebaseAuth.getCurrentUser();

                if (current_user!= null){
                    //means user signed in
//                    onSignIn(current_user.getDisplayName());
                    profilepic = current_user.getPhotoUrl().toString();
                    System.out.println("scsc"+profilepic);
                    Toast.makeText(Signin.this," sign in successful !",Toast.LENGTH_LONG).show();
                    fromsigin = new Intent(Signin.this,MainActivity.class);
                    fromsigin.putExtra("profilepic_uri",profilepic);
                    Signin.this.startActivity(fromsigin);
                    System.out.println("yes"+profilepic);
                    Signin.this.finish();


                }

                else{
                    //user not signed in
                    // now display the firebase sign in ui7jjj
                    System.out.println("no");
                    Toast.makeText(Signin.this,"Not signed in !",Toast.LENGTH_LONG).show();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setIsSmartLockEnabled(false)
                                    .setTheme(R.style.siginin_theme)
                                    .build(),
                            1);
                }
            }
        };
//                                    .setLogo(R.drawable.splah_logo)

        //attach a listener to authencation
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
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

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(Signin.this, MainActivity.class);
                Toast.makeText(Signin.this, "success", Toast.LENGTH_SHORT).show();
                System.out.println("back to main");
                Signin.this.startActivity(intent);
                Signin.this.finish();
            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(Signin.this, "cancalled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


    }}




