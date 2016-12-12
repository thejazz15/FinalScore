package com.thejazz.finalscore.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.thejazz.finalscore.R;
import com.thejazz.finalscore.adapters.NavDrawerAdapter;
import com.thejazz.finalscore.adapters.ProfileAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.thejazz.finalscore.R.id.toolbar;
import static com.thejazz.finalscore.activities.LoginActivity.MyPREFERENCES;

public class ProfileActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    CircleImageView cIv;
    private String TITLES[] = {"Name", "E-mail", "Date of A/C Creation"};
    private String VALUES[];
    private SharedPreferences prefs;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        VALUES = new String[]{prefs.getString("name", "-"), prefs.getString("email", "-"), prefs.getString("created_at", "-").substring(0,10)};
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cIv = (CircleImageView) findViewById(R.id.circleView);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mAdapter = new ProfileAdapter(getApplicationContext(), TITLES, VALUES);
        recyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
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
