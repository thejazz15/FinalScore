package com.thejazz.finalscore.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thejazz.finalscore.R;
import com.thejazz.finalscore.fragments.NavDrawerFragment;
import com.thejazz.finalscore.utilities.UrlUtility;
import com.thejazz.finalscore.utilities.Utility;
import com.thejazz.finalscore.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.id;
import static com.thejazz.finalscore.activities.LoginActivity.MyPREFERENCES;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView welcomeTv;
    private Button checkFieldsBtn, searchButton;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private JsonObjectRequest objectRequest;
    private SharedPreferences prefs;
    private NumberPicker fromTime, toTime, fromAmpm, toAmpm;
    int from_time ,to_time ,from_ampm, to_ampm;
    final String values[] = {" AM", " PM"};
    private String name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        name = prefs.getString("name", "N/A");
        email = prefs.getString("email", "N/A");
        from_time = 1; to_time = 1; from_ampm = 0; to_ampm = 0;
        setContentView(R.layout.activity_main);
        String token = prefs.getString("token", null);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getInstance().getRequestQueue();
        getUserInfo(token);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavDrawerFragment drawerFragment = (NavDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drawer_fragment);
        welcomeTv = (TextView) findViewById(R.id.welcome_tv);
        if(name == null){
            name = prefs.getString("name", "");
            email = prefs.getString("email", "");
        }
        welcomeTv.setText("Welcome "+name+"!\nEnter your timings below");
        drawerFragment.setUp(R.id.nav_drawer_fragment, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar, name, email);
        fromTime = (NumberPicker) findViewById(R.id.from_time);
        fromTime.setMinValue(1);
        fromTime.setMaxValue(12);
        fromTime.setWrapSelectorWheel(true);
        fromTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                from_time = newVal;
            }
        });
        fromAmpm = (NumberPicker) findViewById(R.id.from_ampm);
        fromAmpm.setMinValue(0);
        fromAmpm.setMaxValue(values.length-1);
        fromAmpm.setDisplayedValues(values);
        fromAmpm.setWrapSelectorWheel(true);
        fromAmpm.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                from_ampm = newVal;
            }
        });

        toTime = (NumberPicker) findViewById(R.id.to_time);
        toTime.setMinValue(1);
        toTime.setMaxValue(12);
        toTime.setWrapSelectorWheel(true);
        toTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                to_time = newVal;
            }
        });

        toAmpm = (NumberPicker) findViewById(R.id.to_ampm);
        toAmpm.setMinValue(0);
        toAmpm.setMaxValue(values.length-1);
        toAmpm.setDisplayedValues(values);
        toAmpm.setWrapSelectorWheel(true);
        toAmpm.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                to_ampm = newVal;
            }
        });

        checkFieldsBtn = (Button) findViewById(R.id.check_fields_btn);
        searchButton = (Button) findViewById(R.id.search_btn);
        checkFieldsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(from_ampm > to_ampm || (from_ampm == to_ampm && from_time >= to_time)){
                    Toast.makeText(getApplicationContext(),"Please enter correct timings", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), FieldsActivity.class);
                    intent.putExtra("from_time", Integer.toString(from_time)+values[from_ampm]);
                    intent.putExtra("to_time", Integer.toString(to_time)+values[to_ampm]);
                    startActivity(intent);
                }
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });
    }

    private void getUserInfo(String token){
        String url = UrlUtility.BASE_URL+"user/view?token="+token;
        objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.has("message")){
                        Log.v("tokencheck", "Message response");
                        prefs.edit().clear().commit();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }else{
                        Log.v("tokencheck", "Correct login token   "+response);
                        int id = response.getInt("id");
                        prefs.edit().putInt("user_id", id).commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError || error instanceof ServerError){
                    Log.v("tokencheck", Utility.VolleyErrorMessage(error));
                    prefs.edit().clear().commit();
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                welcomeTv.setText(Utility.VolleyErrorMessage(error));
            }
        });
        requestQueue.add(objectRequest);
    }

    @Override
    public void onBackPressed() {
        if( !NavDrawerFragment.isNavDrawerOpen())
            super.onBackPressed();
    }
}
