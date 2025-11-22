package Vista;

import Control.Consultas.controladorConsultaProfesores;
import Modelo.Profesor;

import java.util.Scanner;

public class InterfazConsultaProfesores {

    private final controladorConsultaProfesores controlador;
    private final Scanner sc;

    public InterfazConsultaProfesores(controladorConsultaProfesores controlador) {
        this.controlador = controlador;
        this.controlador.setVista(this);
        this.sc = new Scanner(System.in);
    }

    // 1.0 : ConsultarInformacionProfesor(id_Profesor)
    public void consultarInformacionProfesor() {

        System.out.println();
        System.out.println("Consultar informacion de un profesor");
        System.out.print("Id del profesor (id_Profesor o codDocente): ");

        String idProfesor = sc.nextLine();

        if (idProfesor == null || idProfesor.trim().isEmpty()) {
            mostrarMensaje("Debe ingresar un id de profesor valido.");
            return;
        }

        controlador.consultarProfesor(idProfesor);
    }

    // 1.5 : mostrarInformacionProfesor
    public void mostrarInformacionProfesor(Profesor profesor) {

        System.out.println();
        System.out.println("=== Informacion del profesor ===");
        System.out.println("Nombre         : " + profesor.getNombre());
        System.out.println("Correo         : " + profesor.getEmail());
        System.out.println("Id Profesor    : " + profesor.getId_Profesor());
        System.out.println("Codigo Docente : " + profesor.getCodDocente());
        System.out.println("Horas semanales: " + profesor.getHorasSemanales());
        System.out.println("Sueldo         : " + profesor.getSueldo());
    }

    // Rama [Profesor no encontrado] -> mostrarMensaje("El profesor no existe")
    public void mostrarMensaje(String mensaje) {
        System.out.println();
        System.out.println(mensaje);
    }
}
