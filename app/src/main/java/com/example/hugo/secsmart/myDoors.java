package com.example.hugo.secsmart;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.hugo.secsmart.adapter.PortaListaAdapter;
import com.example.hugo.secsmart.dominio.Porta;

import java.util.List;


public class myDoors extends AppCompatActivity {
    private ListView portasListView;
    private PortaListaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_doors);

        List<Porta> fornecedores = (List<Porta>)
                getIntent().getSerializableExtra("fornecedores");

        portasListView = (ListView) findViewById(R.id.listPortas);
        adapter = new PortaListaAdapter(this,
                R.layout.list_portas, fornecedores);

        portasListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_doors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
