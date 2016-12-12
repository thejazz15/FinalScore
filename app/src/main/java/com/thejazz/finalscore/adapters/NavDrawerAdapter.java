package com.thejazz.finalscore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thejazz.finalscore.R;
import com.thejazz.finalscore.activities.BookingHistoryActivity;
import com.thejazz.finalscore.activities.BookingsActivity;
import com.thejazz.finalscore.activities.LoginActivity;
import com.thejazz.finalscore.activities.MainActivity;
import com.thejazz.finalscore.activities.ProfileActivity;
import com.thejazz.finalscore.fragments.NavDrawerFragment;

import static com.thejazz.finalscore.activities.LoginActivity.MyPREFERENCES;

/**
 * Created by hp1 on 28-12-2014.
 */
public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    private String mNavTitles[];
    private int mIcons[];
    private Context mContext;
    SharedPreferences prefs;

    public NavDrawerAdapter(String Titles[],int Icons[], Context context){
        mNavTitles = Titles;
        mIcons = Icons;
        mContext = context;
        prefs = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public NavDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent,false);
            return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NavDrawerAdapter.ViewHolder holder, final int position) {
            holder.textView.setText(mNavTitles[position]);
            holder.imageView.setImageResource(mIcons[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0)
                    NavDrawerFragment.closeNavDrawer();
                else if(position == 1)
                    mContext.startActivity(new Intent(mContext.getApplicationContext(), BookingsActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                else if(position == 2){
                    mContext.startActivity(new Intent(mContext.getApplicationContext(), ProfileActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }else if(position == 3){
                    mContext.startActivity(new Intent(mContext.getApplicationContext(), BookingHistoryActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                else if(position == 5){
                    prefs.edit().clear().commit();
                    ((Activity)mContext).finish();
                    mContext.startActivity(new Intent(mContext.getApplicationContext(),LoginActivity.class));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.rowText);
            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
        }

    }
}