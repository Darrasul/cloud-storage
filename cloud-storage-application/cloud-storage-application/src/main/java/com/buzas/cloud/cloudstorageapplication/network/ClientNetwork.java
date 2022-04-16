package com.buzas.cloud.cloudstorageapplication.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientNetwork {

    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;

    private final String host;
    private final int port;

    public ClientNetwork(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        socket = new Socket(host, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public ClientNetwork() throws IOException {
        host = "localhost";
        port = 8189;
        socket = new Socket(host, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public Long readLong() throws IOException {
        return input.readLong();
    }

    public String readUTF() throws IOException {
        return input.readUTF();
    }
}
