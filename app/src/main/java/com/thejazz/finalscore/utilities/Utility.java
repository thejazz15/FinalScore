package com.thejazz.finalscore.utilities;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TheJazz on 31/10/16.
 */

public class Utility {

    public static String VolleyErrorMessage(VolleyError error) {
        String message = null;
        if (error instanceof TimeoutError)
            message = "No Internet Connectivity :(";
        else if (error instanceof NoConnectionError)
            message = "No Connection to Server :(";
        else if (error instanceof AuthFailureError)
            message = "Authentication Failed.";
        else if (error instanceof ServerError)
            message = "Server Request Failed.";
        else if (error instanceof NetworkError)
            message = "No network connectivity.";
        else if (error instanceof ParseError)
            message = "Could not parse data.";
        return message;
    }

    public static int getTimeinInt(String time){
        int t = Character.getNumericValue(time.charAt(0));
        if(time.charAt(1) == ' '){
            if(time.charAt(2) == 'P')
                t = t+12;
        }
        else{
            t = 10 + Character.getNumericValue(time.charAt(1));
            if(time.charAt(3) == 'P')
                t = t+12;
        }
        return t;
    }

    public static String getTimeAsString(int time){
        if(time <= 12){
            return time+" AM";
        }else{
            return (time-12)+" PM";
        }
    }

    public static Date getDateFromStartTime(String fullDate, int startTime){
        String date = fullDate.substring(0,10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date booked = new Date();
        try {
            booked = sdf.parse(date+" "+startTime+":00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return booked;
    }
}
