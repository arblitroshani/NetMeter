package com.arblitroshani.netmeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class ClientActivity extends AppCompatActivity {

    EditText etHost, etPort, etMessage;
    TextView tvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        etHost = findViewById(R.id.etHost);
        etPort = findViewById(R.id.etPort);
        etMessage = findViewById(R.id.etMessage);
        tvResponse = findViewById(R.id.tvResponse);

        findViewById(R.id.bStartServer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String host = etHost.getText().toString();
                int port = Integer.parseInt(etPort.getText().toString());
                String message = etMessage.getText().toString();

                try {
                    new Client(host, port, message, ClientActivity.this, R.id.tvResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
