package com.thejazz.finalscore.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.thejazz.finalscore.activities.LoginActivity.MyPREFERENCES;

/**
 * Created by TheJazz on 02/12/16.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context mContext;
    private ContentValues[] cvArray;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private SharedPreferences prefs;

    public HistoryAdapter(Context mContext){
        this.mContext = mContext;
        prefs = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    public void setBookingList(ContentValues[] cvArray) {
        this.cvArray = new ContentValues[cvArray.length];
        this.cvArray = cvArray;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_card_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ContentValues values = cvArray[position];
        holder.fieldDetailsTv.setText(String.format(mContext.getResources().getString(R.string.booking_field_info),
                values.getAsString("field_name"), values.getAsString("field_no") ));
        holder.timingsTv.setText(String.format(mContext.getResources().getString(R.string.booking_timings),
                values.getAsInteger("from_time"), values.getAsInteger("to_time")));
        holder.costTv.setText(String.format(mContext.getResources().getString(R.string.booking_cost),
                values.getAsInteger("cost")));
        String created_at = values.getAsString("created_at");
        String date = created_at.substring(0,10);
        String time = created_at.substring(11, 16);
        holder.dateTv.setText(String.format(mContext.getResources().getString(R.string.dob), date, time));
        String deleted_at = values.getAsString("deleted_at");
        holder.statusTv.setClickable(false);
        holder.statusTv.setTextSize(10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
//        String dateAsString = sdf.format(created_at.substring(0,8));
        Date bookedDate = null;
        try {
            bookedDate = sdf.parse(created_at);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.v("History", now.toString()+" "+bookedDate.toString());
        if(now.getTime() > (bookedDate.getTime()+(1000*60*60*values.getAsInteger("to_time")))){
            if(deleted_at.length() <= 5){
                holder.statusTv.setText("PAID");
                holder.statusTv.setTextColor(Color.parseColor("#4CAF50"));
            }
            else{
                holder.statusTv.setText("CANCELLED");
                holder.statusTv.setTextColor(Color.parseColor("#8b0000"));
            }
        } else {
            holder.statusTv.setText("PENDING");
            holder.statusTv.setTextColor(Color.parseColor("#616161"));
        }

    }

    @Override
    public int getItemCount() {
        return (cvArray == null) ? 0 : cvArray.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView fieldDetailsTv, timingsTv, costTv, statusTv, dateTv;
        ImageView thumbnail;

        MyViewHolder(View itemView) {
            super(itemView);
            fieldDetailsTv = (TextView) itemView.findViewById(R.id.field_name_no_tv);
            timingsTv = (TextView) itemView.findViewById(R.id.timings_tv);
            costTv = (TextView) itemView.findViewById(R.id.cost_tv);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            statusTv = (TextView) itemView.findViewById(R.id.status_tv);
            dateTv= (TextView) itemView.findViewById(R.id.date_tv);
        }
    }
}

