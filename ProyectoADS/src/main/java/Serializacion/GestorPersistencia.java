package Serializacion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Administra la serialización y deserialización del estado completo del programa.
 */
public class GestorPersistencia {

    private final Path archivoEstado;

    public GestorPersistencia(Path archivoEstado) {
        this.archivoEstado = archivoEstado;
    }

    public void guardar(EstadoPrograma estadoPrograma) {
        try {
            Files.createDirectories(archivoEstado.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(archivoEstado))) {
                oos.writeObject(estadoPrograma);
            }
        } catch (IOException e) {
            System.err.println("No se pudo guardar el estado: " + e.getMessage());
        }
    }

    public EstadoPrograma cargar() {
        if (!Files.exists(archivoEstado)) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(archivoEstado))) {
            Object data = ois.readObject();
            if (data instanceof EstadoPrograma estado) {
                return estado;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se pudo cargar el estado: " + e.getMessage());
        }

        return null;
    }
}
