package com.example.proyectoads;

import Control.Consultas.controladorConsultaEstudiantes;
import Control.Consultas.controladorConsultaProfesores;
import Control.Gestion.controladorCrearAsignatura;
import Control.Gestion.controladorGestionAsignaturasEstudiante;
import Modelo.Asignatura;
import Modelo.Clase;
import Modelo.Departamento;
import Modelo.Estudiante;
import Modelo.Profesor;
import Vista.InterfazConsultaAsignaturas;
import Vista.InterfazConsultaEstudiantes;
import Vista.InterfazConsultaProfesores;
import Vista.InterfazCrearAsignatura;
import Vista.InterfazGestionAsignaturasEstudiante;
import Serializacion.Serializacion;
import Serializacion.Deserializacion;
import Control.Consultas.controladorConsultaAsignaturas;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // ===== PROFESORES =====
        Profesor p1 = new Profesor("juan.perez@uni.edu", "Juan Perez",
                16, 3000000, "DOC001", "P001");
        Profesor p2 = new Profesor("ana.gomez@uni.edu", "Ana Gomez",
                12, 2800000, "DOC002", "P002");

        List<Profesor> profesores = new ArrayList<Profesor>();
        profesores.add(p1);
        profesores.add(p2);
        // Carpeta donde quieres guardar (puede ser relativa)
        String base = "datos";  // crea la carpeta 'datos' en la raíz del proyecto

        // SERIALIZAR
        Serializacion.guardarProfesoresTexto(profesores, base + "/profesores.txt");
        Serializacion.guardarProfesoresCSV(profesores, base + "/profesores.csv");
        Serializacion.guardarProfesoresJSON(profesores, base + "/profesores.json");

        // DESERIALIZAR
        List<Profesor> profsTxt  = Deserializacion.cargarProfesoresTexto(base + "/profesores.txt");
        List<Profesor> profsCsv  = Deserializacion.cargarProfesoresCSV(base + "/profesores.csv");
        List<Profesor> profsJson = Deserializacion.cargarProfesoresJSON(base + "/profesores.json");

        System.out.println("Desde TXT hay:  " + profsTxt.size()  + " profesores");
        System.out.println("Desde CSV hay:  " + profsCsv.size()  + " profesores");
        System.out.println("Desde JSON hay: " + profsJson.size() + " profesores");


        // ===== ASIGNATURAS =====
        Asignatura a1 = new Asignatura("ADS101", "Analisis y Diseno de SW", 3, false);
        Asignatura a2 = new Asignatura("BD102", "Bases de Datos", 4, false);
        Asignatura a3 = new Asignatura("RED103", "Redes de Computadores", 3, false);

        List<Asignatura> asignaturas = new ArrayList<Asignatura>();
        asignaturas.add(a1);
        asignaturas.add(a2);
        asignaturas.add(a3);

        // ===== CLASES =====
        Clase c1 = new Clase("C001", "2025-1", "Lu-Mi", "8-10", "A101", 40, p1);
        Clase c2 = new Clase("C002", "2025-1", "Ma-Ju", "10-12", "A102", 35, p1);
        Clase c3 = new Clase("C003", "2025-2", "Lu-Mi", "14-16", "B201", 30, p1);
        Clase c4 = new Clase("C004", "2025-1", "Ma-Ju", "8-10", "B202", 40, p2);

        a1.getClases().add(c1);
        a2.getClases().add(c2);
        a3.getClases().add(c3);
        a1.getClases().add(c4);

        // ===== ESTUDIANTES =====
        Estudiante e1 = new Estudiante("Carlos Lopez", "E001",
                "carlos@uni.edu", "Ingenieria de Sistemas", true);
        Estudiante e2 = new Estudiante("Maria Ruiz", "E002",
                "maria@uni.edu", "Ingenieria Industrial", false);

        List<Estudiante> estudiantes = new ArrayList<Estudiante>();
        estudiantes.add(e1);
        estudiantes.add(e2);

        // Inscribir estudiantes en clases
        c1.getEstudiantesInscritos().add(e1);
        c2.getEstudiantesInscritos().add(e1);
        c1.getEstudiantesInscritos().add(e2);
        c3.getEstudiantesInscritos().add(e2);

        // ===== DEPARTAMENTOS =====
        List<Departamento> departamentos = new ArrayList<Departamento>();

        Departamento depSis = new Departamento(1, "Ingenieria de Sistemas");
        depSis.getAsignaturas().add(a1);
        depSis.getAsignaturas().add(a2);

        Departamento depTele = new Departamento(2, "Telematica");
        depTele.getAsignaturas().add(a3);

        departamentos.add(depSis);
        departamentos.add(depTele);

        // ===== CONTROLADORES Y VISTAS =====

        // Consultas de asignaturas
        controladorConsultaAsignaturas controladorConsultas =
                new controladorConsultaAsignaturas(profesores, asignaturas);
        controladorConsultas.setDepartamentos(departamentos);

        InterfazConsultaAsignaturas vistaConsultas =
                new InterfazConsultaAsignaturas(controladorConsultas);

        // Gestion de asignaturas de estudiante
        controladorGestionAsignaturasEstudiante controladorGestion =
                new controladorGestionAsignaturasEstudiante(estudiantes, asignaturas);

        InterfazGestionAsignaturasEstudiante vistaGestion =
                new InterfazGestionAsignaturasEstudiante(controladorGestion);

        // Consulta de estudiantes por asignatura
        controladorConsultaEstudiantes controladorEstudiantes =
                new controladorConsultaEstudiantes(asignaturas);

        InterfazConsultaEstudiantes vistaEstudiantes =
                new InterfazConsultaEstudiantes(controladorEstudiantes);

        // Consulta de informacion de profesor
        controladorConsultaProfesores controladorProfesores =
                new controladorConsultaProfesores(profesores);

        InterfazConsultaProfesores vistaProfesores =
                new InterfazConsultaProfesores(controladorProfesores);

        // NUEVO: crear nuevas asignaturas
        controladorCrearAsignatura controladorCrear =
                new controladorCrearAsignatura(asignaturas, departamentos);

        InterfazCrearAsignatura vistaCrear =
                new InterfazCrearAsignatura(controladorCrear);

        // ===== MENU PRINCIPAL =====
        Scanner menuScanner = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println();
            System.out.println("=== Sistema de gestion de asignaturas ===");
            System.out.println("1. Consultar asignaturas por profesor y semestre");
            System.out.println("2. Consultar informacion de una asignatura");
            System.out.println("3. Gestionar asignaturas de un estudiante");
            System.out.println("4. Consultar estudiantes inscritos en una asignatura");
            System.out.println("5. Consultar asignaturas por departamento");
            System.out.println("6. Consultar informacion de un profesor");
            System.out.println("7. Crear nueva asignatura");
            System.out.println("8. Salir");
            System.out.print("Seleccione una opcion: ");

            String linea = menuScanner.nextLine();

            try {
                opcion = Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            if (opcion == 1) {
                vistaConsultas.consultarProfesorAsignaturaSemestre();
            } else if (opcion == 2) {
                vistaConsultas.consultarInformacionAsignatura();
            } else if (opcion == 3) {
                vistaGestion.menuGestionEstudiante();
            } else if (opcion == 4) {
                vistaEstudiantes.consultarEstudiantesPorAsignatura();
            } else if (opcion == 5) {
                vistaConsultas.consultarAsignaturasPorDepartamento();
            } else if (opcion == 6) {
                vistaProfesores.consultarInformacionProfesor();
            } else if (opcion == 7) {
                vistaCrear.crearNuevaAsignatura();
            } else if (opcion == 8) {
                System.out.println("Saliendo del sistema...");
            } else {
                System.out.println("Opcion invalida. Intente de nuevo.");
            }

        } while (opcion != 8);

        menuScanner.close();
    }
}
