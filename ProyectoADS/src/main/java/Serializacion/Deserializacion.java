package Serializacion;

import Modelo.Profesor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Deserializacion {

    // Archivo.txt
    public static List<Profesor> cargarProfesoresTexto(String rutaArchivo) {

        List<Profesor> profesores = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {

            String linea;
            while ((linea = br.readLine()) != null) {

                // id;codDocente;nombre;email;horas;sueldo
                String[] partes = linea.split(";");
                if (partes.length != 6) {
                    continue;
                }

                Profesor p = new Profesor();
                p.setId_Profesor(partes[0]);
                p.setCodDocente(partes[1]);
                p.setNombre(partes[2]);
                p.setEmail(partes[3]);
                p.setHorasSemanales(Integer.parseInt(partes[4]));
                p.setSueldo(Integer.parseInt(partes[5]));

                profesores.add(p);
            }

            System.out.println("Profesores cargados desde TXT: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al leer profesores en texto: " + e.getMessage());
        }

        return profesores;
    }

    // Archivo CSV
    public static List<Profesor> cargarProfesoresCSV(String rutaArchivo) {

        List<Profesor> profesores = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {

            String linea;
            boolean primera = true;

            while ((linea = br.readLine()) != null) {

                if (primera) {         // saltar encabezado
                    primera = false;
                    continue;
                }

                // idProfesor,codDocente,nombre,email,horasSemanales,sueldo
                String[] partes = linea.split(",");
                if (partes.length != 6) {
                    continue;
                }

                Profesor p = new Profesor();
                p.setId_Profesor(partes[0]);
                p.setCodDocente(partes[1]);
                p.setNombre(partes[2]);
                p.setEmail(partes[3]);
                p.setHorasSemanales(Integer.parseInt(partes[4]));
                p.setSueldo(Integer.parseInt(partes[5]));

                profesores.add(p);
            }

            System.out.println("Profesores cargados desde CSV: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al leer profesores en CSV: " + e.getMessage());
        }

        return profesores;
    }

    // Archivo JSON
    public static List<Profesor> cargarProfesoresJSON(String rutaArchivo) {

        List<Profesor> profesores = new ArrayList<>();
        Gson gson = new Gson();

        try (Reader reader = new FileReader(rutaArchivo)) {

            Type tipoLista = new TypeToken<List<Profesor>>(){}.getType();
            List<Profesor> lista = gson.fromJson(reader, tipoLista);

            if (lista != null) {
                profesores = lista;
            }

            System.out.println("Profesores cargados desde JSON: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al leer profesores en JSON: " + e.getMessage());
        }

        return profesores;
    }
}
