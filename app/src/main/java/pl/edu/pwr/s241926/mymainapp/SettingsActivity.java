package pl.edu.pwr.s241926.mymainapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox checkBox1, checkBox2, checkBox3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ///////////CHECKBOX///////////////////////////
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        //////////CLICK LISTENER//////////////////////
        checkBox1.setOnClickListener(this);
        checkBox2.setOnClickListener(this);
        checkBox3.setOnClickListener(this);
        /////////BUTTON //////////////////////////////
        Button buttonEnter = findViewById(R.id.button1);
        buttonEnter.setOnClickListener(this);
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
            SaveLoadAppData save = new SaveLoadAppData(SettingsActivity.this);
            save.saveData(checkBox2.isChecked(), checkBox3.isChecked());
            Toast.makeText(getApplicationContext(), "Settings saved - please wait", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
