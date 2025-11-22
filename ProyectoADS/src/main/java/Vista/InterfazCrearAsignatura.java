package Vista;

import Control.Gestion.controladorCrearAsignatura;

import java.util.Scanner;

public class InterfazCrearAsignatura {

    private final controladorCrearAsignatura controlador;
    private final Scanner sc;

    public InterfazCrearAsignatura(controladorCrearAsignatura controlador) {
        this.controlador = controlador;
        this.controlador.setVista(this);
        this.sc = new Scanner(System.in);
    }

    // Caso de uso: crear nueva asignatura
    public void crearNuevaAsignatura() {

        System.out.println();
        System.out.println("=== Crear nueva asignatura ===");

        System.out.print("Codigo de la asignatura: ");
        String codigo = sc.nextLine();

        System.out.print("Nombre de la asignatura: ");
        String nombre = sc.nextLine();

        System.out.print("Creditos: ");
        String textoCreditos = sc.nextLine();

        int creditos;
        try {
            creditos = Integer.parseInt(textoCreditos);
        } catch (NumberFormatException e) {
            mostrarError("Los creditos deben ser un numero entero.");
            return;
        }

        System.out.print("Requiere examen de ingles? (S/N): ");
        String respIngles = sc.nextLine();
        boolean requiereIngles = "S".equalsIgnoreCase(respIngles.trim());

        System.out.print("Id del departamento (opcional, Enter si no aplica): ");
        String textoDep = sc.nextLine();

        Integer idDepartamento = null;
        if (textoDep != null && !textoDep.trim().isEmpty()) {
            try {
                idDepartamento = Integer.parseInt(textoDep.trim());
            } catch (NumberFormatException e) {
                mostrarError("El id del departamento debe ser un numero entero.");
                return;
            }
        }

        // Validaciones basicas
        if (codigo == null || codigo.trim().isEmpty()) {
            mostrarError("El codigo de la asignatura no puede estar vacio.");
            return;
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarError("El nombre de la asignatura no puede estar vacio.");
            return;
        }

        // Llamar al controlador
        controlador.crearAsignatura(codigo.trim(),
                nombre.trim(),
                creditos,
                requiereIngles,
                idDepartamento);
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
