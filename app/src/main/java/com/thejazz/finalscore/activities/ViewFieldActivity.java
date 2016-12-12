package com.thejazz.finalscore.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thejazz.finalscore.R;
import com.thejazz.finalscore.utilities.Utility;
import com.thejazz.finalscore.utilities.VolleySingleton;

import java.util.ArrayList;
import java.util.List;

import static com.thejazz.finalscore.R.id.start;
import static com.thejazz.finalscore.R.id.toolbar;

public class ViewFieldActivity extends AppCompatActivity {

    private TextView nameTv, fieldTv, costTv, timingsTv;
    private NumberPicker fromTime, toTime, fromAmpm, toAmpm;
    int from_time ,to_time ,from_ampm, to_ampm;
    final String values[] = {" AM", " PM"};
    private Toolbar toolbar;
    private Button bookBtn;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private JsonObjectRequest objectRequest;
    private ContentValues fieldValues;
    List<Integer> startList = new ArrayList<Integer>();
    List<Integer> endList = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        from_time = 1; to_time = 1; from_ampm = 0; to_ampm = 0;
        setContentView(R.layout.activity_view_field);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fieldValues = (ContentValues) extras.get("field");
            startList = extras.getIntegerArrayList("startList");
            endList = extras.getIntegerArrayList("endList");
        }
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getInstance().getRequestQueue();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Field Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        nameTv = (TextView) findViewById(R.id.name_tv);
        fieldTv = (TextView) findViewById(R.id.field_tv);
        costTv = (TextView) findViewById(R.id.cost_tv);
        timingsTv = (TextView) findViewById(R.id.booking_details_tv);
        fromTime = (NumberPicker) findViewById(R.id.from_time);
        fromTime.setMinValue(1);
        fromTime.setMaxValue(12);
        fromTime.setWrapSelectorWheel(true);
        fromTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                from_time = newVal;
            }
        });
        fromAmpm = (NumberPicker) findViewById(R.id.from_ampm);
        fromAmpm.setMinValue(0);
        fromAmpm.setMaxValue(values.length-1);
        fromAmpm.setDisplayedValues(values);
        fromAmpm.setWrapSelectorWheel(true);
        fromAmpm.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                from_ampm = newVal;
            }
        });

        toTime = (NumberPicker) findViewById(R.id.to_time);
        toTime.setMinValue(1);
        toTime.setMaxValue(12);
        toTime.setWrapSelectorWheel(true);
        toTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                to_time = newVal;
            }
        });

        toAmpm = (NumberPicker) findViewById(R.id.to_ampm);
        toAmpm.setMinValue(0);
        toAmpm.setMaxValue(values.length-1);
        toAmpm.setDisplayedValues(values);
        toAmpm.setWrapSelectorWheel(true);
        toAmpm.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                to_ampm = newVal;
            }
        });

        nameTv.setText(fieldValues.getAsString("name"));
        fieldTv.setText(fieldValues.getAsString("field"));
        costTv.setText(String.format(getResources().getString(R.string.cost_per_hour), fieldValues.getAsInteger("price")));
        String timingsStr = " ";
        for(int i=0; i < startList.size(); i++){
            timingsStr += " "+Utility.getTimeAsString(startList.get(i))
                    +" - "+Utility.getTimeAsString(endList.get(i))+",";
        }
        timingsStr = timingsStr.substring(0, timingsStr.length()-1) + (timingsStr.length() > 1 ? "." : "NONE");
        timingsTv.setText(timingsStr);
        bookBtn = (Button) findViewById(R.id.book_btn);
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(from_ampm > to_ampm || (from_ampm == to_ampm && from_time >= to_time)){
                    Toast.makeText(getApplicationContext(),"Please enter correct timings", Toast.LENGTH_SHORT).show();
                }
                else{
                    int i;
                    for(i=0; i<startList.size(); i++){
                        if(( (from_ampm == 0 ? from_time : from_time+12) >= startList.get(i)
                                && (from_ampm == 0 ? from_time : from_time+12) < endList.get(i)) ||
                                ( (to_ampm == 0 ? to_time : to_time+12) > startList.get(i)
                                        && (to_ampm == 0 ? to_time : to_time+12) <= endList.get(i))){
                            Toast.makeText(getApplicationContext(),"Please enter correct timings", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if(i == startList.size()){
                        Intent intent = new Intent(getApplicationContext(), ConfirmActivity.class);
                        intent.putExtra("field", fieldValues);
                        intent.putExtra("fromTime", Integer.toString(from_time)+values[from_ampm]);
                        intent.putExtra("toTime", Integer.toString(to_time)+values[to_ampm]);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
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
}
