package src;

import java.io.*;
import java.net.*;

public class Server {

	public static void main(String[] arg) {

		Grid grid = null;
		try {

			ServerSocket serverConnection = new ServerSocket(5000);

			System.out.println("Server Ready...\nWaiting For Incoming Connections...");

			Socket socket = serverConnection.accept();
			ObjectInputStream serverInputStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream serverOutputStream = new ObjectOutputStream(socket.getOutputStream());
			try{
				while(true){
					try{
						System.out.println("Connection Accepted!");


						grid = (Grid)serverInputStream.readObject();
						if(grid == null){
							break;
						}
						//client wants new squares
						//call function
						//Send them back to client

						serverOutputStream.writeObject(grid);


					} catch(Exception ex){
						socket.close();
					}
				}
			} catch(Exception ex){
				serverConnection.close();
			} finally {
				serverInputStream.close();
				serverOutputStream.close();
			}
 




		}  catch(Exception e) {System.out.println(e);
		}
	}

}