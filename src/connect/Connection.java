package connect;

import message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Connection implements AutoCloseable {
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        output = new ObjectOutputStream(this.socket.getOutputStream());
        input = new ObjectInputStream(this.socket.getInputStream());
    }

    public void sendMessage(Message message) throws IOException {
        message.setDateTime();
        output.writeObject(message);
        output.flush();
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
            return (Message) input.readObject();
    }

    public Socket getSocket(){
        return socket;
    }

    @Override
    public void close() throws Exception {
        input.close();
        output.close();
        socket.close();
    }
}