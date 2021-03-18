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
    static HashMap<String, String> citiesToNation = new HashMap<String, String>();
    static String cwd = new File("").getAbsolutePath();
    static Charset charset = Charset.forName("ISO-8859-1");
    static HashMap<String, String> provinces = new HashMap<String, String>();
    static List<String> lines;

    public static void main(String[] args) {
        Scanner inputScan = new Scanner(System.in);
        System.out.println("Enter save game name (include the .eu4):");
        String saveName = inputScan.next();
        fileName = cwd + "\\" + saveName;
        String rural = "";
        String upper = "";
        String urban = "";
        String numeroRural = "";
        String numeroUpper = "";
        String numeroUrban = "";

        //ArrayList<String> francia = new ArrayList<String>();
        System.out.println("Tag que quieres saber ( Escribe done para pararlo ) ");
        String tag = inputScan.next();
        int totalRural = 0;
        int totalUpper = 0;
        int totalUrban = 0;
        do {
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
            int total = totalRural + totalUpper + totalUrban;
            System.out.println("Total pop rural: " + totalRural);
            System.out.println("Total pop upper: " + totalUpper);
            System.out.println("Total pop urban: " + totalUrban);
            System.out.println("Total pop: " + total + " k");
            System.out.println("Tag que quieres saber");
            tag = inputScan.next();
        } while (!"done".equals(tag));
    }
}
