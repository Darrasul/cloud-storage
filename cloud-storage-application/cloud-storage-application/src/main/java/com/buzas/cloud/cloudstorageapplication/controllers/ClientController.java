package com.buzas.cloud.cloudstorageapplication.controllers;

import com.buzas.cloud.cloudstorageapplication.network.ClientNetwork;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    public ListView<String> leftNameplate;
    public ListView<String> rightNameplate;
    private ClientNetwork clientNetwork;
    public ListView<String> userView;
    public ListView<String> serverView;

    private void readCommands() {
        try {
            signUpNameplates();
            while (true){
                String command = clientNetwork.readUTF();
                if (command.equals("/userList")) {
                    readUserFilesNames();
                } else if (command.equals("/serverList")) {
                    readServerFilesNames();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read command");
            e.printStackTrace();
        }
    }

    private void readUserFilesNames(){
        try {
            userView.getItems().clear();
            Long filesCount = clientNetwork.readLong();
            for (int i = 0; i < filesCount; i++) {
                String fileName = clientNetwork.readUTF();
                userView.getItems().addAll(fileName);
            }
        } catch (IOException e) {
            System.err.println("Failed to read files in repository");
            e.printStackTrace();
        }

    }

    private void readServerFilesNames(){
        try {
            serverView.getItems().clear();
            Long filesCount = clientNetwork.readLong();
            for (int i = 0; i < filesCount; i++) {
                String fileName = clientNetwork.readUTF();
                serverView.getItems().addAll(fileName);
            }
        } catch (IOException e) {
            System.err.println("Failed to read files in cloud");
            e.printStackTrace();
        }

    }

    // Метод ниже в последствии может быть переработан. В текущем виде он просто подписывает где чьи файлы
    // Если появится возможность, к примеру, добавлять папки в облаке или синхронизироваться с другим пользователем,
    // то можно будет переписать часть после второго clear,
    // чтобы показывать ники пользователей или названия открытых папок в облаке

    private void signUpNameplates() {
        leftNameplate.getItems().clear();
        rightNameplate.getItems().clear();
        leftNameplate.getItems().add("User");
        rightNameplate.getItems().add("Server");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            clientNetwork = new ClientNetwork();
            Thread commandReadThread = new Thread(this::readCommands);
            commandReadThread.setDaemon(true);
            commandReadThread.start();
        } catch (IOException e) {
            System.err.println("Failed to launch Client Network");
            e.printStackTrace();
        }
    }

    public void fromUser(ActionEvent actionEvent) throws NullPointerException, InterruptedException {
        File serverDirectory = new File("cloudFiles");
        File userDirectory = new File("userFiles");

        String[] userFiles = userDirectory.list();
        System.out.println(userFiles.length + "files affected during export attempt");
        for (String userFile : userFiles) {
            Thread thread = new Thread(() -> {
                File oldFile = new File(userDirectory, userFile);
                if (oldFile.exists()) {
                    File newFile = new File(serverDirectory, userFile);
                    if (!newFile.exists()){
                        try {
                            FileWriter writer = new FileWriter(newFile);
                            FileReader reader = new FileReader(oldFile);
                            reader.skip(0);
                            reader.transferTo(writer);
                            reader.close();
                            writer.close();
                        } catch (IOException e) {
                            System.err.println("Failed to copy data to cloud");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.printf("File %s already exists \n", newFile.getName());
                    }
                }
            });
            thread.start();

//          Внизу аналогичная ситуация. Я пытался просто использовать readUserFilesNames() и аналог чуть ниже после
//           прогона всех добавленных значений, но это по какой-то причине приводило к завизанию клиента(хотя
//           процедуру оно выполняло)

            File newFile = new File(serverDirectory, userFile);
            if (!newFile.exists()){
                serverView.getItems().addAll(userFile);
            }
        }
    }

    public void fromServer(ActionEvent actionEvent) throws NullPointerException, InterruptedException {
        File userDirectory = new File("userFiles");
        File serverDirectory = new File("cloudFiles");

        String[] serverFiles = serverDirectory.list();
        System.out.println(serverFiles.length + "files affected during import attempt");
        for (String serverFile : serverFiles) {
            Thread thread = new Thread(() -> {
                File oldFile = new File(serverDirectory, serverFile);
                if (oldFile.exists()) {
                    File newFile = new File(userDirectory, serverFile);
                    if (!newFile.exists()) {
                        try {
                            FileWriter writer = new FileWriter(newFile);
                            FileReader reader = new FileReader(oldFile);
                            reader.skip(0);
                            reader.transferTo(writer);
                            reader.close();
                            writer.close();
                        } catch (IOException e) {
                            System.err.println("Failed to copy data from cloud");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.printf("File %s already exists \n", newFile.getName());
                    }
                } });
            thread.start();
            File newFile = new File(userDirectory, serverFile);
            if (!newFile.exists()){
                userView.getItems().addAll(serverFile);
            }
        }
    }

    //Использовал для теста функционала. Нашел проблему: reader.skip(0) считывает всё, но reader.read() - пропускает
    // первый символ. Если вам не сложно, не могли бы вы объяснить, как правильно использовать read() в таком случае
    // без потери данных
//    public static void main(String[] args) throws IOException {
//        File serverDirectory = new File("cloudFiles");
//        File serverFile = new File(serverDirectory, "testFileA");
//        File newFile = new File(serverDirectory, "testFileD");
//
//        FileReader reader = new FileReader(serverFile);
//        FileWriter writer = new FileWriter(newFile);
//
//        reader.skip(0);
//        reader.transferTo(writer);
//        reader.close();
//        writer.close();
//
//    }
}
