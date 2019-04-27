package Client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;

public class Settings implements Serializable {


    public int gridSize,
                squareSize,
                spawnChance,
                port;
    public String ipAddress, preDefs = "PREDEFS";
    public static final String settingsFilePath="settings.txt",
                    connectionFilePath="conn.txt",
                    preDefsFilePath="predefs.txt";
    public Settings(){

    }

    public void writePreDefToFile(String msg, String filePath) throws IOException{
        File file = new File(filePath);
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream writer = new FileOutputStream(filePath, true);
        writer.write((msg + "\n").getBytes());
        writer.close();
        System.out.println("Wrote predef to file!");
    }

    public void deletePreDefFromFile(String fileName, String filePath) throws IOException{
        String newFileContents = "";
        ArrayList<String> preDefList = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));

            String line;
            fileName = fileName+":";
            while ((line = reader.readLine()) != null) {
                if(!line.startsWith(fileName)){
                    preDefList.add(line);
                    System.out.println("lines that were cut!" + fileName);
                }
            }
            reader.close();
            PrintWriter pw = new PrintWriter(filePath);
            pw.close();
            FileOutputStream writer = new FileOutputStream(filePath, true);
            for(String predef : preDefList){
                writer.write((predef+"\n").getBytes());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        System.out.println("Saved settings to file!");

    }

    public String getGridSettings(){
        return this.gridSize+":"+this.squareSize+":"+this.spawnChance+this.preDefs;
    }

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


