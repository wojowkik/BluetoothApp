package pl.edu.pwr.s241926.mymainapp;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket  bluetoothSocket = null;
    BTconnectedThread btConnectedThread =null; SendCommandThread sendCommandThread;
    String bluetoothAddress = null, messageFromMCU, nameOfBluetoothModule = "HC-05";
    boolean isBluetoothConnection = false;

    TextView textView, textViewTEMP, textViewHUM;
    Button onButton, offButton, reconnectButton, switchButton, takeTempHumButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //text View/////////////////////////////////////
        textView        = findViewById(R.id.textView1);
        textViewHUM     = findViewById(R.id.textViewHUM);
        textViewTEMP    = findViewById(R.id.textViewTEMP);
        //Buttons//////////////////////////////////////
        onButton            = findViewById(R.id.buttonSetTime);
        offButton           = findViewById(R.id.buttonOnOff);
        switchButton        = findViewById(R.id.buttonSwitch);
        reconnectButton     = findViewById(R.id.buttonReConnect);
        takeTempHumButton   = findViewById(R.id.takeTempHumButton);
        //button listeners/////////////////////////////
        onButton.setOnClickListener(this);
        offButton.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        reconnectButton.setOnClickListener(this);
        takeTempHumButton.setOnClickListener(this);
    }
    @Override//////////////////////////ON RESUME //////////////////////
    protected void onResume() {
        super.onResume();
        btConnect();
        /*
        btConnectedThread = new BTconnectedThread(bluetoothSocket);
        btConnectedThread.start();
        if(isBluetoothConnection){
            sendCommandThread = new SendCommandThread();
            sendCommandThread.start();
            SaveLoadAppData data = new SaveLoadAppData(MainActivity.this);
            //data.saveData(2,false,false,false,false,false);
            TimeCommands commands = new TimeCommands();
            btConnectedThread.sendCommandViaBluetooth(commands.getDateCommand());
            btConnectedThread.sendCommandViaBluetooth(commands.getTimeCommand());
            btConnectedThread.sendCommandViaBluetooth(data.getCommand());
        }

         */
    }
    @Override/////////////////////////ON PAUSE ///////////////////////
    protected void onPause() {
        super.onPause();
        btDisconnect();
        sendCommandThread.stopMe();
    }
    @Override///////////////////////ON CLICK//////////////////////////
    public void onClick(View v)
    {
        if (v.getId() == R.id.buttonSetTime) {
            TimeCommands commands = new TimeCommands();
            btConnectedThread.sendCommandViaBluetooth(commands.getDateCommand());
            btConnectedThread.sendCommandViaBluetooth(commands.getTimeCommand());
        }
        if (v.getId() == R.id.buttonOnOff) {
            btConnectedThread.sendCommandViaBluetooth("____ON____");
            //btConnectedThread.sendCommandViaBluetooth("___ON___");//uszkodzone
        }
        if (v.getId() == R.id.buttonSwitch) {
            //btConnectedThread.sendCommandViaBluetooth("____SW____");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.takeTempHumButton) {
            btConnectedThread.sendCommandViaBluetooth("____TH____");
            //btConnectedThread.sendCommandViaBluetooth("123456789");//uszkodzone
            //messageDisplay();
        }
        if (v.getId() == R.id.buttonReConnect) {
            if (!isBluetoothConnection) {
                try {
                    btMakeConnection();
                } //connection=true;
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Connection device  error", Toast.LENGTH_SHORT).show();
                }
                if(isBluetoothConnection){
                    sendCommandThread = new SendCommandThread();
                    sendCommandThread.start();
                }
            }
        }
    }
    private void btConnect() {////////////////////////////BTconnect/////////////////////////////////////////
        btTurnOn();
        btFindPairedDevices();
        try {
            btMakeConnection();
            //
            btConnectedThread = new BTconnectedThread(bluetoothSocket);
            btConnectedThread.start();
            if(isBluetoothConnection){
                sendCommandThread = new SendCommandThread();
                sendCommandThread.start();
                SaveLoadAppData data = new SaveLoadAppData(MainActivity.this);
                //data.saveData(2,false,false,false,false,false);
                TimeCommands commands = new TimeCommands();
                btConnectedThread.sendCommandViaBluetooth(commands.getDateCommand());
                btConnectedThread.sendCommandViaBluetooth(commands.getTimeCommand());
                btConnectedThread.sendCommandViaBluetooth(data.getCommand());
            }
            //
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
        }
    }
    private void btMakeConnection() {////////////////////////////////////////BTmakeConnection//////////////////////////////
        try {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(bluetoothAddress);
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
            bluetoothSocket.connect();
            textView.setText("BT Address: " + bluetoothAddress);
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            isBluetoothConnection = true;

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
        }

    }
    private void btDisconnect() {//////////////////////////////////BTdisconnect///////////////////////////////
        try {
            bluetoothSocket.close();
            textView.setText("disconect");
            isBluetoothConnection = false;
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Could not close the client socket", Toast.LENGTH_SHORT).show();
        }
    }
    private void btFindPairedDevices() {/////////////////////////////BTfindPairedDevices//////////////////////////////
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        textView.setText("");
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                if (deviceName.equals(nameOfBluetoothModule)) {
                    textView.append("D: " + deviceName + "\nA: " + deviceHardwareAddress + "\n");
                    bluetoothAddress = deviceHardwareAddress;
                }
            }

        }
    }
    private void btTurnOn() {///////////////////////////////////////BTturnOn////////////////////////////////////////////
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getApplicationContext(), "Device desn't support bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }
/////////////////////////////////////create new class for connect thread//////////////////////////////////////////////////////////
    private class BTconnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        BTconnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[15];
            int bytes;
            String text;
            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    if(buffer[0] == 'M')
                    {
                        messageFromMCU = ""; buffer[0] = ' ';
                    }
                    text = new String(buffer, 0, bytes);
                    if(bytes != 0)
                    {
                        messageFromMCU = messageFromMCU + text;
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        //write method
        void sendCommandViaBluetooth(String input) {
            try {
                mmOutStream.write(input.getBytes()); //write bytes over BT connection via outstream //converts entered String into bytes
                sendCommandThread.setIncomeText(messageFromMCU);
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();
            }


        }
    }
    /////////////////////////////
    private class SendCommandThread extends Thread{
        String temp = "TEMP", hum = "HUM", incomeText;
        volatile boolean isThreadOn = true;

        private void setText(String text){
            if(text != null)//[rzy uruchomieniu jest null
            {
                if(text.charAt(1) == 'T' && text.charAt(2) == 'E' && text.charAt(3) == 'M') {
                    temp = "TEMP: "+text.charAt(4)+text.charAt(5)+"°C";
                }
                if(text.charAt(6) == 'H' && text.charAt(7) == 'U' && text.charAt(8) == 'M') {
                    hum = "HUM: "+text.charAt(9)+text.charAt(10)+"%";
                }
            }
        }
        void setIncomeText(String text){
            incomeText = text;
        }
        void stopMe()
        {
            isThreadOn = false;
        }

        @Override
        public void run() {
            while (isThreadOn)
            {
                try{
                    sleep(1000);//opóźnienie kolejnego pomiaru
                    btConnectedThread.sendCommandViaBluetooth("____TH____");
                    sleep(500);
                    setText(incomeText);
                    runOnUiThread(new Runnable() { //https://medium.com/@yossisegev/understanding-activity-runonuithread-e102d388fe93
                        @Override
                    public void run() {
                                textViewTEMP.setText(temp);
                                textViewHUM.setText(hum);
                    }
                });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}