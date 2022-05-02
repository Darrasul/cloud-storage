package com.buzas.cloud.application.network;

// Решил проблему с package' is declared in module which does not export it 'путем подключения core папки как рута сюда
// Все остальные решения не особо помогли.
// Проблема изначально в строении проекта: при определенных действиях(в нашем случае при добавлении зависимости двух модулей)
// возникает проблема взаимодействия модулей с module-info.java и модулями без них. Простое добавление этих файлов с прописанием
// не помогает. Это возникает из-за отсутствия нужных параметров в пользователи/.../.m2/settings.xml, которые нужно в таком случае
// самостоятельно задать. Как правильно добавлять файлы в .m2/settings, увы, после Гугла осталось для меня загадкой, потому
// выбранный мной костыль остался единственным способом.
import com.buzas.cloud.model.AbstractMessage;
import com.buzas.cloud.model.FileMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public void closeNetwork() throws IOException {
        this.socket.close();
        System.out.println("Client disconnected");
    }

    public AbstractMessage read() throws Exception {
        return (AbstractMessage) input.readObject();
    }

    public void write(AbstractMessage message) throws IOException {
        System.out.println("writing message: " + message);
        output.writeObject(message);
    }

    public void download(AbstractMessage message) throws IOException {
        System.out.println("receiving message: " + message);
        output.writeObject(message);
    }
}
