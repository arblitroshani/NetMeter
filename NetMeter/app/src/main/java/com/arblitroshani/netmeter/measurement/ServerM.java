package com.arblitroshani.netmeter.measurement;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerM {

    private ServerSocket server;
    private Socket socket;

    private InputStream sIn;
    private OutputStream sOut;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ConnSetupMsg csm;
    private String st;

    private static final String MESSAGE_VALID_READY = "200 OK: Ready";
    private static final String MESSAGE_VALID_CLOSING = "200 OK: Closing Connection";
    private static final String MESSAGE_INVALID_CONNECTION = "404 ERROR: Invalid Connection Setup Message";
    private static final String MESSAGE_INVALID_MEASURE = "404 ERROR: Invalid Measurement Message";
    private static final String MESSAGE_INVALID_CLOSING = "404 ERROR: Invalid Connection Termination Message";
    private static final String MESSAGE_INVALID_MESSAGE = "404 ERROR: Invalid Message";

    public ServerM(int port) {
        new Thread(() -> {
            try {
                // Get port number from constructor
                server = new ServerSocket(port);
                startListening();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }).start();
    }

    private void startListening() throws IOException {
        // Wait  and accept client request
        socket = server.accept();

        sIn = socket.getInputStream();
        sOut = socket.getOutputStream();
        dis = new DataInputStream(sIn);
        dos = new DataOutputStream(sOut);

        int currentProbe = 1;
        int maxProbe = 1;

        while (!socket.isClosed()) {
            // Get string from client

            st = dis.readUTF();
            char firstChar = st.charAt(0);
            switch (firstChar) {
                case 's':   // Initial setup
                    // check if valid connection setup message
                    if (ConnSetupMsg.isValidMessage(st)) {
                        dos.writeUTF(MESSAGE_VALID_READY);
                        csm = ConnSetupMsg.parseMessage(st);
                        currentProbe = 1;
                        maxProbe = csm.getNumProbes();
                    } else {
                        dos.writeUTF(MESSAGE_INVALID_CONNECTION);
                        closeConnection();
                    }
                    break;
                case 'm':   // Measurement Phase
                    // validate rest of message
                    if (validMeasurementMessage(st, currentProbe++, maxProbe, csm.getMessageSize())) {
                        dos.writeUTF(st);
                    } else {
                        dos.writeUTF(MESSAGE_INVALID_MEASURE);
                        closeConnection();
                    }
                    break;
                case 't':
                    // validate message format
                    if (st.equals("s ")) {
                        dos.writeUTF(MESSAGE_VALID_CLOSING);
                    } else {
                        dos.writeUTF(MESSAGE_INVALID_CLOSING);
                    }
                    closeConnection();
                    break;
                default:
                    dos.writeUTF(MESSAGE_INVALID_MESSAGE);
                    closeConnection();
                    break;
            }
        }
    }

    private boolean validMeasurementMessage(String st, int currentProbe, int maxProbe, int messageSize) {
        String[] elements = st.split("\\s");
        // if (!elements[0].equals('m')) return false;   // already tested
        int messageProbe = Integer.parseInt(elements[1]);
        if (messageProbe != currentProbe || messageProbe > maxProbe) return false;

        String header = "m " + elements[1] + " ";
        return (st.length() - header.length() == messageSize);
    }

    private void closeConnection() {
        try {
            if (dos != null) dos.close();
            if (sOut != null) sOut.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}