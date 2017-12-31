package com.arblitroshani.netmeter;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;
    private Socket s;
    private String st;

    private InputStream sIn;
    private OutputStream sOut;
    private DataInputStream dis;
    private DataOutputStream dos;

    private Context context;
    private TextView tvResult;

    public Server(int port, Context context, int tvResultServer) throws IOException {
        this.context = context;
        tvResult = ((Activity)context).findViewById(tvResultServer);

        new Thread(() -> {
            try {
                // Get port number from constructor
                server = new ServerSocket(port);
                output("Server Started.");
                startListening();
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

    private void startListening() throws IOException {
        // Wait  and accept client request
        s = server.accept();
        output("Client connected");

        // Get string from client
        sIn = s.getInputStream();
        dis = new DataInputStream(sIn);
        st = dis.readUTF();
        output("Got message: " + st);

        // Echo back the string
        sOut = s.getOutputStream();
        dos = new DataOutputStream(sOut);
        dos.writeUTF(st.toUpperCase());
    }

    private void output(String s) {
        ((Activity)context).runOnUiThread(() -> tvResult.setText(tvResult.getText().toString() + "\n" + s));
    }
}