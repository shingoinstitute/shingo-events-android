package org.shingo.shingoeventsapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.ui.events.EventListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button events = (Button) findViewById(R.id.action_events);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startThisActivity(EventListActivity.class);
            }
        });

        (findViewById(R.id.action_model)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startThisActivity(ModelActivity.class);
            }
        });

    }

    private void startThisActivity(Class dest){
        Intent i = new Intent(this, dest);
        startActivity(i);
    }

}
