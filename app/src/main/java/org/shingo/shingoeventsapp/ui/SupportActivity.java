package org.shingo.shingoeventsapp.ui;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.api.OnTaskComplete;
import org.shingo.shingoeventsapp.api.RestApi;
import org.shingo.shingoeventsapp.data.reportabug.SendReportTask;

import fr.ganfra.materialspinner.MaterialSpinner;

public class SupportActivity extends AppCompatActivity implements OnTaskComplete {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        final String[] ITEMS = {"Bug Report", "Feedback", "Contact us"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        final Button submit = (Button)findViewById(R.id.action_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText)findViewById(R.id.email);
                EditText desc = (EditText) findViewById(R.id.comment);
                int position = spinner.getSelectedItemPosition();
                System.out.println("spinner.getSelectedItemPosition() " + (position - 1));
                submitBugReport(email.getText().toString(), desc.getText().toString(), ITEMS[position - 1]);
            }
        });
        ((findViewById(R.id.action_cancel))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != -1) {
                    System.out.println("Spinner position: " + position);
                    View emailContainer = findViewById(R.id.email_container);
                    TextInputLayout descContainer = (TextInputLayout)findViewById(R.id.description_container);
                    emailContainer.setVisibility(View.VISIBLE);
                    descContainer.setVisibility(View.VISIBLE);
                    EditText desc = (EditText)findViewById(R.id.comment);
                    if(position != 0) {
                        String hint = (position == 1) ? "Feedback" : "Comments";
                        desc.setHint(hint);
                        descContainer.setHint("");
                    } else {
                        desc.setHint(getResources().getString(R.string.prompt_description));
                    }
                    submit.setEnabled(true);
                } else {
                    View emailContainer = findViewById(R.id.email_container);
                    View descContainer = findViewById(R.id.description_container);
                    emailContainer.setVisibility(View.INVISIBLE);
                    descContainer.setVisibility(View.INVISIBLE);
                    submit.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                View emailContainer = findViewById(R.id.email_container);
                View descContainer = findViewById(R.id.description_container);
                emailContainer.setVisibility(View.INVISIBLE);
                descContainer.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void submitBugReport(String email, String description, String type){
        String report = email + "\n" + description;
        String system_info = "OS: " + System.getProperty("os.version") + "\n" + // OS version
        "API: " + android.os.Build.VERSION.SDK + "\n" +                         // API Level
        "Device: " + android.os.Build.DEVICE + "\n" +                           // Device
        "Model: " + android.os.Build.MODEL + "\n" +                             // Model
        "Product: " + android.os.Build.PRODUCT;                                 // Product

        RestApi api = new RestApi(this, this);
        SendReportTask sendReportTask = api.sendReport();
        sendReportTask.execute(system_info, report, type);
    }

    @Override
    public void onTaskComplete() {
        try {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.support_container), "Report submitted", Snackbar.LENGTH_LONG)
                    .setAction("Done", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    });
            snackbar.show();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
