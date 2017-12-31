package com.arblitroshani.netmeter;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class ServerActivity extends AppCompatActivity {

    TextView tvIp, tvResult;
    EditText etPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        tvIp = findViewById(R.id.tvIp);
        tvResult = findViewById(R.id.tvResultServer);
        etPort = findViewById(R.id.etPort);

        tvIp.setText("IP address: " + getIpAddress());

        findViewById(R.id.bStartServer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // print Ip
                int port = Integer.parseInt(etPort.getText().toString());
                try {
                    new Server(port, ServerActivity.this, R.id.tvResultServer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getIpAddress() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ip = wifiMgr.getConnectionInfo().getIpAddress();
        return Formatter.formatIpAddress(ip);
    }
}
