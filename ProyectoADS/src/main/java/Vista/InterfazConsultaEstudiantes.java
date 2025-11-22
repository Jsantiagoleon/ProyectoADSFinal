package Vista;

import Control.Consultas.controladorConsultaEstudiantes;
import Modelo.Asignatura;
import Modelo.Estudiante;

import java.util.List;
import java.util.Scanner;

public class InterfazConsultaEstudiantes {

    private final controladorConsultaEstudiantes controlador;
    private final Scanner sc;

    public InterfazConsultaEstudiantes(controladorConsultaEstudiantes controlador) {
        this.controlador = controlador;
        this.controlador.setVista(this);
        this.sc = new Scanner(System.in);
    }

    // Lanza el caso de uso desde la vista
    public void consultarEstudiantesPorAsignatura() {

        System.out.println();
        System.out.println("Consultar estudiantes inscritos en una asignatura especifica");

        System.out.print("Codigo de la asignatura: ");
        String codigo = sc.nextLine();

        if (!validarCodigo(codigo)) {
            mostrarError("Debe ingresar un codigo de asignatura valido.");
            return;
        }

        controlador.consultarEstudiantesPorAsignatura(codigo);
    }

    private boolean validarCodigo(String codigo) {
        if (codigo == null) {
            return false;
        }
        return !codigo.trim().isEmpty();
    }

    // Metodo llamado por el controlador cuando ya tiene los datos
    public void mostrarEstudiantesAsignatura(Asignatura asignatura,
                                             List<Estudiante> estudiantes) {

        System.out.println();
        System.out.println("Asignatura: " + asignatura.getCodigoAsignatura()
                + " - " + asignatura.getNombreAsignatura());

        if (estudiantes == null || estudiantes.isEmpty()) {
            System.out.println("No hay estudiantes inscritos en esta asignatura.");
        } else {
            System.out.println("Estudiantes inscritos:");
            for (Estudiante e : estudiantes) {
                System.out.println("  - " + e.getIdEstudiante()
                        + " | " + e.getNombre()
                        + " | " + e.getEmail());
            }
        }
    }

    public void mostrarError(String mensaje) {
        System.out.println();
        System.out.println("[ERROR] " + mensaje);
    }
}
