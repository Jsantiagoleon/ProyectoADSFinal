package Vista;

import Modelo.Asignatura;
import Modelo.Clase;
import Modelo.Profesor;
import Control.Consultas.controladorConsultaAsignaturas;

import java.util.List;
import java.util.Scanner;

public class InterfazConsultaAsignaturas {

    private final controladorConsultaAsignaturas controlador;
    private final Scanner sc;

    public InterfazConsultaAsignaturas(controladorConsultaAsignaturas controlador) {
        this.controlador = controlador;
        this.controlador.setVista(this);
        this.sc = new Scanner(System.in);
    }

    //  CASO DE USO: PROFESOR + SEMESTRE

    public void consultarProfesorAsignaturaSemestre() {

        System.out.println("Consultar asignaturas dictadas por un profesor en un semestre");

        System.out.print("Id del profesor: ");
        String idProfesor = sc.nextLine();

        System.out.print("Semestre (ejemplo: 2025-1): ");
        String semestre = sc.nextLine();

        if (!validarProfesorSemestre(idProfesor, semestre)) {
            mostrarError("Debe ingresar un id de profesor y un semestre validos.");
            return;
        }

        controlador.buscarPorProfesorSemestre(idProfesor, semestre);
    }

    private boolean validarProfesorSemestre(String idProfesor, String semestre) {
        if (idProfesor == null || semestre == null) {
            return false;
        }
        if (idProfesor.trim().isEmpty()) {
            return false;
        }
        if (semestre.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public void mostrarResultados(Profesor profesor,
                                  List<Asignatura> asignaturas,
                                  String semestre) {

        System.out.println();
        System.out.println("Profesor: " + profesor.getNombre()
                + " (id: " + profesor.getId_Profesor() + ")");
        System.out.println("Semestre: " + semestre);

        if (asignaturas == null || asignaturas.isEmpty()) {
            System.out.println("El profesor no dicta asignaturas en este semestre.");
        } else {
            System.out.println("Asignaturas dictadas:");
            for (Asignatura a : asignaturas) {
                System.out.println("- " + a.getCodigoAsignatura()
                        + " - " + a.getNombreAsignatura());
            }
        }
    }

    // CASO DE USO: INFORMACION DE UNA ASIGNATURA

    public void consultarInformacionAsignatura() {

        System.out.println();
        System.out.println("Consultar informacion de una asignatura");

        System.out.print("Codigo de la asignatura: ");
        String codigo = sc.nextLine();

        if (!validarCodigoAsignatura(codigo)) {
            mostrarError("Debe ingresar un codigo de asignatura valido.");
            return;
        }

        controlador.buscarAsignaturaPorCodigo(codigo);
    }

    private boolean validarCodigoAsignatura(String codigo) {
        if (codigo == null) {
            return false;
        }
        return !codigo.trim().isEmpty();
    }

    public void mostrarInfoAsignatura(Asignatura asignatura) {

        System.out.println();
        System.out.println("-Informacion de la asignatura-");
        System.out.println("Codigo : " + asignatura.getCodigoAsignatura());
        System.out.println("Nombre : " + asignatura.getNombreAsignatura());

        List<Clase> clases = asignatura.getClases();

        if (clases == null || clases.isEmpty()) {
            System.out.println("La asignatura no tiene clases asociadas.");
        } else {
            System.out.println("Clases de la asignatura:");
            for (Clase c : clases) {

                String nombreProf;
                if (c.getProfesor() != null) {
                    nombreProf = c.getProfesor().getNombre();
                } else {
                    nombreProf = "Sin profesor asignado";
                }

                System.out.println("  - IdClase: " + c.getIdClase()
                        + " | Semestre: " + c.getSemestre()
                        + " | Dias: " + c.getDias()
                        + " | Horas: " + c.getHoras()
                        + " | Salon: " + c.getSalon()
                        + " | Profesor: " + nombreProf);
            }
        }
    }


    public void mostrarError(String mensaje) {
        System.out.println();
        System.out.println("[ERROR] " + mensaje);
    }
    public void consultarAsignaturasPorDepartamento() {

        System.out.println();
        System.out.println("Consultar asignaturas ofrecidas por un departamento especifico");

        System.out.print("Id del departamento (numero): ");
        String textoId = sc.nextLine();

        int idDepa;

        try {
            idDepa = Integer.parseInt(textoId);
        } catch (NumberFormatException e) {
            mostrarError("El id del departamento debe ser un numero entero.");
            return;
        }

        // Llamar al controlador
        List<Asignatura> asignaturasDepa =
                controlador.consultarAsignaturasPorDepartamento(idDepa);

        // Si el controlador devolvio null, asumimos que no existe ese departamento
        if (asignaturasDepa == null) {
            mostrarError("No se encontro un departamento con id " + idDepa);
            return;
        }

        System.out.println();
        System.out.println("Asignaturas del departamento " + idDepa + ":");

        if (asignaturasDepa.isEmpty()) {
            System.out.println("El departamento no tiene asignaturas registradas.");
        } else {
            for (Asignatura a : asignaturasDepa) {
                System.out.println("- " + a.getCodigoAsignatura()
                        + " - " + a.getNombreAsignatura());
            }
        }
    }

}
