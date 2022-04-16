package com.buzas.cloud;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class FileMessageHandler implements Runnable{
    private final DataInputStream input;
    private final DataOutputStream output;

    public FileMessageHandler(Socket socket) throws IOException {
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client accepted");
        searchForCloudFiles(output);
        searchForUserFiles(output);
    }

    private void searchForCloudFiles(DataOutputStream output) throws IOException {
        File directory = new File("cloudFiles");
        String[] files = directory.list();
        output.writeUTF("/serverList");
        output.writeLong(files.length);
        for (String file : files) {
            output.writeUTF(file);
        }
    }

    private void searchForUserFiles(DataOutputStream output) throws IOException {
        File directory = new File("userFiles");
        String[] files = directory.list();
        output.writeUTF("/userList");
        output.writeLong(files.length);
        for (String file : files) {
            output.writeUTF(file);
        }
    }

    @Override
    public void run() {
        try {
            while (true){
            input.readUTF();
            }
        } catch (IOException e) {
            System.err.println("Failed to start or keep reading UTF at run command");
            e.printStackTrace();
        }
    }
}
