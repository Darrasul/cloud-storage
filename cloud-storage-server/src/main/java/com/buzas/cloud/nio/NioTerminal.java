package com.buzas.cloud.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class NioTerminal {
// Команды для реализации: перейти в деректорию, создать файл, вывести текст из файла, создать деректорию
    private Path directory;
    private final ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buffer = ByteBuffer.allocate(256);

    public NioTerminal() throws IOException {

        directory = Path.of("cloudFiles");

        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8189));
        serverChannel.configureBlocking(false);

        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server starts on port: 8189");

        while (serverChannel.isOpen()){
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            try {
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }

                    iterator.remove();
                }
            } catch (Exception e) {
                System.err.println("Lost connect to channel");
                e.printStackTrace();
            }
        }

    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel readableChannel = (SocketChannel) key.channel();

        String message = readMessageFromChannel(readableChannel).trim();
        System.out.println("Received: " + message);
        if (message.equals("ls")){
            readableChannel.write(
                ByteBuffer.wrap(getLsResultString().getBytes(StandardCharsets.UTF_8))
            );
        } else if (message.startsWith("mkdir ")) {
            String makeDir = message.substring(6);
            createNewDirectory(makeDir);
        } else if (message.startsWith("cd ")) {
            String newDir = message.substring(3);
            directory = Path.of(newDir);
        } else if (message.startsWith("cat ")) {
            String listenFile = message.substring(4);
            readableChannel.write(
                    ByteBuffer.wrap(readFileFilling(listenFile).getBytes(StandardCharsets.UTF_8))
            );
            readableChannel.write(ByteBuffer.wrap("\n\r".getBytes(StandardCharsets.UTF_8)));
        } else if (message.startsWith("touch ")) {
            String createFile = message.substring(6);
            createNewFile(createFile);
        } else {
            readableChannel.write(ByteBuffer.wrap("Unknown command \n\r".getBytes(StandardCharsets.UTF_8)));
        }

        readableChannel.write(ByteBuffer.wrap("-> ".getBytes(StandardCharsets.UTF_8)));
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel acceptedChannel = serverChannel.accept();

        acceptedChannel.configureBlocking(false);
        acceptedChannel.register(selector, SelectionKey.OP_READ);
        acceptedChannel.write(ByteBuffer.wrap("Welcome in NioTerminal! \n\r ->".getBytes(StandardCharsets.UTF_8)));
        System.out.println("Client accepted");
    }

    private String readMessageFromChannel(SocketChannel readableChannel) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (true){
            int readCount = readableChannel.read(buffer);

            if (readCount <= -1) {
                readableChannel.close();
                break;
            } else if (readCount == 0){
                break;
            }

            buffer.flip();

            while (buffer.hasRemaining()){
                stringBuilder.append((char) buffer.get());
            }

            buffer.clear();
        }

        return stringBuilder.toString();
    }

    private String getLsResultString() throws IOException {
        return Files.list(directory)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.joining("\n\r")) + "\n\r";
    }

    private String readFileFilling(String listenedFile) throws IOException {
        Path listenedPath = Path.of(directory.toString(), listenedFile);

        return Files.readString(listenedPath, StandardCharsets.UTF_8);
    }

    private void createNewDirectory(String dirName) throws IOException {
        Path newPath = Path.of(directory.toString(), dirName);

        if (!Files.exists(newPath)){
            Files.createDirectory(newPath);
        }
    }

    private void createNewFile(String createdFile) throws IOException {
        Path creationPath = Path.of(directory.toString(), createdFile);
        Files.createFile(creationPath);
    }

    public static void main(String[] args) {
        try {
            new NioTerminal();
        } catch (IOException e) {
            System.err.println("Failed to initialize Terminal");
            e.printStackTrace();
        }
    }
}
