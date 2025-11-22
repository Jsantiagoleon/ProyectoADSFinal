package Vista;

import Control.Gestion.controladorGestionAsignaturasEstudiante;
import Modelo.Clase;
import Modelo.Estudiante;

import java.util.List;
import java.util.Scanner;

public class InterfazGestionAsignaturasEstudiante {

    private final controladorGestionAsignaturasEstudiante controlador;
    private final Scanner sc;

    public InterfazGestionAsignaturasEstudiante(controladorGestionAsignaturasEstudiante controlador) {
        this.controlador = controlador;
        this.controlador.setVista(this);
        this.sc = new Scanner(System.in);
    }

    public void menuGestionEstudiante() {

        int opcion = 0;

        do {
            System.out.println();
            System.out.println("=== Gestion de asignaturas de estudiante ===");
            System.out.println("1. Adicionar clase a estudiante");
            System.out.println("2. Retirar clase de estudiante");
            System.out.println("3. Cambiar clase de estudiante");
            System.out.println("4. Ver clases inscritas de un estudiante");
            System.out.println("5. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");

            String linea = sc.nextLine();

            try {
                opcion = Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            if (opcion == 1) {
                gestionarAdicion();
            } else if (opcion == 2) {
                gestionarRetiro();
            } else if (opcion == 3) {
                gestionarCambio();
            } else if (opcion == 4) {
                gestionarVerClases();
            } else if (opcion == 5) {
                System.out.println("Volviendo al menu principal");
            } else {
                System.out.println("Opcion invalida");
            }

        } while (opcion != 5);
    }

    private void gestionarAdicion() {

        System.out.println();
        System.out.println("Adicionar clase a estudiante");

        System.out.print("Id del estudiante: ");
        String idEstudiante = sc.nextLine();

        System.out.print("Id de la clase: ");
        String idClase = sc.nextLine();

        controlador.adicionarClaseAEstudiante(idEstudiante, idClase);
    }

    private void gestionarRetiro() {

        System.out.println();
        System.out.println("Retirar clase de estudiante");

        System.out.print("Id del estudiante: ");
        String idEstudiante = sc.nextLine();

        System.out.print("Id de la clase: ");
        String idClase = sc.nextLine();

        controlador.retirarClaseDeEstudiante(idEstudiante, idClase);
    }

    private void gestionarCambio() {

        System.out.println();
        System.out.println("Cambiar clase de estudiante");

        System.out.print("Id del estudiante: ");
        String idEstudiante = sc.nextLine();

        System.out.print("Id de la clase actual: ");
        String idClaseActual = sc.nextLine();

        System.out.print("Id de la nueva clase: ");
        String idClaseNueva = sc.nextLine();

        controlador.cambiarClaseDeEstudiante(idEstudiante, idClaseActual, idClaseNueva);
    }

    private void gestionarVerClases() {

        System.out.println();
        System.out.println("Ver clases inscritas de un estudiante");

        System.out.print("Id del estudiante: ");
        String idEstudiante = sc.nextLine();

        controlador.mostrarClasesDeEstudiante(idEstudiante);
    }

    // ================== METODOS QUE LLAMA EL CONTROLADOR ==================

    public void mostrarClasesEstudiante(Estudiante estudiante, List<Clase> clases) {

        System.out.println();
        System.out.println("Clases del estudiante " + estudiante.getNombre()
                + " (id " + estudiante.getIdEstudiante() + ")");

        if (clases == null || clases.isEmpty()) {
            System.out.println("El estudiante no tiene clases inscritas");
        } else {
            for (Clase c : clases) {
                System.out.println("  - IdClase: " + c.getIdClase()
                        + " | Semestre: " + c.getSemestre()
                        + " | Dias: " + c.getDias()
                        + " | Horas: " + c.getHoras()
                        + " | Salon: " + c.getSalon());
            }
        }
    }

    public void mostrarError(String mensaje) {
        System.out.println();
        System.out.println("[ERROR] " + mensaje);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println();
        System.out.println(mensaje);
    }
}
