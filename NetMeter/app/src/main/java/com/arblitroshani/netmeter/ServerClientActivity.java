package com.arblitroshani.netmeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class ServerClientActivity extends AppCompatActivity {

    EditText etPort, etMessage;
    Button bStartServer, bStartClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_client);

        etPort = findViewById(R.id.etPort);
        etMessage = findViewById(R.id.etMessage);
        bStartServer = findViewById(R.id.bStartServer);
        bStartClient = findViewById(R.id.bStartClient);

        bStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int port = Integer.parseInt(etPort.getText().toString());
                try {
                    new Server(port, ServerClientActivity.this, R.id.tvResultServer);
                    bStartClient.setEnabled(true);
                    bStartServer.setEnabled(false);
                    etPort.setEnabled(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        bStartClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString();
                int port = Integer.parseInt(etPort.getText().toString());
                try {
                    new Client("127.0.0.1", port, message, ServerClientActivity.this, R.id.tvResultClient);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
