package pl.edu.pwr.s241926.mymainapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class SaveLoadAppData
{
    private SharedPreferences myPreferences;
    SaveLoadAppData(Context context){
        myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    void saveData(int refreshTime, boolean disp1, boolean disp2, boolean disp3, boolean disp4, boolean disp5) {
        SharedPreferences.Editor myEditor = myPreferences.edit();
        String napis = "REFRESH:";
        if(refreshTime < 10){
            napis+="0"+refreshTime;
        }
        else{
            napis+=refreshTime;
        }
        myEditor.putString("REFRESH", napis);
        myEditor.putBoolean("DISP1", disp1);
        myEditor.putBoolean("DISP2", disp2);
        myEditor.putBoolean("DISP3", disp3);
        myEditor.putBoolean("DISP4", disp4);
        myEditor.putBoolean("DISP5", disp5);
        myEditor.apply();
    }
    String getDataREFRESH() {
        return myPreferences.getString("REFRESH", "REFRESH:10");
    }
    String getDataDISP(int displayOption) {
        String key = "DISP"+displayOption;
        if(myPreferences.getBoolean(key,true)) {
            return "DISPLAY"+displayOption+":"+"T";//DISPLAY1:T - Display1_TRUE
        }
        else
            return "DISPLAY"+displayOption+":"+"F";//DISPLAY1:F - Display1_FALSE
    }
    //R10DISTTFFT
}
