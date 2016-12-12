package com.thejazz.finalscore.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thejazz.finalscore.R;
import com.thejazz.finalscore.adapters.NavDrawerAdapter;

public class NavDrawerFragment extends Fragment {

    private static String SHARED_PREF_FILE = "myPrefs";
    private static String USER_LEARNED_DRAWER_KEY = "user_learned_drawer";
    private View containerView;
    private ActionBarDrawerToggle mDrawerToggle;
    static DrawerLayout mDrawerLayout;
    private boolean userLearnedDrawer;
    private boolean fromSavedInstanceState;
    private TextView nameHeader, emailHeader;
    private String TITLES[] = {"Home","My Bookings","Profile", "Booking History", "Settings", "Logout"};
    int icons[] = {R.mipmap.ic_home_black_24dp, R.mipmap.ic_event_available_black_24dp,
            R.mipmap.ic_person_outline_black_24dp,  R.mipmap.ic_history_black_24dp,
            R.mipmap.ic_settings_applications_black_24dp, R.mipmap.ic_lock_outline_black_24dp};
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    public NavDrawerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), USER_LEARNED_DRAWER_KEY, "false"));
        if(savedInstanceState != null){
            fromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_drawer, container, false);
        nameHeader = (TextView) view.findViewById(R.id.name_header);
        emailHeader = (TextView) view.findViewById(R.id.email_header);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mAdapter = new NavDrawerAdapter(TITLES,icons, getActivity());
        recyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        return view;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar, String name, String email) {
        nameHeader.setText(name);
        emailHeader.setText(email);
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!userLearnedDrawer){
                    userLearnedDrawer = true;
                    saveToPreferences(getActivity(), USER_LEARNED_DRAWER_KEY, userLearnedDrawer+"");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        if(!userLearnedDrawer && !fromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    private static String readFromPreferences(Context context, String prefName, String defaultValue){
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        return prefs.getString(prefName, defaultValue);
    }

    private static void saveToPreferences(Context context, String prefName, String prefValue){
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefName, prefValue).commit();
    }

    public static boolean isNavDrawerOpen(){
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return true;
        }else{
            return false;
        }
    }

    public static void closeNavDrawer(){
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }
}
