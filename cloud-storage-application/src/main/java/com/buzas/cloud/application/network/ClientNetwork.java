package com.buzas.cloud.application.network;

// Решил проблему с package is declared in module which does not export it путем подключения core папки как рута сюда
import com.buzas.cloud.model.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class ClientNetwork {

    private final Socket socket;
    private final ObjectDecoderInputStream input;
    private final ObjectEncoderOutputStream output;

    private final String host;
    private final int port;

    public ClientNetwork(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        socket = new Socket(host, port);
        output = new ObjectEncoderOutputStream(socket.getOutputStream());
        input = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public ClientNetwork() throws IOException {
        host = "localhost";
        port = 8189;
        socket = new Socket(host, port);
        output = new ObjectEncoderOutputStream(socket.getOutputStream());
        input = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public AbstractMessage read() throws Exception {
        return (AbstractMessage) input.readObject();
    }

    public void write(AbstractMessage message) throws IOException {
        output.writeObject(message);
    }
}
