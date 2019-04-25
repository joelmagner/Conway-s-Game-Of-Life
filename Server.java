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
			ObjectOutputStream serverOutputStream = new ObjectOutputStream(socket.getOutputStream());

			try{
				System.out.println("Connection Accepted!");
				while(true){
					try{
						DataInputStream dIn = new DataInputStream(socket.getInputStream());
						boolean done = false;
						while(!done) {
							byte messageType = dIn.readByte();

							switch(messageType)
							{
								case 1: // Init grid
									String message = dIn.readUTF();
									System.out.println("Grid Settings: " + message);

									int gridSize = Integer.parseInt(message.split(":")[0]);
									int squareSize = Integer.parseInt(message.split(":")[1]);
									int spawnChance = Integer.parseInt(message.split(":")[2]);
									grid = new Grid(gridSize, squareSize, spawnChance);
									serverOutputStream.writeObject(grid);
									break;
								case 2: // maybe pause simulation or something similar
									System.out.println("Paused simulation: " + dIn.readUTF());
									break;
								case 3: // something
									System.out.println("blabla: " + dIn.readUTF());
									break;
								default:
									done = true;
							}
						}
						dIn.close();

						if(grid == null){
							break;
						}
					} catch(Exception ex){
						socket.close();
					}
				}
			} catch(Exception ex){
				serverConnection.close();
				System.out.println("Connection disconnected!");
			} finally {
				serverOutputStream.close();
				System.out.println("Connection closed!");
			}
 




		}  catch(Exception e) {System.out.println(e);
		}
	}

}