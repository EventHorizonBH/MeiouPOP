/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MeiouPOP;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alberto
 */
public class MeiouPOP {

    /**
     * @param args the command line arguments
     */
    public static String fileName = "";
    static int StaticStartingIndex = 0;
    static String cwd = new File("").getAbsolutePath();
    static Charset charset = Charset.forName("ISO-8859-1"); // Formato del save
    static List<String> lines; // Array para almacenar las lineas del save
    static File carpeta = new File(System.getProperty("user.dir")); // Directorio donde se ejecuta el programa
    static String paises[] = new String[]{"HAB", "POL", "PAP", "BRA", "MLO", "CAS", "MOS", "ENG", "OTT", "FRA", "DEN", "HOL"}; // Array con la lista de paises con jugador
    static String[] cabecera = {"TAG", "Pop rural", "Pop Upper", "Pop urbana", "Pop total"};

    public static void main(String[] args) {
        //Scanner inputScan = new Scanner(System.in);
        //System.out.println("Enter save game name (include the .eu4):"); //Comentado por uso de carpeta en vez de poner nombre a mano
        //String saveName = inputScan.next();
        //fileName = cwd + "\\" + saveName;
        List<String[]> list = new ArrayList<>();
        list.add(cabecera);
        File[] listaArchivos = carpeta.listFiles();
        // Recorre toda la carpeta en busca de archivos, si es un archivo saca los datos de los paises dentro, si no lo es da error
        for (int j = 0; j < listaArchivos.length; j++) {
            if (listaArchivos[j].isFile()) {
                fileName = cwd + "\\" + listaArchivos[j].getName();
                System.out.println("SAVE: " + listaArchivos[j].getName() + "\n");
                //Inicaliza las variables a 0 porque daba error si no
                String rural = "";
                String upper = "";
                String urban = "";
                String numeroRural = "";
                String numeroUpper = "";
                String numeroUrban = "";

                //System.out.println("Tag que quieres saber ( Escribe done para pararlo ) ");
                //Recorre toda la lista de países y va sacando sus datos 1 a 1
                for (int z = 0; z < paises.length; z++) {
                    String tag = paises[z];
                    System.out.println("TAG DEL PAÍS: " + tag + "\n");
                    //String tag = inputScan.next();
                    int totalRural = 0;
                    int totalUpper = 0;
                    int totalUrban = 0;
                    try {
                        totalRural = 0;
                        totalUpper = 0;
                        totalUrban = 0;
                        lines = Files.readAllLines(Paths.get(fileName), charset);
                        for (int i = 0; i < lines.size(); i++) {
                            String line = lines.get(i);
                            if (line.contains("rural_pop_display=")) {
                                rural = lines.get(i);
                                numeroRural = rural.substring(21).replace(".", "");
                                //System.out.println("Prueba = " + numeroRural);
                            }
                            if (line.contains("upper_pop_display=")) {
                                upper = lines.get(i);
                                numeroUpper = upper.substring(21).replace(".", "");
                                //System.out.println("Upper = " + numero);
                            }
                            if (line.contains("urban_pop_display=")) {
                                urban = lines.get(i);
                                numeroUrban = urban.substring(21).replace(".", "");
                                //System.out.println("Urban = " + numero);
                                //System.out.println();
                            }
                            // Suma el total de todas las provincias y muestra datos provincia a provincia
                            if (line.contains("owner=\"" + tag + "\"") && lines.get(i - 1).contains("name=")) {
                                String nombre = lines.get(i - 1).substring(8, lines.get(i - 1).length() - 1);
                                System.out.println("Provincia: " + nombre);
                                System.out.println("Rural: " + numeroRural + " k");
                                System.out.println("Upper: " + numeroUpper + " k");
                                System.out.println("Urbana: " + numeroUrban + " k");
                                totalRural = totalRural + Integer.parseInt(numeroRural);
                                totalUpper = totalUpper + Integer.parseInt(numeroUpper);
                                totalUrban = totalUrban + Integer.parseInt(numeroUrban);
                                System.out.println();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR AL LEER EL SAVE");
                    }
                    // Muestra los datos del total
                    int total = totalRural + totalUpper + totalUrban;
                    System.out.println("Total pop rural: " + totalRural);
                    System.out.println("Total pop upper: " + totalUpper);
                    System.out.println("Total pop urban: " + totalUrban);
                    System.out.println("Total pop: " + total + " k");
                    System.out.println("");

                    // Exportar a csv
                    String[] fila = {tag, Integer.toString(totalRural), Integer.toString(totalUpper), Integer.toString(totalUrban), Integer.toString(total)};

                    list.add(fila);

                }
            } else {
                System.out.println("No es un archivo, es una carpeta u otras cositas");
            }
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter(cwd + "\\resultados.csv"))) {
            writer.writeAll(list);
        } catch (IOException ex) {
            Logger.getLogger(MeiouPOP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
