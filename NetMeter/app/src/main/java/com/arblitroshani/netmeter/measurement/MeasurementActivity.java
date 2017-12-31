package com.arblitroshani.netmeter.measurement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arblitroshani.netmeter.R;

public class MeasurementActivity extends AppCompatActivity {

    EditText etPort, etNumProbes, etMessageSize;
    Button bStartServer, bStartClient;
    TextView tvServerResult, tvClientResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        etPort = findViewById(R.id.etPort);
        etNumProbes = findViewById(R.id.etNumProbes);
        etMessageSize = findViewById(R.id.etMsgSize);
        bStartServer = findViewById(R.id.bStartServer);
        bStartClient = findViewById(R.id.bStartClient);
        tvServerResult = findViewById(R.id.tvResultServer);
        tvClientResult = findViewById(R.id.tvResultClient);

        bStartServer.setOnClickListener(view -> {
            // Start the server
            int port = Integer.parseInt(etPort.getText().toString());

            new ServerM(port);
            tvServerResult.setText("Server Started");
            bStartClient.setEnabled(true);
            bStartServer.setEnabled(false);
            etPort.setEnabled(false);
        });

        bStartClient.setOnClickListener(view -> {
            int port = Integer.parseInt(etPort.getText().toString());
            int probe = Integer.parseInt(etNumProbes.getText().toString());
            int msgSize = Integer.parseInt(etMessageSize.getText().toString());

            // Generate RTT connection setup message object amd start client
            ConnSetupMsg csm = new ConnSetupMsg('s', "tput", probe, msgSize,  0);
            new ClientM("127.0.0.1", port, csm, MeasurementActivity.this, R.id.tvResultClient, R.id.chartRtt, R.id.chartTput);
        });
    }
}
