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
    void saveData(int refreshTime, boolean disp6, boolean disp7, boolean disp8, boolean disp9) {
        SharedPreferences.Editor myEditor = myPreferences.edit();
        String napis = "";
        if(refreshTime < 10){
            napis+="0"+refreshTime;
        }
        else{
            napis+=refreshTime;
        }
        myEditor.putString("REFRESH", napis);//10 np.
        myEditor.putBoolean("DISP6", disp6);
        myEditor.putBoolean("DISP7", disp7);
        myEditor.putBoolean("DISP8", disp8);
        myEditor.putBoolean("DISP9", disp9);
        myEditor.apply();
    }
    String getCommand() {
        String napis = "R:";
        napis += myPreferences.getString("REFRESH", "03");
        napis+="D:    ";
        for(int i = 6 ; i<10 ; i++){
            napis = setChar(i, napis);
        }
        return napis;
    }
    private String setChar(int dispPos, String comand) {
        char[] chars = comand.toCharArray();
        String key = "DISP"+dispPos;
        if(myPreferences.getBoolean(key,true)) {
            chars[dispPos] = '1';
        }
        else{
            chars[dispPos] = '0';
        }
        return String.valueOf(chars);
    }
}
