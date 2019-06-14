package server.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	
	private List<User> users = new ArrayList<>();

    @SuppressWarnings("resource")
	public Server(int port) {

        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();

        }
        System.out.println("Waiting for connection...");
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            User user = new User(this, socket);
            user.start();
            users.add(user);
        }
    }

    public static void main(String args[]) {
    	new Server(1996);
    }
    
    public List<User> getConnectedUsers() {
    	return this.users;
    }
}