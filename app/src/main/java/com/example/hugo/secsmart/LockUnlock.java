package com.example.hugo.secsmart;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.hugo.secsmart.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Ad on 02/06/2015.
 */
public class LockUnlock extends Activity
{
    TextView dvcText;
    ToggleButton trvBtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockunlock);

        BluetoothDevice device = (BluetoothDevice) getIntent().getExtras().getParcelable("device");
        UUID uuid = device.getUuids()[0].getUuid();

        dvcText = (TextView) findViewById(R.id.dvcText);
        dvcText.setText(device.getName());

        trvBtn = (ToggleButton) findViewById(R.id.trvBtn);
        trvBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "destravado!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "travado!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void sendDataToPairedDevice(String message ,BluetoothDevice device, UUID uuid){
        byte[] toSend = message.getBytes();
        try {
            BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            OutputStream mmOutStream = socket.getOutputStream();
            mmOutStream.write(toSend);
            // Your Data is sent to  BT connected paired device ENJOY.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void myDoors(View view) {
        startActivity(new Intent(this, myDoors.class));
    }
}
