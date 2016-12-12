package com.thejazz.finalscore.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.thejazz.finalscore.adapters.BookingsAdapter;
import com.thejazz.finalscore.adapters.HistoryAdapter;
import com.thejazz.finalscore.utilities.UrlUtility;
import com.thejazz.finalscore.utilities.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import static com.thejazz.finalscore.activities.LoginActivity.MyPREFERENCES;

public class BookingHistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private HistoryAdapter myAdapter;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private ContentValues[] cvArray;
    private SharedPreferences prefs;
    private TextView noResultsTv;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        noResultsTv = (TextView) findViewById(R.id.no_results_tv);
        progressBar = (ProgressBar) findViewById(R.id.load_progress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Booking History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        myAdapter = new HistoryAdapter(getApplicationContext());
        recyclerView.setAdapter(myAdapter);
        getMyBookings();
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

    public void getMyBookings(){
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        int id = prefs.getInt("user_id", 0);
//        String token = prefs.getString("token", null);
        String url = UrlUtility.BASE_URL+"book/"+id+"/history";
        progressBar.setVisibility(View.VISIBLE);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    int i, id;
                    String field_no, field_name, deleted_at, created_at;
                    int from_time, to_time, price;
                    JSONArray bookings = response.getJSONArray("bookings");
                    Vector<ContentValues> vector = new Vector<ContentValues>(bookings.length());
                    for(i = 0; i < bookings.length(); i++){
                        JSONObject booking = bookings.getJSONObject(i);
                        ContentValues values = new ContentValues();
                        id = booking.getInt("id");
                        from_time = booking.getInt("start_time");
                        to_time = booking.getInt("end_time");
                        deleted_at = booking.getString("deleted_at");
                        created_at = booking.getString("created_at");
                        price = booking.getInt("total_price");
                        JSONObject field = booking.getJSONObject("view_field");
                        field_name = field.getString("field_name");
                        field_no = field.getString("field_no");
                        values.put("id", id);
                        values.put("field_name", field_name);
                        values.put("field_no", field_no);
                        values.put("cost", price);
                        values.put("from_time", from_time);
                        values.put("to_time", to_time);
                        values.put("deleted_at", deleted_at);
                        values.put("created_at", created_at);
                        vector.add(values);
                    }
                    cvArray = new ContentValues[vector.size()];
                    vector.toArray(cvArray);
                    progressBar.setVisibility(View.GONE);
                    if(cvArray.length == 0){
                        noResultsTv.setVisibility(View.VISIBLE);
                    }else{
                        myAdapter.setBookingList(cvArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
