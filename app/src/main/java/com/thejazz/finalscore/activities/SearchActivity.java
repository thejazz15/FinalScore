package com.thejazz.finalscore.activities;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thejazz.finalscore.R;
import com.thejazz.finalscore.adapters.FieldsAdapter;
import com.thejazz.finalscore.adapters.SearchAdapter;
import com.thejazz.finalscore.utilities.UrlUtility;
import com.thejazz.finalscore.utilities.Utility;
import com.thejazz.finalscore.utilities.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SearchAdapter myAdapter;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private ContentValues[] cvArray;
    private TextView resultsTv;
    private ProgressBar progressBar;
    private ImageButton searchBtn;
    private EditText searchEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Fields");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        resultsTv = (TextView) findViewById(R.id.results_tv);
        progressBar = (ProgressBar) findViewById(R.id.search_progress);
        searchBtn = (ImageButton) findViewById(R.id.search_btn);
        searchEt = (EditText) findViewById(R.id.search_et);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        myAdapter = new SearchAdapter(getApplicationContext());
        recyclerView.setAdapter(myAdapter);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchEt.getText().toString().length() != 0){
                    progressBar.setVisibility(View.VISIBLE);
                    resultsTv.setText("Searching...");
                    searchFields(searchEt.getText().toString());
                }
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

    private void searchFields(final String name){
        final String searchName = name;
        JSONObject params = new JSONObject();
        try {
            params.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = UrlUtility.BASE_URL+"field/search";
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
                    if(cvArray.length == 0){
                        resultsTv.setText("No results found.");
                        progressBar.setVisibility(View.GONE);
                    }else{
                        vector.toArray(cvArray);
                        resultsTv.setText("Search results for \""+searchName+"\" :");
                        progressBar.setVisibility(View.GONE);
                        myAdapter.setFieldList(cvArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultsTv.setText(Utility.VolleyErrorMessage(error));
                progressBar.setVisibility(View.GONE);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
