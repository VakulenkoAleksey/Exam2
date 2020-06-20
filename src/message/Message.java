package message;


import java.io.Serializable;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Message implements Serializable {
    private String sender;
    private String text;
    private LocalDateTime time;
    private SocketAddress socketAddress;

    public Message(String text) {
        this.text = text;
    }

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }



    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public void setDateTime() {
        time =  LocalDateTime.now();
    }

    public LocalDateTime getTime() {
        return time;
    }

    public static Message getMessage(String sender, String text){
        return new Message(sender, text);
    }

    public static Message getMessage(String text){
        return new Message(text);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm");
        if (sender == null){
            return time.format(formatter) + "\n" +
                    text;
        }else {
            return time.format(formatter) + "\n" +
                    sender + ":\n" +
                    text;
        }
    }
}

