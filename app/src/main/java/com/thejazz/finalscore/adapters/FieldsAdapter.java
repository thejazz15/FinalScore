package com.thejazz.finalscore.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thejazz.finalscore.R;
import com.thejazz.finalscore.activities.ConfirmActivity;

public class FieldsAdapter extends RecyclerView.Adapter<FieldsAdapter.MyViewHolder> {

    private Context mContext;
    private ContentValues[] cvArray;
    private String fromTime, toTime;

    public FieldsAdapter(Context mContext, String fromTime, String toTime) {
        this.mContext = mContext;
        this.fromTime = fromTime;
        this.toTime = toTime;
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ContentValues values = cvArray[position];
        holder.name.setText(values.getAsString("name"));
        holder.fieldNo.setText(values.getAsString("field"));
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext.getApplicationContext(), ConfirmActivity.class)
                        .putExtra("field", values)
                        .putExtra("fromTime", fromTime)
                        .putExtra("toTime", toTime)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (cvArray == null) ? 0 : cvArray.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, fieldNo;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            fieldNo = (TextView) view.findViewById(R.id.field_no);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }
}