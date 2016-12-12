package com.thejazz.finalscore.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thejazz.finalscore.R;

/**
 * Created by TheJazz on 03/12/16.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder>{

    private String TITLES[];
    private Context mContext;
    SharedPreferences prefs;
    private String[] VALUES;

    public ProfileAdapter(Context context, String[] titles, String[] values){
        mContext = context;
        TITLES = titles;
        VALUES = values;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item_row,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.fieldTitle.setText(TITLES[position]);
        holder.fieldValue.setText(VALUES[position]);
    }

    @Override
    public int getItemCount() {
        return TITLES.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView fieldTitle, fieldValue;
        public MyViewHolder(View itemView) {
            super(itemView);
            fieldTitle = (TextView) itemView.findViewById(R.id.title_tv);
            fieldValue = (TextView) itemView.findViewById(R.id.value_tv);
        }
    }
}
