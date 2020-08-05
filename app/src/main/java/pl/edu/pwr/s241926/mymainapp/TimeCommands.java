package pl.edu.pwr.s241926.mymainapp;

import android.icu.text.SimpleDateFormat;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

class TimeCommands
{
    private Date currentTime;
    TimeCommands(){
        currentTime = Calendar.getInstance().getTime();
        System.out.println(currentTime);
    }
    String getDateCommand(){
        return "T" + DateFormat.getTimeInstance().format(currentTime.getTime());//9 znakow
    }
    String getTimeCommand(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String date = sdf.format(currentTime);
        date = "D" + date; //D - oznaczenie daty
        System.out.println(date);
        return date; //9 znaków
    }
}
