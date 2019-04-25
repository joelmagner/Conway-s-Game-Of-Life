package Server;
import Common.Grid;
import Common.Square;

import java.io.*;
import java.net.*;

public class Server {
	static Grid grid = null;
	public static void main(String[] arg) {

		try {

			ServerSocket serverConnection = new ServerSocket(5000);

			System.out.println("Server.Server Ready...\nWaiting For Incoming Connections...");

			Socket socket = serverConnection.accept();
			ObjectOutputStream serverOutputStream = new ObjectOutputStream(socket.getOutputStream());

			try{
				System.out.println("Connection Accepted!");
				DataInputStream dIn = new DataInputStream(socket.getInputStream());
				GameManager gm = new GameManager();

				while(true){
					try{

						byte messageType = dIn.readByte();

						switch(messageType)
						{
							case 1: // Init grid
								serverOutputStream.reset();
								String message = dIn.readUTF();
								System.out.println("Grid Settings: " + message);

								int gridSize 	= Integer.parseInt(message.split(":")[0]),
									squareSize 	= Integer.parseInt(message.split(":")[1]),
									spawnChance = Integer.parseInt(message.split(":")[2]);

								grid = new Grid(gridSize, squareSize, spawnChance);
								grid = gm.round(grid);
								serverOutputStream.writeObject(grid);
								break;
							case 2: // next round
								serverOutputStream.reset();
								System.out.println("next round: " + dIn.readUTF());

								grid = gm.round(grid);
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