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
import java.util.Arrays;
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
    static String paises[] = new String[]{"HAB", "POL", "PAP", "BRA", "MLO", "SPA", "RUS", "ENG", "OTT", "FRA", "DEN", "MUN"}; // Array con la lista de paises con jugador
    static String[] cabecera = {"TAG", "Pop rural", "Pop Upper", "Pop urbana", "Pop total", "Pop mundial", "Pop mundial urbana", "Income anual"};
    static int mundialPop = 0;

    // Metodo que limpia la lista con los datos para que se separen por save
    public static void limpiarLista(List<String[]> lista) {
        for (int i = 1; i < lista.size(); i++) {
            lista.set(i, null);
        }
    }

    // Obtiene la pop total mundial y la pop urbana mundial pero en vez de provincia a provincia la coge directamente del país
    public static long[] obtenerMundo() throws IOException {
        lines = Files.readAllLines(Paths.get(fileName), charset);
        long totalPop = 0;
        long totalUrban = 0;
        String rural = "";
        String upper = "";
        String urban = "";
        String numeroRural = "";
        String numeroUpper = "";
        String numeroUrban = "";
        long totalRural = 0;
        long totalUpper = 0;
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
                //System.out.println("Upper = " + numeroUpper);
            }
            if (line.contains("urban_pop_display=")) {
                urban = lines.get(i);
                numeroUrban = urban.substring(21).replace(".", "");
                //System.out.println("Urban = " + numeroUrban);
                //System.out.println();
            }
            // Suma el total de todas las provincias y muestra datos provincia a provincia
            if (!numeroRural.equals("") && !numeroUrban.equals("") && !numeroUpper.equals("")) {
                totalPop = 0;
                totalRural = totalRural + Long.parseLong(numeroRural);
                totalUpper = totalUpper + Long.parseLong(numeroUpper);
                totalUrban = totalUrban + Long.parseLong(numeroUrban);
            }
        }
        System.out.println();
        totalPop = totalRural + totalUrban + totalUpper;
        System.out.println("TOTAL POP DE TODAS ES: " + totalPop);
        /*String popTotal = "";
        String popTotalUrban = "";
        String numeroPopTotal = "";
        String numeroPopTotalUrban = "";
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("country_total_pop_r=")) {
                popTotal = lines.get(i);
                numeroPopTotal = popTotal.substring(24).replace(".", "");
                totalPop = totalPop + Integer.parseInt(numeroPopTotal);
            }
            if (line.contains("country_total_urban=")) {
                popTotalUrban = lines.get(i);
                numeroPopTotalUrban = popTotalUrban.substring(24).replace(".", "");
                totalUrban = totalUrban + Integer.parseInt(numeroPopTotalUrban);
            }
        }*/
        //Pasa con el return un array con los dos valores
        long[] mundo = {totalPop, totalUrban};
        return mundo;
    }
    public static double sumarIncome(String[] income){
        double resultado = 0;
        for (int i = 0; i < income.length; i++) {
            String temp = income[i];
            resultado = resultado + Double.parseDouble(temp);
        }
        return resultado;
    }

    // Para obtener los vasallos ( o un intento de )
    public static void obtenerVasallo(String line, String tag, int i) {
        String vasallo = "";
        if (line.contains("overlord=\"" + tag + "\"")) {
            for (int x = i; x >= 0; x--) {
                String tempLine = lines.get(x);
                if (tempLine.matches("\t{1}[A-Z0-9]{3,4}=[{]")) {
                    vasallo = tempLine.replace("={", "");
                    System.out.println("SOY EL VASALLO: " + vasallo + " DE " + tag);
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        List<String[]> list = new ArrayList<>();
        list.add(cabecera);
        File[] listaArchivos = carpeta.listFiles();
        // Recorre toda la carpeta en busca de archivos, si es un archivo saca los datos de los paises dentro, si no lo es da error
        for (int j = 0; j < listaArchivos.length; j++) {
            String archivo = listaArchivos[j].toString();
            int indice = archivo.lastIndexOf('.');
            if (indice > 0) {
                String extension = archivo.substring(indice + 1);

                if (listaArchivos[j].isFile() && extension.equals("eu4")) { // Comprobamos que sea una archivo y su extension eu4 para que no lea los otros
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
                        double resultado = 0;
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
                                if (line.contains(tag+"={") && lines.get(i+1).contains("human=yes")) {
                                    for (int x = i; x < lines.size(); x++) {
                                        String tempLine = lines.get(x);
                                        if (tempLine.contains("lastyearincome={")) {
                                                String income = lines.get(x+1);
                                                String[] incomeArray= income.split(" ");
                                                resultado = sumarIncome(incomeArray);
                                                System.out.println("INCOME " + resultado + " DE " + tag);
                                                break;
                                            }

                                        }

                                    }
                                
                                obtenerVasallo(line, tag, i);
                                //obtenerIncome(line, i, tag);
                                //obtenerIncome(line, i, tag);
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
                        //System.out.println("MUNDIAL POP ENTERA DE ENTERAS " + mundialPop);

                        // Exportar a csv
                        long[] popMundial = obtenerMundo();
                        String[] fila = {tag, Integer.toString(totalRural), Integer.toString(totalUpper), Integer.toString(totalUrban), Integer.toString(total), Long.toString(popMundial[0]).substring(0, Long.toString(popMundial[0]).length() - 3), Long.toString(popMundial[1]).substring(0, Long.toString(popMundial[0]).length() - 3), Double.toString(resultado)};

                        list.add(fila);

                    }
                    try {
                        Files.createDirectories(Paths.get(cwd + "\\resultados\\"));
                    } catch (IOException ex) {
                        System.err.println("Fallo al crear el directorio resultados" + ex.getMessage());
                    }
                    try (CSVWriter writer = new CSVWriter(new FileWriter(cwd + "\\resultados\\resultados" + listaArchivos[j].getName() + ".csv"))) {
                        writer.writeAll(list);
                        limpiarLista(list);
                    } catch (IOException ex) {
                        Logger.getLogger(MeiouPOP.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.out.println("No es un archivo, es una carpeta u otras cositas");
                }
            }
        }
    }
}
