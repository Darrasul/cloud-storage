package com.buzas.cloud.model;

public class ErrorMessage extends AbstractMessage{
    @Override
    public MessageType getMessageType() {
        return MessageType.ERROR;
    }
}
