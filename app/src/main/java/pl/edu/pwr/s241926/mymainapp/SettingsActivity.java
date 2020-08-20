package pl.edu.pwr.s241926.mymainapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener, View.OnClickListener {

    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;
    private int refreshValue=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ///////////CHECKBOX////////////////////////////
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBox5 = findViewById(R.id.checkBox5);
        //////////CLICK LISTENER//////////////////////
        checkBox1.setOnClickListener(this);
        checkBox2.setOnClickListener(this);
        checkBox3.setOnClickListener(this);
        checkBox4.setOnClickListener(this);
        checkBox5.setOnClickListener(this);
/////////////////BUTTON //////////////////////////////////////
        Button buttonEnter = findViewById(R.id.button1);
        buttonEnter.setOnClickListener(this);
        ////////NUMBER PICKER ///////////////////////////////
        NumberPicker numberPicker = findViewById(R.id.numerPicker);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setMinValue(1); numberPicker.setMaxValue(99);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        refreshValue = newVal;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.checkBox1) {
            checkBox2.setChecked(false);
            checkBox3.setChecked(false);
            checkBox4.setChecked(false);
            checkBox5.setChecked(false);
        }
        else {
            checkBox1.setChecked(false);
        }
        if(v.getId() == R.id.button1) {
            SaveLoadAppData save = new SaveLoadAppData(SettingsActivity.this);
            save.saveData(refreshValue, checkBox2.isChecked(), checkBox3.isChecked(), checkBox4.isChecked(), checkBox5.isChecked());
            checkBox1.setText(save.getCommand());//do usuniecie
            Toast.makeText(getApplicationContext(), "Settings saved - please wait", Toast.LENGTH_SHORT).show();
            finish();//close activity
        }
    }
}
