package com.buzas.cloud;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    public static void main(String[] args){
        try {
            ServerSocket server = new ServerSocket(8189);
            System.out.println("Server online");
            while (true){
                Socket serverSocket = server.accept();
                new Thread(new FileMessageHandler(serverSocket)).start();
            }

        } catch (IOException e){
            System.err.println("Failed to start server");
            e.printStackTrace();
        }
    }
}
