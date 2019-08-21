package Client;
import java.io.*;
import java.util.ArrayList;

/**
 * @author Joel Magnér
 * <p>
 *     Main purpose is dealing with volatility.
 *     Allows user to save and read their settings to or from disk.
 * </p>
 */

public class Settings implements Serializable {


    public int  gridSize,
                squareSize,
                spawnChance,
                port;
    public String ipAddress;
    public static final String settingsFilePath = "assets/settings.txt",
                    connectionFilePath = "assets/conn.txt",
                    preDefs = "PREDEFS",
                    preDefsFilePath = "assets/predefs.txt";
    public Settings(){

    }

    /**
     *
     * @param msg String - message to be saved.
     * @param filePath String - location of file.
     * @throws IOException
     * <p>
     *     Saves user inputted predefined values for the grid to a file.
     * </p>
     */

    public void writePreDefToFile(String msg, String filePath) throws IOException{
        File file = new File(filePath);
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream writer = new FileOutputStream(filePath, true);
        writer.write((msg + "\n").getBytes());
        writer.close();
    }

    /**
     *
     * @param fileName String - name of file.
     * @param filePath String - path to file.
     * <p>
     *     User deletes an entry in the predefine file.
     * </p>
     */

    public void deletePreDefFromFile(String fileName, String filePath){
        ArrayList<String> preDefList = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));

            String line;
            fileName = fileName+":";
            while ((line = reader.readLine()) != null) {
                if(!line.startsWith(fileName)){
                    preDefList.add(line);
                }
            }
            reader.close();
            PrintWriter pw = new PrintWriter(filePath);
            pw.close();
            FileOutputStream writer = new FileOutputStream(filePath, true);
            for(String preDef : preDefList){
                writer.write((preDef+"\n").getBytes());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param filePath String - path to file
     * @return preDefList
     *
     * <p>
     *     Reads from the predefined file.
     * </p>
     */

    public ArrayList<String> readPreDefFromFile(String filePath){
        ArrayList<String> preDefList = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                preDefList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return preDefList;
    }

    /**
     *
     * @param msg String - message to be saved.
     * @param filepath String - path to file.
     * @throws IOException
     * <p>
     *     Writes user settings to a file.
     * </p>
     */

    public void writeSettingsToFile(String[] msg, String filepath) throws IOException{

        File file = new File(filepath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream writer = new FileOutputStream(filepath);
        for(String row : msg){
            writer.write((row + "\n").getBytes());
        }
        writer.close();
    }

    /**
     * @return grid settings
     * <p>
     *     The grid format separated by ":".
     *     eg. 10:20:50:PREDEFS1x3 1x4 1x5
     * </p>
     */

    public String getGridSettings(){
        return this.gridSize+":"+this.squareSize+":"+this.spawnChance+this.preDefs;
    }

    /**
     *
     * @param filepath - path to settings file
     * <p>
     *      parses the settings file
     * </p>
     */

    public void readSettingsFromFile(String filepath){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filepath));
            String line = reader.readLine();
            while (line != null) {
                int valueN = 0;
                String valueS ="";
                try{
                    valueN = Integer.parseInt(line.split(":")[1]);
                }catch(NumberFormatException ex) {
                    valueS = line.split(":")[1];
                }

                if(line.startsWith("gridsize:")){
                    this.gridSize = valueN;
                }
                if(line.startsWith("squaresize:")){
                    this.squareSize = valueN;
                }
                if(line.startsWith("spawnchance:")){
                    this.spawnChance = valueN;
                }
                if(line.startsWith("ipaddress:")){
                    this.ipAddress = valueS;
                }
                if(line.startsWith("port:")){
                    this.port = valueN;
                }

                System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            switch(filepath) {
                case settingsFilePath:
                    this.gridSize = 20;
                    this.squareSize = 20;
                    this.spawnChance = 15;
                    break;
                case connectionFilePath:
                    this.ipAddress = "127.0.0.1";
                    this.port = 5000;
                    break;
            }
            System.out.println("No file was found - setting default values");
        }
    }
}


