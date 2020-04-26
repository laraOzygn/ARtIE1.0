package com.example.artie10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class Preview extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm. widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8), (int) (height*.6));

        Button b = (Button) findViewById( R.id.ar_button);
        b.setOnClickListener(v -> openAR());
    }
    public void openAR(){
        Intent intent = new Intent(this, ARScreen.class);
        startActivity(intent);
    }


}
