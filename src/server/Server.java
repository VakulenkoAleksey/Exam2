package server;


import connect.Connection;
import message.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;


public class Server implements Runnable {
    private final int PORT = 8090;
    private CopyOnWriteArraySet<Connection> connections = new CopyOnWriteArraySet<>();
    private BlockingQueue<Message> messages = new ArrayBlockingQueue<>(10, true);
    private ArrayList<Message> messagesHistory = new ArrayList<>();



    @Override
    public void run() {

        while (true) {
            try {
                Message message = messages.take();

                for (Connection connection : connections) {
                    if (connection.getSocket().getRemoteSocketAddress().equals(message.getSocketAddress()))
                        continue;
                    connection.sendMessage(message);
                }

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> {
                    try (Connection connection = new Connection(clientSocket)) {

                        connections.add(connection);
                        System.out.println("New connection, number of users " + connections.size());

                        while (true) {
                            try {
                                Message message = connection.readMessage();
                                message.setSocketAddress(connection.getSocket().getRemoteSocketAddress());
                                if (message.getText().equalsIgnoreCase("/quit")) {
                                    connections.remove(connection);
                                    messages.put(Message.getMessage(message.getSender() + " покинул(а) чат!"));
                                    System.out.println("User " + message.getSender() + " left chat, number of users "
                                            + connections.size());
                                    break;
                                }
                                messages.put(message);
                                messagesHistory.add(message);
                                System.out.println(message);
                            } catch (SocketException e) {

                                connection.getSocket().getRemoteSocketAddress();
                                connections.remove(connection);
                                String name = "";
                                for (Message ms: messagesHistory) {
                                    if (connection.getSocket().getRemoteSocketAddress().equals(ms.getSocketAddress())){
                                        name = " " + ms.getSender();
                                        break;
                                    }
                                }
                                System.out.println("User" + name + " disconnected from server, number of users "
                                        + connections.size());
                                messages.put(Message.getMessage("User" + name + " is disconnected from server"));
                                break;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }).start();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        new Thread(server).start();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

