package Server;
import Common.Grid;
import Common.Square;

import java.awt.*;
import java.io.*;
import java.net.*;

public class Server {
	static Grid grid = null;
	public static void main(String[] arg) throws IOException{
		ServerSocket serverConnection = null;
		try {

			 serverConnection = new ServerSocket(5000);

			System.out.println("Server.Server Ready...\nWaiting For Incoming Connections...");
			Socket socket = null;
			while(true){
				socket = serverConnection.accept();
				performTask(socket);
			}

		} catch(IOException ex) {

			serverConnection.close();
			ex.printStackTrace();
		}
	}

	private static void performTask(Socket socket) throws IOException{

		ObjectOutputStream serverOutputStream = new ObjectOutputStream(socket.getOutputStream());
		try{
			System.out.println("Connection Accepted!");
			DataInputStream dIn = new DataInputStream(socket.getInputStream());
			GameManager gm = new GameManager();

			while(true){
				try {
					byte messageType = dIn.readByte();
					switch (messageType) {
						case 1: // Init grid
							serverOutputStream.reset();
							String message = dIn.readUTF();
							System.out.println("Grid Settings: " + message);
							int gridSize, squareSize, spawnChance;

							String gridInfo = message.split("PREDEFS")[0];
							gridSize = Integer.parseInt(gridInfo.split(":")[0]);
							squareSize = Integer.parseInt(gridInfo.split(":")[1]);
							spawnChance = Integer.parseInt(gridInfo.split(":")[2]);
							String preDefs = message.split("PREDEFS")[1];

							grid = new Grid(gridSize, squareSize, spawnChance);
							grid = gm.round(grid);

							if(preDefs.length() > 1){
								for (String xy : preDefs.split(" ")) {
									int x = 0, y = 0, newPre = 0;
									for (String v : xy.split("x")) {
										newPre++;
										switch (newPre) {
											case 1:
												x = Integer.parseInt(v);
												break;
											case 2:
												y = Integer.parseInt(v);
												for (Square s : grid.getGrid()) {
													if (s.getSquareX() == x && s.getSquareY() == y) {
														s.setSquareFill("#DDDDDD");
														s.setSquareStatus(true);
														grid.setGrid(grid.grid);
													}
												}
												newPre = 0;
												break;
											default:
												break;
										}
									}
								}
							}
							serverOutputStream.writeObject(grid);
							break;
						case 2: // next round
							serverOutputStream.reset();
							System.out.println("next round: " + dIn.readUTF());

							grid = gm.round(grid);
							serverOutputStream.writeObject(grid);

							break;
						case 3: // next X rounds
							serverOutputStream.reset();
							int rounds = Integer.parseInt(dIn.readUTF());
							System.out.println("Rounds : "+ rounds);
							for(int i=0; i< rounds; i++){
								grid = gm.round(grid);
							}
							serverOutputStream.writeObject(grid);
							break;
						default:
							break;
					}
				} catch(Exception ex){
					dIn.close();
					socket.close();
					System.out.println("Connection lost");
					break;
				}
			}
		} catch(Exception ex){
			System.out.println("Connection disconnected!");
		} finally {
			serverOutputStream.close();
			System.out.println("Connection closed!");
		}

	}

}