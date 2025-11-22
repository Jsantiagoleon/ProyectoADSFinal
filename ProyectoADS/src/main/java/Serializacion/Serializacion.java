package Serializacion;

import Modelo.Profesor;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Serializacion {

    // ========= TEXTO PLANO (formato propio, separado por ;) =========
    public static void guardarProfesoresTexto(List<Profesor> profesores, String rutaArchivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {

            for (Profesor p : profesores) {
                if (p == null) {
                    continue;
                }
                String linea = p.getId_Profesor() + ";" +
                        p.getCodDocente() + ";" +
                        p.getNombre() + ";" +
                        p.getEmail() + ";" +
                        p.getHorasSemanales() + ";" +
                        p.getSueldo();

                bw.write(linea);
                bw.newLine();
            }

            System.out.println("Profesores guardados en TXT: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al guardar profesores en texto: " + e.getMessage());
        }
    }

    // ========= CSV (separado por coma, con encabezado) =========
    public static void guardarProfesoresCSV(List<Profesor> profesores, String rutaArchivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {

            // Encabezado
            bw.write("idProfesor,codDocente,nombre,email,horasSemanales,sueldo");
            bw.newLine();

            for (Profesor p : profesores) {
                if (p == null) {
                    continue;
                }
                String linea = p.getId_Profesor() + "," +
                        p.getCodDocente() + "," +
                        p.getNombre() + "," +
                        p.getEmail() + "," +
                        p.getHorasSemanales() + "," +
                        p.getSueldo();

                bw.write(linea);
                bw.newLine();
            }

            System.out.println("Profesores guardados en CSV: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al guardar profesores en CSV: " + e.getMessage());
        }
    }

    // ========= JSON (usando Gson) =========
    public static void guardarProfesoresJSON(List<Profesor> profesores, String rutaArchivo) {
        Gson gson = new Gson();

        try (Writer writer = new FileWriter(rutaArchivo)) {

            gson.toJson(profesores, writer);
            System.out.println("Profesores guardados en JSON: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al guardar profesores en JSON: " + e.getMessage());
        }
    }
}
