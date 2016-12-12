package com.thejazz.finalscore.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

public class LoginActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    private Toolbar toolbar;
    private TextInputEditText inputEmail, inputPassword;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private TextView signupTv;
    private Button btnLogin;
    private ProgressBar pBar;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private JsonObjectRequest objectRequest;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sharedpreferences.contains("token")){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputEmail = (TextInputEditText) findViewById(R.id.input_email);
        inputPassword = (TextInputEditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        signupTv = (TextView) findViewById(R.id.signup_tv);
        pBar = (ProgressBar) findViewById(R.id.login_progress);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();


        signupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        pBar.setVisibility(View.VISIBLE);
        authenticateUser();
    }

    private void authenticateUser(){
        Map<String, String> body = new HashMap();
        body.put("email", inputEmail.getText().toString());
        body.put("password", inputPassword.getText().toString());
        final JSONObject params = new JSONObject(body);
        objectRequest = new JsonObjectRequest(Request.Method.POST, UrlUtility.LOGIN_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    pBar.setVisibility(View.GONE);
                    if(response.has("token")){
                        String token = response.getString("token");
                        sharedpreferences.edit().putString("token", token).commit();
                        JSONObject userObj = response.getJSONObject("user");
                        String name = userObj.getString("name");
                        String email = userObj.getString("email");
                        String created_at = userObj.getString("created_at");
                        int id = userObj.getInt("id");
                        sharedpreferences.edit().putInt("user_id", id).commit();
                        sharedpreferences.edit().putString("name", name).commit();
                        sharedpreferences.edit().putString("email", email).commit();
                        sharedpreferences.edit().putString("created_at", created_at).commit();
                        Toast.makeText(getApplicationContext(), "You're Ready to Go!", Toast.LENGTH_LONG).show();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        String message = response.getString("message");
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    }
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
}
