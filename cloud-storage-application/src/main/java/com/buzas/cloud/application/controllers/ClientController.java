package com.buzas.cloud.application.controllers;

import com.buzas.cloud.application.network.ClientNetwork;
import com.buzas.cloud.model.AbstractMessage;
import com.buzas.cloud.model.FileMessage;
import com.buzas.cloud.model.ListMessage;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    private final Path serverDirectory = Path.of("cloudFiles");
    private Path clientDirectory;
    public ListView<String> leftNameplate;
    public ListView<String> rightNameplate;
    private ClientNetwork clientNetwork;
    public ListView<String> userView;
    public ListView<String> serverView;

    private void readCommands() {
        try {
            while (true){
                AbstractMessage message = clientNetwork.read();
                if (message instanceof ListMessage listMessage){
                    serverView.getItems().clear();
                    serverView.getItems().addAll(listMessage.getFiles());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to read command");
            e.printStackTrace();
        }
    }

    private List<String> readUserFilesNames() throws IOException {
            return Files.list(clientDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
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
            signUpNameplates();
            clientDirectory = Path.of("userFiles");
            clientNetwork = new ClientNetwork();
            userView.getItems().clear();
            userView.getItems().addAll(readUserFilesNames());
            Thread.sleep(300);
            Thread commandReadThread = new Thread(this::readCommands);
            commandReadThread.setDaemon(true);
            commandReadThread.start();
        } catch (IOException e) {
            System.err.println("Failed to launch Client Network");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Client was interrupted");
            e.printStackTrace();
        }
    }

    public void fromUser(ActionEvent actionEvent) throws Exception {
        String fileName = userView.getSelectionModel().getSelectedItem();
        Path serverPath = serverDirectory.resolve(fileName);
        if (Files.exists(serverPath)){
            System.out.println("File at path: " + serverPath + " replaced with a newer version");
        }
        clientNetwork.write(new FileMessage(clientDirectory.resolve(fileName)));
    }

    public void fromServer(ActionEvent actionEvent) throws Exception {
        String serverFile = serverView.getSelectionModel().getSelectedItem();
        Path userPath = clientDirectory.resolve(serverFile);
        if (Files.exists(userPath)){
            System.out.println("File at path: " + userPath + " replaced with a stable version");
        }
        Files.write(userPath, clientNetwork.download(new FileMessage(serverDirectory.resolve(serverFile))));
        userView.getItems().clear();
        userView.getItems().addAll(readUserFilesNames());
    }
}
