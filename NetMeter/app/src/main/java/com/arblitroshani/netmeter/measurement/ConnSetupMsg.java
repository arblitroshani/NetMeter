package com.arblitroshani.netmeter.measurement;

public class ConnSetupMsg {

    public static final String RTT  = "rtt";
    public static final String TPUT = "tput";

    private static final char DEFAULT_PROTOCOL_PHASE = 's';
    private static final int  DEFAULT_NUM_PROBES = 10;
    private static final int  DEFAULT_SERVER_DELAY = 0;

    private char protocolPhase;
    private String measurementType;
    private int numProbes;
    private int messageSize;
    private int serverDelay;

    public ConnSetupMsg(char protocolPhase, String measurementType, int numProbes, int messageSize, int serverDelay) {
        this.protocolPhase = protocolPhase;
        this.measurementType = measurementType;
        this.numProbes = numProbes;
        this.messageSize = messageSize;
        this.serverDelay = serverDelay;
    }

    // Use some default values
    public ConnSetupMsg(String measurementType, int messageSize) {
        this(   DEFAULT_PROTOCOL_PHASE,
                measurementType,
                DEFAULT_NUM_PROBES,
                messageSize,
                DEFAULT_SERVER_DELAY);
    }

    public ConnSetupMsg(){

    }

    public String generateMessage() {
        return protocolPhase + " " +
                measurementType + " " +
                numProbes + " " +
                messageSize + " " +
                serverDelay;
    }

    public static boolean isValidMessage(String s) {
        // Use 1 space character delimiter
        String[] elements = s.split("\\s");

        // first string = s
        if (!elements[0].equals("s")) return false;

        // second string = rtt or tput
        if (!elements[1].equals(RTT) && !elements[1].equals(TPUT)) return false;

        // number of probes is integer between 10 and 20
        if (!isPositiveInteger(elements[2])) return false;

        // message size is positive integer < 1000 for rtt, < 32 for tput
        if (isPositiveInteger(elements[3])) {
            int msgSize = Integer.parseInt(elements[3]);
            if (elements[1].equals(RTT) && msgSize > 1000) return false;
            if (elements[1].equals(TPUT) && msgSize > 32*1024)   return false;
        } else return false;

        // server delay is nonnegative integer
        return elements[4].matches("\\d+");
    }

    private static boolean isPositiveInteger(String a) {
        return a.matches("[1-9]\\d*") && Integer.parseInt(a) >= 10;
    }

    public static ConnSetupMsg parseMessage(String message) {
        if (!isValidMessage(message)) {
            System.out.println("Invalid message");
            return null;
        }
        String[] tokens = message.split("\\s");

        ConnSetupMsg csm = new ConnSetupMsg();
        csm.setProtocolPhase(tokens[0].charAt(0));
        csm.setMeasurementType(tokens[1]);
        csm.setNumProbes(Integer.parseInt(tokens[2]));
        csm.setMessageSize(Integer.parseInt(tokens[3]));
        csm.setServerDelay(Integer.parseInt(tokens[4]));
        return csm;
    }

    // Getters and Setters

    public char getProtocolPhase() {
        return protocolPhase;
    }

    public void setProtocolPhase(char protocolPhase) {
        this.protocolPhase = protocolPhase;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public int getNumProbes() {
        return numProbes;
    }

    public void setNumProbes(int numProbes) {
        this.numProbes = numProbes;
    }

    public int getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    public int getServerDelay() {
        return serverDelay;
    }

    public void setServerDelay(int serverDelay) {
        this.serverDelay = serverDelay;
    }
}