package pl.edu.pwr.s241926.mymainapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class SaveLoadAppData {
    private SharedPreferences myPreferences;

    SaveLoadAppData(Context context) {
        myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    void saveData(boolean disp8, boolean disp9, int alarmHour, int alarmMinute, boolean isAlarmOn) {
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putBoolean("DISP8", disp8);
        myEditor.putBoolean("DISP9", disp9);
        myEditor.putInt("alarmHour", alarmHour);
        myEditor.putInt("alarmMinute", alarmMinute);
        myEditor.putBoolean("isAlarmOn", isAlarmOn);
        myEditor.apply();
    }

    String getDisplayMode() {
        String command = "SETTINGS";
        if (myPreferences.getBoolean("DISP8", true)) {
            command += "1";
        } else {
            command += "0";
        }
        if (myPreferences.getBoolean("DISP9", true)) {
            command += "1";
        } else {
            command += "0";
        }
        return command;
    }
    String getAlarm()//A18:00ST1
    {
        String command = "AL";
        if(myPreferences.getInt("alarmHour",12) < 10)
        {
            command+="0"+myPreferences.getInt("alarmHour",12)+":";
        }
        else
        {
            command+=""+myPreferences.getInt("alarmHour",12)+":";
        }
        if(myPreferences.getInt("alarmMinute",12) < 10)
        {
            command+="0"+myPreferences.getInt("alarmMinute",12)+"ST";
        }
        else
        {
            command+=""+myPreferences.getInt("alarmMinute",12)+"ST";
        }
        if(!myPreferences.getBoolean("isAlarmOn",false))
        {
            command+="0";
        }
        else
        {
            command+="1";
        }
        return command;
    }
    int getSavedHours()
    {
        return myPreferences.getInt("alarmHour",12);
    }
    int getSavedMinutes()
    {
        return myPreferences.getInt("alarmMinute",12);
    }
}
