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
    public static final int REQUEST_ENABLE_BT = 1;//musi być tak
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket  bluetoothSocket = null;
    BTconnectedThread btConnectedThread =null; SendCommandThread sendCommandThread;
    String bluetoothAddress = null, messageFromMCU, nameOfBluetoothModule = "HC-05";
    boolean isBluetoothConnection = false;

    TextView textView, textViewTEMP, textViewHUM;
    Button reconnectButton, disconnectButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //text View/////////////////////////////////////
        textView        = findViewById(R.id.textView1);
        textViewHUM     = findViewById(R.id.textViewHUM);
        textViewTEMP    = findViewById(R.id.textViewTEMP);
        //Buttons//////////////////////////////////////
        reconnectButton     = findViewById(R.id.buttonReConnect);
        disconnectButton    = findViewById(R.id.buttonDisconnect);
        settingsButton      = findViewById(R.id.buttonSettings);
        //button listeners/////////////////////////////
        reconnectButton.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
    }
    @Override//////////////////////////ON RESUME //////////////////////
    protected void onResume() {
        super.onResume();
        btConnect();
    }
    @Override/////////////////////////ON PAUSE ///////////////////////
    protected void onPause() {
        super.onPause();
        btDisconnect();
    }
    @Override///////////////////////ON CLICK//////////////////////////
    public void onClick(View v) {
        if (v.getId() == R.id.buttonReConnect) {
            btDisconnect();
            btConnect();
        }
        if(v.getId() == R.id.buttonDisconnect) {
            btDisconnect();
        }
        if (v.getId() == R.id.buttonSettings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }
    private void btConnect() {////////////////////////////BTconnect/////////////////////////////////////////
        btTurnOn();
        btFindPairedDevices();
        try {
            //btMakeConnection();
            //
                btConnectedThread = new BTconnectedThread();
                btConnectedThread.start();
            if(isBluetoothConnection) {
                sendCommandThread = new SendCommandThread();
                sendCommandThread.start();
                SaveLoadAppData data = new SaveLoadAppData(MainActivity.this);
                TimeCommands commands = new TimeCommands();
                btConnectedThread.sendCommandViaBluetooth(commands.getDateCommand());
                btConnectedThread.sendCommandViaBluetooth(commands.getTimeCommand());
                btConnectedThread.sendCommandViaBluetooth(data.getDisplayMode());
                btConnectedThread.sendCommandViaBluetooth(data.getAlarm());//alarm, 18:20, ST- stan = 1 - włączony
                //textView.setText(data.getAlarm());
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
        }
    }

    private void btDisconnect() {//////////////////////////////////BTdisconnect///////////////////////////////
        try {
            bluetoothSocket.close();
            isBluetoothConnection = false;
            textView.setText(R.string.disconnected);
        } catch (Exception e) {//powodowało błędy z połączeniem, uruchamianiem BT - "IOExeption" - po zamianie działą
            Toast.makeText(getApplicationContext(), "Could not close the client socket", Toast.LENGTH_SHORT).show();
        }
        try {
            sendCommandThread.stopMe();
            btConnectedThread.stopMe();
        } catch (Exception e) {//powodowało błędy z połączeniem, uruchamianiem BT - "IOExeption" - po zamianie działą
            Toast.makeText(getApplicationContext(), "Could not close thread", Toast.LENGTH_SHORT).show();
        }
    }
    private void btFindPairedDevices() {/////////////////////////////BTfindPairedDevices//////////////////////////////
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                if (deviceName.equals(nameOfBluetoothModule)) {
                    bluetoothAddress = device.getAddress();
                    bluetoothAdapter.cancelDiscovery();
                }
            }
        }
    }
    private void btTurnOn() {///////////////////////////////////////BTturnOn////////////////////////////////////////////
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getApplicationContext(), "Device desn't support bluetooth", Toast.LENGTH_SHORT).show();
            System.exit(0);
        } else {
            if (!bluetoothAdapter.isEnabled()) {//może lepiej while
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }
/////////////////////////////////////create new class for connect thread//////////////////////////////////////////////////////////
    private class BTconnectedThread extends Thread {
        private final InputStream bluetoothInStream;
        private final OutputStream bluetoothOutStream;
        private volatile boolean isThreadOn=true;
        //creation of the connect thread
        BTconnectedThread() {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(bluetoothAddress);
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                bluetoothSocket.connect();
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                isBluetoothConnection = true;
                textView.setText(R.string.connected);
                //Create I/O streams for connection
                tmpIn = bluetoothSocket.getInputStream();
                tmpOut = bluetoothSocket.getOutputStream();
            }
            catch (Exception e) {
                try {
                    bluetoothSocket.close();
                } catch (Exception closeException) {
                    Toast.makeText(getApplicationContext(), "Could not close the client socket", Toast.LENGTH_SHORT).show();
                }
                textView.setText(R.string.disconnected);
                //Toast.makeText(getApplicationContext(), "ERROR - try connect again", Toast.LENGTH_SHORT).show();
            }
            bluetoothInStream = tmpIn;
            bluetoothOutStream = tmpOut;
        }
        void stopMe(){
            isThreadOn=false;
        }

        public void run() {
            byte[] buffer = new byte[100];
            int bytes;
            String text;
            // Keep looping to listen for received messages
            while (isThreadOn) {
                try {
                    bytes = bluetoothInStream.read(buffer);            //read bytes from input buffer
                    if(buffer[0] == 'M')
                    {
                        messageFromMCU = ""; buffer[0] = ' ';
                    }
                    text = new String(buffer, 0, bytes);
                    if(bytes != 0)
                    {
                        messageFromMCU = messageFromMCU + text;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        }
        //write method
        void sendCommandViaBluetooth(String input) {
            try {
                bluetoothOutStream.write(input.getBytes()); //write bytes over BT connection via outstream //converts entered String into bytes
                //sendCommandThread.setIncomeText(messageFromMCU);
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "THREAD Failure", Toast.LENGTH_LONG).show();
                System.exit(0);
            }
        }
    }
    /////////////////////////////
    private class SendCommandThread extends Thread{
        String temp = "Temperature", hum = "Humidity", incomeText;
        volatile boolean isThreadOn = true;

        private void setText(String text){
            if(text != null)//przy uruchomieniu jest null
            {
                if(text.length()==11){
                    if(text.charAt(1) == 'T' && text.charAt(2) == 'E' && text.charAt(3) == 'M') {
                        temp = "Temperature: "+text.charAt(4)+text.charAt(5)+"°C";
                    }
                    if(text.charAt(6) == 'H' && text.charAt(7) == 'U' && text.charAt(8) == 'M') {
                        hum = "Humidity: "+text.charAt(9)+text.charAt(10)+"%";
                    }
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
                    btConnectedThread.sendCommandViaBluetooth("____TH____");
                    sleep(2500);//opóźnienie kolejnego pomiaru
                    setText(messageFromMCU);
                    runOnUiThread(new Runnable() { //https://medium.com/@yossisegev/understanding-activity-runonuithread-e102d388fe93
                        @Override
                    public void run() {
                                textViewTEMP.setText(temp);
                                textViewHUM.setText(hum);
                    }});
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}