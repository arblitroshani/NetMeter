package com.arblitroshani.netmeter;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private String st;
    private Socket s;

    private InputStream sIn;
    private OutputStream sOut;
    private DataInputStream dis;
    private DataOutputStream dos;

    private Context context;
    private TextView tvResult;

    public Client(String host, int port, String message, Context context, int tvResultClient) throws IOException {
        this.context = context;
        tvResult = ((Activity)context).findViewById(tvResultClient);

        new Thread(() -> {
            try {
                s = new Socket(host, port);
                serverInteract(message);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dis != null) dis.close();
                    if (sIn != null) sIn.close();
                    if (s != null) s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void serverInteract(String message) throws IOException {
        // Send message to server
        sOut = s.getOutputStream();
        dos = new DataOutputStream(sOut);
        dos.writeUTF(message);

        // Get input file from server and read it
        sIn = s.getInputStream();
        dis = new DataInputStream(sIn);
        st = dis.readUTF();
        output("Server response: " + st);
    }

    private void output(String s) {
        ((Activity)context).runOnUiThread(() -> tvResult.setText(tvResult.getText().toString() + "\n" + s));
    }
}