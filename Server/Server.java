package Server;
import java.io.*;
import java.net.*;


/**
 * @author Joel Magnér
 * <p>
 *     The server.
 *     Responsible for opening, maintaining and allowing connections.
 * </p>
 */

public class Server {
	public static void main(String[] arg) throws IOException {
		ServerSocket serverConnection = null;
		try {

			serverConnection = new ServerSocket(5000);
			Socket socket;
			System.out.println("Server Ready...\nWaiting For Incoming Connections...");
			while(true){
				socket = serverConnection.accept();
				new MessageService(socket);
			}
		} catch(IOException ex) {
			serverConnection.close();
			ex.printStackTrace();
		}
	}


}