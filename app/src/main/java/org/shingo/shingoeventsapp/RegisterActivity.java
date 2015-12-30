package org.shingo.shingoeventsapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private UserCreateTask mCreateTask = null;

    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mDisplayNameView;
    private EditText mTitleView;
    private EditText mCompanyView;
    private android.support.v7.widget.SwitchCompat mIsVisibleView;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mConfirmPasswordView = (EditText) findViewById(R.id.confirm_password);

        mFirstNameView = (EditText) findViewById(R.id.first_name);

        mLastNameView = (EditText) findViewById(R.id.last_name);

        mDisplayNameView = (EditText) findViewById(R.id.display_name);

        mTitleView = (EditText) findViewById(R.id.attendee_title);

        mCompanyView = (EditText) findViewById(R.id.company);

        mIsVisibleView = (android.support.v7.widget.SwitchCompat) findViewById(R.id.is_visible);

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.login_progress);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Creating account!", Snackbar.LENGTH_LONG)
                        .setAction("Submitting", null).show();

                createAttendee();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        mEmailView.setText(bundle.getString("email"));
        mPasswordView.setText(bundle.getString("password"));
    }

    private void createAttendee(){
        if (mCreateTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String displayName = mDisplayNameView.getText().toString();
        String title = mTitleView.getText().toString();
        String company = mCompanyView.getText().toString();
        boolean visible = mIsVisibleView.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if(!password.equals(confirmPassword)){
            mConfirmPasswordView.setError(getString(R.string.error_mismatch_password));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(TextUtils.isEmpty(firstName)){
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if(TextUtils.isEmpty(lastName)){
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mCreateTask = new UserCreateTask(email, password, firstName, lastName, displayName, title, company, visible);
            mCreateTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserCreateTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mFirstName;
        private final String mLastName;
        private final String mDisplayName;
        private final String mTitle;
        private final String mCompany;
        private final boolean mVisible;
        private String output;

        UserCreateTask(String email, String password, String firstName, String lastName, String displayName, String title, String company, boolean visible) {
            mEmail = email;
            mPassword = password;
            mFirstName = firstName;
            mLastName = lastName;
            mDisplayName = displayName;
            mTitle = title;
            mCompany = company;
            mVisible = visible;
            System.out.println("UserCreateTask created");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            System.out.println("UserCreateTask.doInBackground called");
            boolean success  = false;
            try {
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(mEmail,"UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(mPassword, "UTF-8");
                data += "&" + URLEncoder.encode("first_name", "UTF-8") + "=" + URLEncoder.encode(mFirstName, "UTF-8");
                data += "&" + URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(mLastName, "UTF-8");
                data += "&" + URLEncoder.encode("display_name", "UTF-8") + "=" + URLEncoder.encode(mDisplayName, "UTF-8");
                data += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(mTitle, "UTF-8");
                data += "&" + URLEncoder.encode("company", "UTF-8") + "=" + URLEncoder.encode(mCompany, "UTF-8");
                data += "&" + URLEncoder.encode("visibility", "UTF-8") + "=" + URLEncoder.encode(Boolean.toString(mVisible), "UTF-8");
                URL url = new URL("https://shingo-events.herokuapp.com/api/attendees/create?client_id=" + LoginActivity.CLIENT_ID + "&client_secret=" + LoginActivity.CLIENT_SECRET);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null){
                    sb.append(line);
                }
                output = sb.toString();
                JSONObject response = new JSONObject(output);
                System.out.println("Create response: " + output);
                success = response.getBoolean("success");
                if(success ){
                    System.out.println("Attendee created successfully");
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", mEmail);
                    editor.putString("password", mPassword);
                }
            } catch(UnsupportedEncodingException e){
                return false;
            } catch(IOException e ){
                return false;
            } catch(JSONException e){
                return false;
            }

            return success ;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCreateTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                if(output.contains("duplicate key error")){
                    mEmailView.setError(getString(R.string.error_account_exists));
                    mEmailView.requestFocus();
                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mCreateTask = null;
            showProgress(false);
        }
    }

}
