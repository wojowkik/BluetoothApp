package pl.edu.pwr.s241926.mymainapp;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.NumberPicker;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener,View.OnClickListener {

    private CheckBox checkBox1, checkBox2, checkBox3, checkBoxAlarm;
    private int alarmHour=0, alarmMinute=0;
    SaveLoadAppData save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        /////////////SAVE/////////////////////////////
        save = new SaveLoadAppData(SettingsActivity.this);
        ///////////CHECKBOX///////////////////////////
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBoxAlarm = findViewById(R.id.checkBoxAlarm);
        //////////CLICK LISTENER//////////////////////
        checkBox1.setOnClickListener(this);
        checkBox2.setOnClickListener(this);
        checkBox3.setOnClickListener(this);
        checkBoxAlarm.setOnClickListener(this);
        /////////BUTTON //////////////////////////////
        Button buttonEnter = findViewById(R.id.button1);
        buttonEnter.setOnClickListener(this);
        ///////////////////////////////////////////////
        NumberPicker numberPicker1 = findViewById(R.id.numerPicker1);
        numberPicker1.setOnValueChangedListener(this);
        numberPicker1.setMinValue(0); numberPicker1.setMaxValue(23);
        numberPicker1.setValue(save.getSavedHours());
        alarmHour = save.getSavedHours();//przypisanie wartości poprzedniego zapisu do zmiennej, jeśli nie zostanie nadpisana wonValueChange to pozostanie nie zmieniona z savem

        NumberPicker numberPicker2 = findViewById(R.id.numerPicker2);
        numberPicker2.setOnValueChangedListener(this);
        numberPicker2.setMinValue(0); numberPicker2.setMaxValue(59);
        numberPicker2.setValue(save.getSavedMinutes());
        alarmMinute = save.getSavedMinutes();
    }
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if(picker.getId() == R.id.numerPicker1)
        {
            alarmHour = newVal;
        }
        if(picker.getId() == R.id.numerPicker2)
        {
            alarmMinute = newVal;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.checkBox1) {
            checkBox2.setChecked(true);
            checkBox3.setChecked(true);
        }
        else {
            checkBox1.setChecked(false);
        }
        if(v.getId() == R.id.button1) {
            save.saveData(checkBox2.isChecked(), checkBox3.isChecked(), alarmHour, alarmMinute, checkBoxAlarm.isChecked());
            Toast.makeText(getApplicationContext(), "Settings saved - please wait", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
