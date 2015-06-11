package com.example.hugo.secsmart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hugo.secsmart.R;
import com.example.hugo.secsmart.dominio.Porta;
import com.example.hugo.secsmart.utils.ConexaoHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_ENABLE_BT = 1;
    public static BluetoothAdapter mBluetoothAdapter = null;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    public ListView newDevicesListView;
    public TextView macTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newDevicesListView = (ListView) findViewById(R.id.new_devices);

        mNewDevicesArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, 0);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = newDevicesListView.getItemAtPosition(position);
                Intent myIntent = new Intent(MainActivity.this, LockUnlock.class);

                final String info = listItem.toString();
                //get the device address when click the device item
                String address = info.substring(info.length() - 17).toUpperCase();
                //connect the device when item is click
                BluetoothDevice connect_device = null;
                connect_device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);

                if (connect_device == null){
                    Toast.makeText(getApplicationContext(), "bluetooth não encontrado!", Toast.LENGTH_LONG).show();
                }

                myIntent.putExtra("device", (connect_device));
                MainActivity.this.startActivity(myIntent);
            }
        });

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);


        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    public void onClickbtnActivateBluetooth(View view)
    {

        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, "O Bluetooth não está disponível!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        else if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        Toast.makeText(this, "O Bluetooth foi ativado!", Toast.LENGTH_LONG).show();
        findViewById(R.id.btnSearchDevices).setEnabled(true);


    }

    public void onClickbtnSearchDevices(View view)
    {
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, 0);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);

        mBluetoothAdapter.startDiscovery();
        Toast.makeText(this, "Procurando dispositivos Bluetooth!", Toast.LENGTH_LONG).show();
        // Find and set up the ListView for newly discovered devices
        if (mBluetoothAdapter.getBondedDevices().size() == 0){
            Toast.makeText(this, "Nenhum aparelho encontrado!", Toast.LENGTH_LONG).show();
        }

    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mNewDevicesArrayAdapter.notifyDataSetChanged();
                }


            }
        }
    };


    public void myDoors(MenuItem item) {
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
            String url = "https://secsmart.herokuapp.com/" + mBluetoothAdapter.getAddress() + ".json";
            String resposta = ConexaoHttp.get(url);
            return gerarPortasFromJson(resposta);
        }
        @Override
        protected void onPostExecute(List<Porta> result) {
            super.onPostExecute(result);
            Intent i = new Intent(MainActivity.this, myDoors.class);
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