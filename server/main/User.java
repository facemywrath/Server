package server.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class User extends Thread {

	protected Socket socket;
	private Server server;


	public User(Server server, Socket clientSocket) {
		this.socket = clientSocket;
		this.server = server;
		this.id = "Client " + (server.getConnectedUsers().size()+1);
	}

	String id;

	public DataOutputStream out = null;
	public void run() {
		InputStream inp = null;
		DataInputStream brinp = null;
		try {
			inp = socket.getInputStream();
			brinp = new DataInputStream(new BufferedInputStream(inp));
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			return;
		}
		broadcast(id + " connected.");
		String line;
		main:
		while (true) {
			try {
				line = brinp.readUTF();
				if ((line == null) || line.equalsIgnoreCase("QUIT")) {
					socket.close();
					return;
				} else {
					if(line.startsWith("/nick")) {
						System.out.println(id + " attempted command: " + line);
						String temp = line.substring(line.indexOf(" ")+1, line.length());
						for(User user : server.getConnectedUsers()) {
							if(user.id.equalsIgnoreCase(temp)) {
								message(this, "[SERVER] Username already taken");
								continue main;
							}
						}
						if(!line.contains(" ")) {
							message(this, "[SERVER] Type /nick (username) to change your username.");
							continue;
						}
						if(line.length()-5 > 16) {
							message(this, "[SERVER] Nickname too long!");
							continue;
						}
						this.id = temp;
						message(this, "[SERVER] Username changed to " + id);
						continue;
					}
					System.out.println(id + ": " + line);
					for(User user : server.getConnectedUsers()){
						if(user != this) {
							message(user, id + ": " + line);
						}
					};
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public void message(String userName, String message) {
		for(User user : server.getConnectedUsers()) {
			if(user.id.equalsIgnoreCase(userName)) {
				try {
					user.out.writeUTF(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}	
	public void message(User user, String message) {
		try {
			user.out.writeUTF(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void broadcast(String str) {
		for(User user : server.getConnectedUsers()){
			try {
				user.out.writeUTF(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}