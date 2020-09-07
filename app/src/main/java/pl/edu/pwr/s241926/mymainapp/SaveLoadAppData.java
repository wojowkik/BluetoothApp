package pl.edu.pwr.s241926.mymainapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class SaveLoadAppData {
    private SharedPreferences myPreferences;

    SaveLoadAppData(Context context) {
        myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    void saveData(boolean disp8, boolean disp9) {
        SharedPreferences.Editor myEditor = myPreferences.edit();
        String napis = "";
        myEditor.putString("REFRESH", napis);//10 np.
        myEditor.putBoolean("DISP8", disp8);
        myEditor.putBoolean("DISP9", disp9);
        myEditor.apply();
    }

    String getCommand() {
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
}
