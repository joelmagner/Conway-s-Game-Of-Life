package Server;
import Common.Grid;
import Common.Square;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.stream.IntStream;


/**
 * @author Joel Magnér
 * MessageService
 * <p>
 *     Responsible for receiving and transmitting data from and to the client.
 * </p>
 */


public class MessageService {

    Socket socket;
    DataInputStream dataInputStream;
    ObjectOutputStream serverOutputStream;
    GameManager gm;
    Grid grid       = null,
         initGrid   = null;

    /**
     *
     * @param socket Socket
     * @throws IOException
     * <p>
     *     Responsible for setting up all the streams and get the game instance up and running.
     * </p>
     */

    public MessageService(Socket socket) throws IOException{
        this.socket = socket;
        if(this.setupStreams() == false){
            return;
        }
        this.gm = new GameManager();
        this.relayMessage(this.dataInputStream);
    }

    /**
     * <p>
     *     Sets up the streams
     * </p>
     * @return boolean
     */
    private boolean setupStreams(){
        try{
            this.serverOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.dataInputStream = new DataInputStream(this.socket.getInputStream());
        }catch(IOException e){
            System.out.println("Failed to connect!");
            return false;
        }
        System.out.println("Connection Accepted!");
        return true;
    }

    /**
     *
     * @param dIn DataInputStream
     * @throws IOException
     * <p>
     *     Responsible for relaying an incoming message to the right function.
     * </p>
     */

    private void relayMessage(DataInputStream dIn) throws IOException{
        try{
            while(true){
                try{
                    byte messageType = dIn.readByte();
                    String data = dIn.readUTF();
                    this.serverOutputStream.reset();
                    switch(messageType) {
                        case 1:
                            this.initGrid(data);
                            break;
                        case 2:
                            this.nextRound(data);
                            break;
                        case 3:
                            this.nextXRounds(data);
                            break;
                        case 4:
                            this.restoreGrid();
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    dIn.close();
                    this.socket.close();
                    System.out.println("Connection lost");
                    break;
                }
            }
        }catch(Exception ex){
            System.out.println("Connection disconnected!");
        } finally {
            this.serverOutputStream.close();
            System.out.println("Connection closed!");
        }
    }

    /**
     *
     * @param data contains the data that the client sent
     * @throws IOException
     * <p>
     *     Initializes the clients grid.
     * </p>
     */

    private void initGrid(String data) throws IOException{
        System.out.println("Grid Settings: " + data);
        int gridSize, squareSize, spawnChance;

        String gridInfo = data.split("PREDEFS")[0];
        gridSize = Integer.parseInt(gridInfo.split(":")[0]);
        squareSize = Integer.parseInt(gridInfo.split(":")[1]);
        spawnChance = Integer.parseInt(gridInfo.split(":")[2]);
        this.grid = new Grid(gridSize, squareSize, spawnChance);
        String preDefs = "";
        try {
            preDefs = data.split("PREDEFS")[1];
        } catch(Exception ex){
            /*dont do anything */
        }
        this.addPreDefsToGrid(preDefs);
        System.out.println("Sending: grid");
        this.serverOutputStream.writeObject(this.grid);
        this.initGrid = grid;
    }

    /**
     *
     * @param data contains the data that the client sent
     * @throws IOException
     * <p>
     *     Calling the gamemanager and sends the results to the client.
     * </p>
     */

    private void nextRound(String data) throws IOException {
        this.grid = this.gm.round(this.grid);
        this.addPreDefsToGrid(data);
        this.serverOutputStream.writeObject(this.grid);
    }

    /**
     *
     * @param data contains the data that the client sent
     * @throws IOException
     * <p>
     *     Runs X amount of rounds where X is the amount of rounds the client wants to skip.
     *     Sends the results back to the client.
     * </p>
     */

    private void nextXRounds(String data) throws IOException {
        int rounds = Integer.parseInt(data);
        System.out.println("Rounds : "+ rounds);
        IntStream.range(0,rounds).forEach(round -> this.grid = this.gm.round(this.grid));
        this.serverOutputStream.writeObject(this.grid);
    }


    /**
     *
     * @throws IOException
     * <p>
     *     Restores the state to the initial grid that the client requested.
     * </p>
     */

    private void restoreGrid() throws IOException{
        this.grid = this.initGrid;
        this.serverOutputStream.writeObject(this.grid);
    }

    /**
     *
     * @param data contains the data that the client sent
     * <p>
     *     Parses the incoming predef part of the message.
     *     Will then go ahead and place these squares on the grid.
     *     For visibility, it will color these manually added squares as grey.
     * </p>
     */

    private void addPreDefsToGrid(String data){
        if(data.length() > 1){
            for (String xy : data.split(" ")) {
                int x = 0, y = 0, newPre = 0;
                for (String v : xy.split("x")) {
                    newPre++;
                    switch (newPre) {
                        case 1:
                            x = Integer.parseInt(v);
                            break;
                        case 2:
                            y = Integer.parseInt(v);
                            for (Square s : this.grid.getGrid()) {
                                if (s.getSquareX() == x && s.getSquareY() == y) {
                                    s.setSquareFill("#dddddd");
                                    s.setSquareStatus(true);
                                    this.grid.setGrid(this.grid.grid);
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
    }
}
