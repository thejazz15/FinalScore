package com.thejazz.finalscore.activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextWatcher;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thejazz.finalscore.R;
import com.thejazz.finalscore.utilities.UrlUtility;
import com.thejazz.finalscore.utilities.Utility;
import com.thejazz.finalscore.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText inputName, inputEmail, inputPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    private TextView loginTv;
    private Button btnSignUp;
    private ProgressBar pBar;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private JsonObjectRequest objectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        loginTv = (TextView) findViewById(R.id.login_tv);
        pBar = (ProgressBar) findViewById(R.id.signup_progress);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitForm() {
        if (!validateName()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        pBar.setVisibility(View.VISIBLE);
        signUpUser();
    }

    private void signUpUser(){
        Map<String, String> body = new HashMap();
        body.put("name", inputName.getText().toString());
        body.put("email", inputEmail.getText().toString());
        body.put("password", inputPassword.getText().toString());
        final JSONObject params = new JSONObject(body);
        objectRequest = new JsonObjectRequest(Request.Method.POST, UrlUtility.REGISTER_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    pBar.setVisibility(View.GONE);
                    String message = response.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pBar.setVisibility(View.GONE);
                String errorMsg = Utility.VolleyErrorMessage(error);
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(objectRequest);
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }
}
