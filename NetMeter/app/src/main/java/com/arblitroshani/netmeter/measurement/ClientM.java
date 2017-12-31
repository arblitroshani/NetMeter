package com.arblitroshani.netmeter.measurement;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientM {

    private String st;
    private Socket s;

    private InputStream sIn;
    private OutputStream sOut;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ConnSetupMsg csm;

    private Context context;
    private TextView tvResult;
    private LineChart chartRtt, chartTput;

    private static final int SKIP_COUNT = 2;

    private static final int[] MESSAGE_SIZE_RTT  = {1, 100, 200, 400, 800, 1000};
    private static final int[] MESSAGE_SIZE_TPUT = {1*1024, 2*1024, 4*1024, 8*1024, 16*1024, 32*1024};

    public ClientM(String host, int port, ConnSetupMsg csm, Context context, int tvResultClient, int chartId, int chart2Id) {
        this.context = context;
        chartRtt = ((Activity) context).findViewById(chartId);
        chartTput = ((Activity) context).findViewById(chart2Id);
        tvResult = ((Activity)context).findViewById(tvResultClient);

        // this(host, port, csn.generateMessage());
        output("Client started");
        this.csm = csm;

        new Thread(() -> {
            try {
                // Get host and port from constructor
                // Open connection to server, on specified port
                s = new Socket(host, port);
                serverInteract();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }).start();
    }

    private void serverInteract() throws IOException {
        sOut = s.getOutputStream();
        sIn = s.getInputStream();
        dos = new DataOutputStream(sOut);
        dis = new DataInputStream(sIn);

        // Send message to server
        dos.writeUTF(csm.generateMessage());

        // Get reply from server
        st = dis.readUTF();

        output("Server response: " + st);
        if (st.charAt(0) == '2') {
            calculate(csm.getMeasurementType());
        } else {
            output("Server responded with error");
        }
    }

    private void calculate(String msrType) throws IOException {
        // generate payload
        byte[] payloadBytes = new byte[csm.getMessageSize()];
        String payload = new String(payloadBytes);
        int probes = csm.getNumProbes();
        double[] rttValues = new double[probes];
        double[] tputValues = new double[probes];
        double rtt_av = 0;
        double tput_av = 0;

        // loop all probes, calculate rtt and add them to array
        for (int i = 1; i <= probes; i++) {
            // keep timestamp
            long t1 = System.nanoTime();

            // generate and send the message
            String message = "m " + i + " " + payload;

            dos.writeUTF(message);

            // when received, get other timestamp
            // insert the difference
            String messageBack = dis.readUTF();
            double rtt_curr = (System.nanoTime() - t1)/1000000.0  + csm.getServerDelay();
            double tput_curr = message.length()*1000 / (rtt_curr*1024*1024);  // assuming 1 byte per character

            rttValues[i-1] = rtt_curr;
            tputValues[i-1] = tput_curr;

            if (i > SKIP_COUNT) { // skip first two
                if (i == SKIP_COUNT + 1) {
                    rtt_av = rtt_curr;
                    tput_av = tput_curr;
                } else {
                    rtt_av = 0.8*rtt_av + 0.2*rtt_curr;
                    tput_av = 0.8*tput_av + 0.2*tput_curr;
                }
            }
        }

        // display chartRtt
        List<Entry> entriesRtt = new ArrayList<Entry>();
        List<Entry> entriesTput = new ArrayList<Entry>();

        // calculate average
        // TCP is a slow-start algorithm, so instead of using arithmetic mean, I am using weighted moving average
        // rtt = 0.8 * rtt + 0.2 * new_rtt
        // This makes sure, the initial bursts do not contribute as much into the final rtt estimate

        // display the array values
        //output("The RTT values are:");
        for (int i = 0; i < probes; i++) {
            entriesRtt.add(new Entry((float)(i+1 + 0.0), (float)rttValues[i]));
            entriesTput.add(new Entry((float)(i+1 + 0.0), (float)tputValues[i]));
            //output(rttValues[i] + "ms, " + tputValues[i] + "MB/s");
        }
        output("WMA-RTT: " + rtt_av + "ms");
        output("WMA-TPUT: " + tput_av + "MB/s");

        LineDataSet dataSetRtt = new LineDataSet(entriesRtt, "RTT (ms)"); // add entriesRtt to dataset
        LineDataSet dataSetTput = new LineDataSet(entriesTput, "ThroughPut MB/s");
        dataSetRtt.setColor(Color.BLUE);
        dataSetTput.setColor(Color.GREEN);
        dataSetRtt.setValueTextColor(Color.RED);
        dataSetTput.setValueTextColor(Color.RED);

        LineData lineDataRtt = new LineData(dataSetRtt);
        LineData lineDataTput = new LineData(dataSetTput);

        ((Activity)context).runOnUiThread(() -> {
            chartRtt.setData(lineDataRtt);
            Description d = new Description();
            d.setText("");
            chartRtt.setDescription(d);
            chartTput.setDescription(d);
            chartTput.setData(lineDataTput);
            chartRtt.invalidate(); // refresh
            chartTput.invalidate();
            chartRtt.setVisibility(View.VISIBLE);
            chartTput.setVisibility(View.VISIBLE);
        });

        // Send termination message to server
        dos.writeUTF("t ");
        // Get reply from server
        st = dis.readUTF();
        closeConnection();
    }

    private void closeConnection() {
        try {
            if (dos != null) dos.close();
            if (sOut != null) sOut.close();
            if (s != null) s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void output(String s) {
        ((Activity)context).runOnUiThread(() -> tvResult.setText(tvResult.getText().toString() + "\n" + s));
    }
}