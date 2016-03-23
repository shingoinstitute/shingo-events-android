package org.shingo.shingoeventsapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.shingo.shingoeventsapp.R;

public class ModelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);
        TouchImageView img = (TouchImageView)findViewById(R.id.model);
        img.setImageResource(R.drawable.model);
    }
}
