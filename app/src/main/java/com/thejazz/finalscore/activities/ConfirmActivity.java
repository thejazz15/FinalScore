package com.thejazz.finalscore.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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

import static com.thejazz.finalscore.activities.LoginActivity.MyPREFERENCES;

public class ConfirmActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button confirmBtn;
    private TextView timingsTv, fieldNameTv, fieldNoTv, priceTv;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private ContentValues fieldValues;
    private String fromTime, toTime;
    private VolleySingleton volleySingleton;
    private SharedPreferences prefs;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int price = 0;
        super.onCreate(savedInstanceState);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        setContentView(R.layout.activity_confirm);requestQueue = VolleySingleton.getInstance().getRequestQueue();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Confirm Booking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        timingsTv = (TextView) findViewById(R.id.timings_tv);
        fieldNameTv = (TextView) findViewById(R.id.field_name_tv);
        fieldNoTv = (TextView) findViewById(R.id.field_no_tv);
        priceTv = (TextView) findViewById(R.id.cost_tv);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        progressBar = (ProgressBar) findViewById(R.id.confirm_progress);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fieldValues = (ContentValues) extras.get("field");
            fromTime = extras.getString("fromTime");
            toTime = extras.getString("toTime");
            price = (Utility.getTimeinInt(toTime) - Utility.getTimeinInt(fromTime)) * fieldValues.getAsInteger("price");
        }
        fieldNameTv.setText(String.format(getResources().getString(R.string.field_name_confirm), fieldValues.get("name")));
        fieldNoTv.setText(String.format(getResources().getString(R.string.field_no_confirm), fieldValues.get("field")));
        priceTv.setText(String.format(getResources().getString(R.string.cost_confirm), price));
        timingsTv.setText(String.format(getResources().getString(R.string.timing_confirm), fromTime, toTime));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                confirmBtn.setVisibility(View.GONE);
                makeBooking();
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

    private void makeBooking(){
        JSONObject params = new JSONObject();
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        String url = UrlUtility.BASE_URL + "book"+"?token="+token;
        try {
            params.put("field_id", fieldValues.getAsInteger("id"));
            params.put("start_time", Utility.getTimeinInt(fromTime));
            params.put("end_time", Utility.getTimeinInt(toTime));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Field booking confirmed!", Toast.LENGTH_LONG).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMsg = Utility.VolleyErrorMessage(error);
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                confirmBtn.setVisibility(View.VISIBLE);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
