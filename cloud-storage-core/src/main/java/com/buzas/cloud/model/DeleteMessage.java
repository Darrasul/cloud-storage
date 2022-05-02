package com.buzas.cloud.model;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class DeleteMessage extends AbstractMessage{

    private String name;

    public DeleteMessage(Path path) {
        name = path.getFileName().toString();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DELETE;
    }
}
