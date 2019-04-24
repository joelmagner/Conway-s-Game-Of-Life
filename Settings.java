package src;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Settings {


    public int gridSize,
                squareSize,
                spawnChance;

    public Settings(){

    }

    public void writeSettingsToFile(String[] msg) throws IOException {
        String filename = "settings.txt";
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream writer = new FileOutputStream(filename);
            for(String row : msg){
                writer.write((row + "\n").getBytes());
            }
            writer.close();
            System.out.println("Wrote new settings to file!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readSettingsFromFile(){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "settings.txt"));
            String line = reader.readLine();
            while (line != null) {
                if(line.startsWith("gridsize:")){
                    this.gridSize = Integer.parseInt(line.split(":")[1]);
                }
                if(line.startsWith("squaresize:")){
                    this.squareSize = Integer.parseInt(line.split(":")[1]);
                }
                if(line.startsWith("spawnchance:")){
                    this.spawnChance = Integer.parseInt(line.split(":")[1]);
                }
                System.out.println(line);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


