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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Alberto
 */
public class MeiouPOP {

    /**
     * @param args the command line arguments
     */
    public static String fileName = "";
    static String cwd = new File("").getAbsolutePath();
    static Charset charset = Charset.forName("ISO-8859-1"); // Formato del save
    static List<String> lines; // Array para almacenar las lineas del save
    static File carpeta = new File(System.getProperty("user.dir")); // Directorio donde se ejecuta el programa
    static String[] cabecera = {"TAG", "Pop urbana", "Pop total", "Pop mundial", "Pop mundial urbana", "Income anual"};
    static int mundialPop = 0;
    static ArrayList<String> jugadores = new ArrayList<String>();
    static HashMap<String, Integer> provincias = new HashMap<String, Integer>();
    static HashMap<String, Integer> provinciasUrban = new HashMap<String, Integer>();

    // Metodo que limpia la lista con los datos para que se separen por save
    public static void limpiarLista(List<String[]> lista) {
        for (int i = 1; i < lista.size(); i++) {
            lista.set(i, null);
        }
    }

    // Obtiene la pop total mundial y la pop urbana mundial provincia a provincia para coger también la del mundo borrado
    public static long[] obtenerMundo() throws IOException {
        ArrayList<String> listaPop = new ArrayList<>();
        ArrayList<String> listaUrban = new ArrayList<>();
        long totalPop = 0;
        long totalUrban = 0;
        String totalString = "";
        String urban = "";
        String numeroTotal = "";
        String numeroUrban = "";
        long totalUpper = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("total_pop_display=")) {
                totalString = lines.get(i);
                numeroTotal = totalString.substring(21).replace(".", "");
                listaPop.add(numeroTotal);
                //System.out.println("Prueba = " + numeroTotal);
            }
            if (line.contains("urban_pop_display=")) {
                urban = lines.get(i);
                numeroUrban = urban.substring(21).replace(".", "");
                listaUrban.add(numeroUrban);
                //System.out.println("Prueba = " + numeroUrban);
            }
        }
        // Suma el total de todas las provincias y los pone en listas para luego sumarlos
        for (String listaPop1 : listaPop) {
            totalPop = totalPop + Integer.parseInt(listaPop1);
        }
        for (String listaUrban1 : listaUrban) {
            totalUrban = totalUrban + Integer.parseInt(listaUrban1);
        }

        //System.out.println("NUMERO DE POP TOTAL " + totalPop);
        System.out.println();

        //Pasa con el return un array con los dos valores
        long[] mundo = {totalPop, totalUrban};
        return mundo;
    }
    
    // Coge todos los valores del income que estan separados por categorias y los suma 
    public static double sumarIncome(String[] income) {
        double resultado = 0;
        for (int i = 0; i < income.length; i++) {
            String temp = income[i];
            resultado = resultado + Double.parseDouble(temp);
        }
        return resultado;
    }
    // Busca en el save paises que cumplan que son jugadores o eran, los mete en el array de jugadores
    public static void detectarJugadores() {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("human=yes")) {
                String tag = lines.get(i - 1);
                tag = tag.substring(0, 4);
                tag = tag.replace("\t", "");
                tag = tag.replace(" ", "");
                jugadores.add(tag);
            } else if (line.matches("\t{1}[A-Z0-9]{3,4}=[{]") && lines.get(i + 1).contains("was_player=")) {
                String tag = lines.get(i);
                tag = tag.substring(0, 4);
                tag = tag.replace("\t", "");
                tag = tag.replace(" ", "");
                jugadores.add(tag);
            }
        }
    }
    // Ordena el HashMap que se le pase por parametro por valor e identificador ( si 2undo parametro true asc si false desc )
    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> unsortMap, final boolean order) {
        List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));

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
                String extension = archivo.substring(indice + 1); // Obtiene la extensión

                if (listaArchivos[j].isFile() && extension.equals("eu4")) { // Comprobamos que sea una archivo y su extension eu4 para que no lea los otros
                    fileName = cwd + "\\" + listaArchivos[j].getName();
                    lines = Files.readAllLines(Paths.get(fileName), charset);
                    jugadores.clear();
                    detectarJugadores(); // Mete los jugadores dentro del arraylist para no tener que ponerlos a mano
                    long[] popMundial = obtenerMundo(); // Obtiene la pop mundial
                    System.out.println("POP MUNDIAL TOTAL = " + popMundial[0]);
                    System.out.println(jugadores);
                    System.out.println("SAVE: " + listaArchivos[j].getName() + "\n");
                    //Inicaliza las variables a 0 porque daba error si no
                    String rural = "";
                    String upper = "";
                    String urban = "";
                    String numeroRural = "";
                    String numeroUpper = "";
                    String numeroUrban = "";
                    String popTotal = "";

                    //Recorre toda la lista de países y va sacando sus datos 1 a 1
                    for (int z = 0; z < jugadores.size(); z++) {
                        String tag = jugadores.get(z);
                        System.out.println("");
                        System.out.println("TAG DEL PAÍS: " + tag + "\n");
                        double resultado = 0;
                        int totalRural = 0;
                        int totalUpper = 0;
                        int totalUrban = 0;
                        int total = 0;
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

                                if (line.replace("\t", "").equals("owner=\"" + tag + "\"") && lines.get(i - 1).contains("name=")) {
                                    String nombre = lines.get(i - 1).substring(8, lines.get(i - 1).length() - 1);
                                    System.out.println("Provincia: " + nombre);
                                    System.out.println("Rural: " + numeroRural + " k");
                                    System.out.println("Upper: " + numeroUpper + " k");
                                    System.out.println("Urbana: " + numeroUrban + " k");
                                    int totalProv = Integer.parseInt(numeroRural) + Integer.parseInt(numeroUpper) + Integer.parseInt(numeroUrban);
                                    System.out.println();
                                    provincias.put(nombre, totalProv);
                                    provinciasUrban.put(nombre, Integer.parseInt(numeroUrban));
                                }
                                // Este if comprueba que la provincia sea de un tag de jugador y entonces muestra sus datos
                                if (line.contains(tag + "={") && (lines.get(i + 1).contains("human=yes") || lines.get(i + 1).contains("was_player=yes"))) {
                                    // Se usa otro for y una linea temporal para asi buscar por "bloques" dentro del save de jugador en jugador ( no se si optimiza o simplemente hace que vaya peor )
                                    for (int x = i; x < lines.size(); x++) {
                                        String tempLine = lines.get(x);
                                        if (tempLine.contains("country_total_pop_r")) {
                                            popTotal = lines.get(x);
                                            popTotal = popTotal.substring(23).replace(".", "");

                                            total = Integer.parseInt(popTotal);
                                            System.out.println("TOTAL POP= " + popTotal);
                                        }
                                        if (tempLine.contains("country_total_urban")) {
                                            numeroUrban = lines.get(x);
                                            numeroUrban = numeroUrban.substring(23).replace(".", "");
                                            totalUrban = Integer.parseInt(numeroUrban);
                                            System.out.println("TOTAL POP URBANA= " + numeroUrban);
                                        }
                                        if (tempLine.contains("lastyearincome={")) {
                                            String income = lines.get(x + 1);
                                            String[] incomeArray = income.split(" ");
                                            resultado = sumarIncome(incomeArray);
                                            System.out.println("INCOME " + resultado + " DE " + tag);
                                            break;
                                        }
                                    }

                                }

                                //obtenerVasallo(line, tag, i);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("ERROR AL LEER EL SAVE");
                        }
                        // Muestra los datos del total

                        // Exportar a csv
                        String[] fila = {tag, Integer.toString(totalUrban), Integer.toString(total), Long.toString(popMundial[0]), Long.toString(popMundial[1]), Double.toString(resultado)};

                        list.add(fila);

                    }
                    //Coge los dos mapas con sus valores e identificadores, los ordena desc y luego muestra los 10 primeros pasandolos a listas
                    HashMap<String, Integer> sortedMapAsc = sortByValue(provincias,false);
                    List<Integer>listaTopPop = new ArrayList<>(sortedMapAsc.values());
                    List<String>listaTopNombre = new ArrayList<>(sortedMapAsc.keySet());
                    System.out.println("****************************************");
                    System.out.println(" TOP 10 PROVINCIAS POR POP DE JUGADORES ");
                    System.out.println("****************************************");
                    for (int i = 0; i < 10; i++) {
                        System.out.println((i+1) + ". " +listaTopNombre.get(i)+ " / " + listaTopPop.get(i));
                    }
                    //Coge los dos mapas con sus valores e identificadores, los ordena desc y luego muestra los 10 primeros pasandolos a listas
                    HashMap<String, Integer> sortedMapAscUrban = sortByValue(provinciasUrban,false);
                    List<Integer>listaUrbanPop = new ArrayList<>(sortedMapAscUrban.values());
                    List<String>listaUrbanNombre = new ArrayList<>(sortedMapAscUrban.keySet());
                    System.out.println("***********************************************");
                    System.out.println(" TOP 10 PROVINCIAS POR POP URBANA DE JUGADORES ");
                    System.out.println("***********************************************");
                    for (int i = 0; i < 10; i++) {
                        System.out.println((i+1) + ". " +listaUrbanNombre.get(i)+ " / " + listaUrbanPop.get(i));
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
