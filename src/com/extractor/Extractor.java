package com.extractor;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class
 *
 * @author Pacito2
 * @since 1.0
 */
public class Extractor {
    public static void main(String[] args) {
        //Calling the export method
        export();
    }

    /**
     * Extract of a 3D model .java, the body, a steering wheel and a wheel
     * Extract the models in new "body", "steering" and "wheel" folders
     */
    public static void export(){
        //Get the .java file to process in the ./import folder
        ArrayList<File> models = getModels(new File("./import"));
        ArrayList<File> textures = getTextures(new File("./import"));
        String[][] categories = {{"body", "body"}, {"steering", "steeringWheelModel"}, {"wheel", "leftFrontWheelModel"}};

        //Process all files in the folder
        for(File file : models){
            for(String[] category : categories){

                //Create export folders
                String exportPath = "./export/";
                try {
                    String newPath = exportPath + category[0];
                    if(!Files.exists(Paths.get(newPath)))
                        Files.createDirectories(Paths.get(newPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Setting up file names to export
                String modelName = file.getName().replaceAll("(?i).java", "");
                if(modelName.startsWith("Model"))
                    modelName = modelName.substring(5);

                //Writing model .java files
                try {
                    Writer writer = new OutputStreamWriter(new FileOutputStream(exportPath + category[0] + "/" + modelName + ".java"), StandardCharsets.UTF_8);
                    Scanner reader = new Scanner(file);

                    //Implementation of UTF8-BOM encoding
                    writer.write('\ufeff');

                    writer.write("public class " + modelName + "\n");

                    //Writing relevant lines and replacement of function names
                    while(reader.hasNextLine()){
                        String data = reader.nextLine();
                        if(data.contains(category[1]) && !data.toLowerCase().contains("open"))
                            writer.write(data
                                    .replaceAll("func_78793_a", "setRotationPoint")
                                    .replaceAll("field_78795_f", "rotateAngleX")
                                    .replaceAll("field_78808_h", "rotateAngleZ")
                                    .replaceAll("field_78796_g", "rotateAngleY")
                                    .replaceAll("func_78790_a", "addBox")
                                    .replaceAll("f", "F")
                                    .replaceAll("\\(ModelBase\\)", "")
                                    + "\n");
                        else if(data.contains("texture") && !data.contains("ModelRendererTurbo"))
                            writer.write(data + "\n");
                    }
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Display of the end of the process
        JOptionPane.showMessageDialog(null, "The files in the import folder have been successfully processed in the export folder", "Java Vehicle Exporter", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Get .java files from a folder and return a list of files
     * @param path of the folder containing the .java models
     * @return List of .java files
     */
    public static ArrayList<File> getModels(final File path){
        ArrayList<File> files = new ArrayList<>();
        for(final File file : path.listFiles()){
            String name = file.getName();
            if(file.isFile() && name.substring(name.lastIndexOf(".") + 1).toLowerCase().equals("java"))
                files.add(file);
        }
        return files;
    }

    /**
     * Get .java files from a folder and return a list of files
     * @param path of the folder containing the .java models
     * @return List of .java files
     */
    public static ArrayList<File> getTextures(final File path){
        ArrayList<File> files = new ArrayList<>();
        for(final File file : path.listFiles()){
            String name = file.getName();
            if(file.isFile() && name.substring(name.lastIndexOf(".") + 1).toLowerCase().equals("png"))
                files.add(file);
        }
        return files;
    }
}
