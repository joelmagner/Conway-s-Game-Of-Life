package src;

import javafx.scene.layout.Pane;

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
				DataInputStream dIn = new DataInputStream(socket.getInputStream());
				Render render = new Render();

				while(true){
					try{

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

								grid = render.round(grid);
								serverOutputStream.writeObject(grid);

								break;
							case 2: // maybe pause simulation or something similar
								System.out.println("next round: " + dIn.readUTF());
								grid = render.round(grid);
								serverOutputStream.writeObject(grid);

								break;
							case 3: // something
								System.out.println("blabla: " + dIn.readUTF());
								break;
							default:
								break;
						}
					} catch(Exception ex){
						dIn.close();
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