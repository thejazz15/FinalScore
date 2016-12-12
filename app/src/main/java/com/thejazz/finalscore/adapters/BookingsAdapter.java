package com.thejazz.finalscore.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import com.thejazz.finalscore.activities.BookingsActivity;
import com.thejazz.finalscore.activities.MainActivity;
import com.thejazz.finalscore.utilities.UrlUtility;
import com.thejazz.finalscore.utilities.Utility;
import com.thejazz.finalscore.utilities.VolleySingleton;

import org.json.JSONObject;

import static com.thejazz.finalscore.activities.LoginActivity.MyPREFERENCES;
import static org.apache.commons.cli.OptionBuilder.create;

/**
 * Created by TheJazz on 15/11/16.
 */

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.MyViewHolder> {

    private Context mContext;
    private ContentValues[] cvArray;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private SharedPreferences prefs;
    private BookingsActivity ba;

    public BookingsAdapter(Context mContext, BookingsActivity ba){
        this.mContext = mContext;
        prefs = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.ba = ba;
    }

    public void setBookingList(ContentValues[] cvArray) {
        this.cvArray = new ContentValues[cvArray.length];
        this.cvArray = cvArray;
        notifyDataSetChanged();
    }

    private void cancelBooking(int id, final MyViewHolder holder){

        String token = prefs.getString("token", null);
        String url = UrlUtility.BASE_URL+"book/"+id+"?token="+token;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(mContext.getApplicationContext(), "Booking Cancelled!", Toast.LENGTH_SHORT).show();
                holder.cancelBtn.setClickable(false);
                holder.cancelBtn.setText("CANCELLED");
                holder.cancelBtn.setTextSize(10);
                holder.cancelBtn.setTextColor(Color.parseColor("#8b0000"));
                holder.cancelBtn.setBackground(mContext.getResources().getDrawable(R.drawable.confirm_layout_style));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext.getApplicationContext(), Utility.VolleyErrorMessage(error), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_card_view, parent, false);
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
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ba, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Cancel Booking");
                builder.setMessage("Are you sure you want to cancel this booking?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        cancelBooking(values.getAsInteger("id"), holder);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (cvArray == null) ? 0 : cvArray.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView fieldDetailsTv, timingsTv, costTv;
        ImageView thumbnail;
        Button cancelBtn;

        MyViewHolder(View itemView) {
            super(itemView);
            fieldDetailsTv = (TextView) itemView.findViewById(R.id.field_name_no_tv);
            timingsTv = (TextView) itemView.findViewById(R.id.timings_tv);
            costTv = (TextView) itemView.findViewById(R.id.cost_tv);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            cancelBtn = (Button) itemView.findViewById(R.id.cancel_btn);
        }
    }
}
