package com.thejazz.finalscore.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thejazz.finalscore.R;
import com.thejazz.finalscore.activities.ViewFieldActivity;
import com.thejazz.finalscore.utilities.UrlUtility;
import com.thejazz.finalscore.utilities.Utility;
import com.thejazz.finalscore.utilities.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by TheJazz on 01/12/16.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>{

    Context mContext;
    private ContentValues[] cvArray, timingsArray;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private VolleySingleton volleySingleton;

    public SearchAdapter(Context context){
        mContext = context;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getInstance().getRequestQueue();
    }

    public void setFieldList(ContentValues[] cvArray) {
        this.cvArray = new ContentValues[cvArray.length];
        this.cvArray = cvArray;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.field_card_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ContentValues values = cvArray[position];
        holder.name.setText(values.getAsString("name"));
        holder.fieldNo.setText(values.getAsString("field"));
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("SearchAdapter", values.toString());
                getFieldTimings(values);
            }
        });
    }

    private void getFieldTimings(final ContentValues values){
        int id = values.getAsInteger("id");
        String url = UrlUtility.BASE_URL+"book/"+id+"/booked";
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("SearchAdapter","Received response");
                    int start_time, end_time;
                    JSONArray timings = response.getJSONArray("timings");
                    Log.v("SearchAdapter", "Just after response : "+timings.toString());
                    Vector<ContentValues> vector = new Vector<ContentValues>(timings.length());
                    ArrayList<Integer> startList = new ArrayList<Integer>();
                    ArrayList<Integer> endList = new ArrayList<Integer>();
                    for(int i = 0; i < timings.length(); i++){
                        ContentValues values = new ContentValues();
                        JSONObject timing = timings.getJSONObject(i);
                        start_time = timing.getInt("start_time");
                        end_time = timing.getInt("end_time");
                        startList.add(start_time);
                        endList.add(end_time);
                    }
                    openViewFieldActivity(startList, endList, values);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("SearchAdapter", Utility.VolleyErrorMessage(error));
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void openViewFieldActivity(ArrayList<Integer> startList, ArrayList<Integer> endList, ContentValues values){
        mContext.startActivity(new Intent(mContext.getApplicationContext(), ViewFieldActivity.class)
                .putExtra("field", values)
                .putExtra("startList", startList)
                .putExtra("endList", endList)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public int getItemCount() {
        return (cvArray == null) ? 0 : cvArray.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, fieldNo;
        public ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            fieldNo = (TextView) itemView.findViewById(R.id.field_no);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }
}
