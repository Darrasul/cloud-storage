package com.buzas.cloud.model;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class DownloadMessage extends AbstractMessage{

    private String name;
    private byte[] bytes;

    public DownloadMessage(Path path) throws IOException {
        name = path.getFileName().toString();
        bytes = Files.readAllBytes(path);
    }
    @Override
    public MessageType getMessageType() {
        return MessageType.DOWNLOAD;
    }
}
