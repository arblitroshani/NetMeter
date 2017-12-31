package com.arblitroshani.netmeter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.arblitroshani.netmeter.measurement.MeasurementActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.b1).setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this,  ClientActivity.class);
            startActivity(i);
        });
        findViewById(R.id.b2).setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this,  ServerActivity.class);
            startActivity(i);
        });
        findViewById(R.id.b3).setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this,  ServerClientActivity.class);
            startActivity(i);
        });
        findViewById(R.id.b4).setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this,  MeasurementActivity.class);
            startActivity(i);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
