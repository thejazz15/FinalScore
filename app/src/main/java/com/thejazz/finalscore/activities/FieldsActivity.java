package com.thejazz.finalscore.activities;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thejazz.finalscore.R;
import com.thejazz.finalscore.adapters.FieldsAdapter;
import com.thejazz.finalscore.utilities.UrlUtility;
import com.thejazz.finalscore.utilities.Utility;
import com.thejazz.finalscore.utilities.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Vector;

import static android.R.attr.id;
import static android.R.attr.key;

public class FieldsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    String fromTime, toTime;
    private TextView timingsTv;
    private RecyclerView recyclerView;
    private FieldsAdapter myAdapter;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private ContentValues[] cvArray;
    private String LOG_TAG = "FieldsActivity";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields);
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Available Fields");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        timingsTv = (TextView) findViewById(R.id.timings_tv);
        progressBar = (ProgressBar) findViewById(R.id.load_progress);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fromTime = extras.getString("from_time");
            toTime = extras.getString("to_time");
        }

        timingsTv.setText("Results for fields available from "+fromTime+" to "+toTime);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        myAdapter = new FieldsAdapter(getApplicationContext(), fromTime, toTime);
        recyclerView.setAdapter(myAdapter);
        getFields();
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

    private void getFields(){
        JSONObject params = new JSONObject();
        try {
            params.put("start_time", Utility.getTimeinInt(fromTime));
            params.put("end_time", Utility.getTimeinInt(toTime));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = UrlUtility.BASE_URL+"field/available";
        progressBar.setVisibility(View.VISIBLE);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int id;
                    String name, field_name;
                    int price;
                    JSONArray fields = response.getJSONArray("fields");
                    Vector<ContentValues> vector = new Vector<ContentValues>(fields.length());
                    for(int i = 0; i < fields.length(); i++){
                        ContentValues values = new ContentValues();
                        JSONObject field = fields.getJSONObject(i);
                        id = field.getInt("id");
                        name = field.getString("name");
                        field_name = field.getString("field");
                        price = field.getInt("price");
                        values.put("id", id);
                        values.put("name", name);
                        values.put("field", field_name);
                        values.put("price", price);
                        vector.add(values);
                    }
                    cvArray = new ContentValues[vector.size()];
                    vector.toArray(cvArray);
                    progressBar.setVisibility(View.GONE);
                    if(cvArray.length == 0){
                        timingsTv.setText("No fields available");
                    }else{
                        myAdapter.setFieldList(cvArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                timingsTv.setText(Utility.VolleyErrorMessage(error));
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
