package client;


import connect.Connection;
import message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    private String name;
    private final String address;
    private final int port;
    private boolean aBoolean;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Client(int port, String address) throws IOException {
        this.port = port;
        this.address = address;
    }

    private Socket getSocket() throws IOException {
        return new Socket(address, port);
    }

    private void setName() throws IOException {
        System.out.println("Введите имя:");
        String str = reader.readLine();
        if (str.matches("[а-яА-Яa-zA-z]+")) {
            this.name = str;
        } else {
            System.out.println("Введено некоректное имя!");
            setName();
        }
    }

    public String getName() {
        return name;
    }

    public void start() throws Exception {
        setName();
        try (Connection connection = new Connection(getSocket())) {
            System.out.println("Подключиение к серверу успешно");

            System.out.println("Для выхода из чата введите команду \"/quit\"");

            new Thread(() -> {
                System.out.println("Введите сообщение:");
                while (true) {
                    try {
                        String text = reader.readLine();
                        connection.sendMessage(Message.getMessage(getName(), text));
                        if (text.equalsIgnoreCase("/quit")) break;
                    } catch (IOException e) {
                        System.out.println("Server connection disconnected");
                        break;
                    }
                }
            }
            ).start();


            while (true) {
                try {
                    if (!connection.getSocket().isConnected()) {
                        break;
                    }
                    try {
                        Message message = connection.readMessage();
                        System.out.println(message);

                    } catch (IOException e) {
                        break;
                    }


                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static void main(String[] args) {
        int port = 8090;
        String address = "localhost";
        try {
            Client client = new Client(port, address);
            client.start();
        } catch (Exception e) {
            System.out.println("Server is not available!");
        }
    }
}

