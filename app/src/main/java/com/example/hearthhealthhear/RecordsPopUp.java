package com.example.hearthhealthhear;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class RecordsPopUp extends Activity {

    Intent popdown;
    String username;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_pop_up);

        // to set the size of popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.75));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;
        getWindow().setAttributes(params);

        Intent getname =  getIntent();
        username = getname.getStringExtra("username");


//        // when close is clicked
//        popdown= new Intent(RecordsPopUp.this,lungs_adapter.class);
//        final Button button =findViewById(R.id.close);
//        button.setOnClickListener(new View.OnClickListener()
//
//        {
//            @Override
//            public void onClick (View v){
//                startActivity(popdown);
//                finish();
//            }
//
//        }

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
    }
