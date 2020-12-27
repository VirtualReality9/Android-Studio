package com.example.tally;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_iv_back:
                finish();
                break;
        }
    }
}