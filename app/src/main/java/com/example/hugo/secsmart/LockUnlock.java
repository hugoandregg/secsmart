package com.example.hugo.secsmart;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.hugo.secsmart.R;
import com.example.hugo.secsmart.dominio.Porta;
import com.example.hugo.secsmart.utils.ConexaoHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
        Method method = null;
        try {
            method = device.getClass().getMethod("getUuids", null);
            ParcelUuid[] uuid = (ParcelUuid[]) method.invoke(device, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        dvcText = (TextView) findViewById(R.id.dvcText);
        dvcText.setText(device.getName());

        trvBtn = (ToggleButton) findViewById(R.id.trvBtn);
        trvBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "travado!", Toast.LENGTH_SHORT).show();
                    //sendDataToPairedDevide("t", device, uuid[0]);
                } else {
                    Toast.makeText(getApplicationContext(), "destravado!", Toast.LENGTH_SHORT).show();
                    //sendDataToPairedDevide("d", device, uuid[0]);
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
        buscarPortas();
    }

    //https://secsmart.herokuapp.com/users
    private List<Porta> gerarPortasFromJson(String dados) {
        List<Porta> resultado = new ArrayList<Porta>();
        try {
            JSONObject jsonObject = new JSONObject(dados);
            //JSONObject emb = jsonObject.getJSONObject("_embedded");
            JSONArray ja = jsonObject.getJSONArray("doors");
            for(int i = 0; i<= ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                Porta f = new Porta();
                f.setNumero(jo.getString("name"));
                f.setEstado(jo.getString("state"));
                resultado.add(f);
            }
            return resultado;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public void buscarPortas() {
        new ListagemTask().execute();
    }

    private class ListagemTask extends
            AsyncTask<String,Integer, List<Porta>> {

        @Override
        protected List<Porta> doInBackground(String... urls) {
            String url = "https://secsmart.herokuapp.com/" + MainActivity.mBluetoothAdapter.getAddress() + ".json";
            String resposta = ConexaoHttp.get(url);
            return gerarPortasFromJson(resposta);
        }
        @Override
        protected void onPostExecute(List<Porta> result) {
            super.onPostExecute(result);
            Intent i = new Intent(LockUnlock.this, myDoors.class);
            i.putExtra("portas", (Serializable) result);
            startActivity(i);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
