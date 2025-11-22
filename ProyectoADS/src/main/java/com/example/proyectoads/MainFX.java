package com.example.proyectoads;

import Modelo.Asignatura;
import Modelo.Clase;
import Modelo.Departamento;
import Modelo.Estudiante;
import Modelo.Profesor;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainFX extends Application {

    // ====== DATOS EN MEMORIA ======
    private List<Profesor> profesores;
    private List<Asignatura> asignaturas;
    private List<Estudiante> estudiantes;
    private List<Departamento> departamentos;

    // ====== ARRANQUE ======
    @Override
    public void start(Stage primaryStage) {
        inicializarDatosEjemplo();

        TabPane tabPane = new TabPane();

        tabPane.getTabs().addAll(
                crearTabProfesorSemestre(),
                crearTabInfoAsignatura(),
                crearTabGestionEstudiante(),
                crearTabEstudiantesAsignatura(),
                crearTabAsignaturasPorDepartamento(),
                crearTabInfoProfesor(),
                crearTabCrearAsignatura()
        );

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(tabPane, 950, 600);
        primaryStage.setTitle("Sistema de Gestión de Asignaturas - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ============================================================
    // ================  TABS / CASOS DE USO  =====================
    // ============================================================

    // 1. Consultar asignaturas por profesor y semestre
    private Tab crearTabProfesorSemestre() {
        Tab tab = new Tab("Prof. vs Semestre");

        Label titulo = new Label("Consultar asignaturas dictadas por un profesor en un semestre");

        TextField txtIdProfesor = new TextField();
        txtIdProfesor.setPromptText("id_Profesor o codDocente (P001, DOC001, etc.)");

        TextField txtSemestre = new TextField();
        txtSemestre.setPromptText("Semestre (ej: 2025-1)");

        Button btnBuscar = new Button("Buscar");

        Label lblProfesor = new Label();
        Label lblMensaje = new Label();

        ListView<String> listaAsignaturas = new ListView<>();

        btnBuscar.setOnAction(e -> {
            lblProfesor.setText("");
            lblMensaje.setText("");
            listaAsignaturas.getItems().clear();

            String id = txtIdProfesor.getText().trim();
            String semestre = txtSemestre.getText().trim();

            if (id.isEmpty() || semestre.isEmpty()) {
                lblMensaje.setText("Debe ingresar un id de profesor y un semestre.");
                return;
            }

            Profesor profesor = buscarProfesorPorIdOCod(id);
            if (profesor == null) {
                lblMensaje.setText("No se encontró profesor con id/código: " + id);
                return;
            }

            List<Asignatura> resultado = obtenerAsignaturasProfesorSemestre(profesor, semestre);

            lblProfesor.setText("Profesor: " + profesor.getNombre()
                    + " (" + profesor.getId_Profesor() + ")  |  Semestre: " + semestre);

            if (resultado.isEmpty()) {
                lblMensaje.setText("El profesor no dicta asignaturas en ese semestre.");
            } else {
                ObservableList<String> items = FXCollections.observableArrayList();
                for (Asignatura a : resultado) {
                    items.add(a.getCodigoAsignatura() + " - " + a.getNombreAsignatura());
                }
                listaAsignaturas.setItems(items);
            }
        });

        HBox filaEntrada = new HBox(10,
                new Label("Id profesor:"), txtIdProfesor,
                new Label("Semestre:"), txtSemestre,
                btnBuscar
        );
        filaEntrada.setPadding(new Insets(10));

        VBox root = new VBox(10,
                titulo,
                filaEntrada,
                lblProfesor,
                lblMensaje,
                listaAsignaturas
        );
        root.setPadding(new Insets(15));

        tab.setContent(root);
        return tab;
    }

    // 2. Consultar información de una asignatura
    private Tab crearTabInfoAsignatura() {
        Tab tab = new Tab("Info Asignatura");

        Label titulo = new Label("Consultar información de una asignatura");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código de la asignatura (ADS101, BD102, ...)");

        Button btnBuscar = new Button("Buscar");

        Label lblDatosBasicos = new Label();
        Label lblMensaje = new Label();

        ListView<String> listaClases = new ListView<>();

        btnBuscar.setOnAction(e -> {
            lblDatosBasicos.setText("");
            lblMensaje.setText("");
            listaClases.getItems().clear();

            String codigo = txtCodigo.getText().trim();
            if (codigo.isEmpty()) {
                lblMensaje.setText("Debe ingresar un código de asignatura.");
                return;
            }

            Asignatura a = buscarAsignaturaPorCodigo(codigo);
            if (a == null) {
                lblMensaje.setText("No se encontró la asignatura con código: " + codigo);
                return;
            }

            String dep = (a.getDepartamento() != null)
                    ? a.getDepartamento().getNombreDepartamento()
                    : "(sin departamento)";

            lblDatosBasicos.setText("Código: " + a.getCodigoAsignatura()
                    + " | Nombre: " + a.getNombreAsignatura()
                    + " | Créditos: " + a.getCreditos()
                    + " | Req. inglés: " + (a.isRequiereExamenIngles() ? "Sí" : "No")
                    + " | Departamento: " + dep);

            if (a.getClases() == null || a.getClases().isEmpty()) {
                lblMensaje.setText("La asignatura no tiene clases registradas.");
            } else {
                ObservableList<String> items = FXCollections.observableArrayList();
                for (Clase c : a.getClases()) {
                    String nombreProf = (c.getProfesor() != null)
                            ? c.getProfesor().getNombre()
                            : "(sin profesor)";
                    items.add("IdClase: " + c.getIdClase()
                            + " | Semestre: " + c.getSemestre()
                            + " | Días: " + c.getDias()
                            + " | Horas: " + c.getHoras()
                            + " | Salón: " + c.getSalon()
                            + " | Profesor: " + nombreProf);
                }
                listaClases.setItems(items);
            }
        });

        HBox filaEntrada = new HBox(10, new Label("Código:"), txtCodigo, btnBuscar);
        filaEntrada.setPadding(new Insets(10));

        VBox root = new VBox(10,
                titulo,
                filaEntrada,
                lblDatosBasicos,
                lblMensaje,
                new Label("Clases de la asignatura:"),
                listaClases
        );
        root.setPadding(new Insets(15));

        tab.setContent(root);
        return tab;
    }

    // 3. Gestionar asignaturas de un estudiante
    private Tab crearTabGestionEstudiante() {
        Tab tab = new Tab("Gestión Estudiante");

        Label titulo = new Label("Gestionar asignaturas de un estudiante (adicionar, retirar, cambiar)");

        // ---- VER CLASES DEL ESTUDIANTE ----
        TextField txtIdEstVer = new TextField();
        txtIdEstVer.setPromptText("Id estudiante (E001, E002, ...)");
        Button btnVer = new Button("Ver clases");
        Label lblEstVer = new Label();
        ListView<String> listaClasesEst = new ListView<>();
        Label lblMensajeVer = new Label();

        btnVer.setOnAction(e -> {
            lblEstVer.setText("");
            lblMensajeVer.setText("");
            listaClasesEst.getItems().clear();

            String id = txtIdEstVer.getText().trim();
            if (id.isEmpty()) {
                lblMensajeVer.setText("Debe ingresar el id del estudiante.");
                return;
            }

            Estudiante est = buscarEstudiantePorId(id);
            if (est == null) {
                lblMensajeVer.setText("No se encontró estudiante con id: " + id);
                return;
            }

            lblEstVer.setText("Estudiante: " + est.getNombre() + " (" + est.getIdEstudiante() + ")");

            List<Clase> clases = obtenerClasesEstudiante(est);
            if (clases.isEmpty()) {
                lblMensajeVer.setText("El estudiante no tiene clases inscritas.");
            } else {
                ObservableList<String> items = FXCollections.observableArrayList();
                for (Clase c : clases) {
                    items.add(formatearClase(c));
                }
                listaClasesEst.setItems(items);
            }
        });

        HBox filaVer = new HBox(10, new Label("Id estudiante:"), txtIdEstVer, btnVer);

        // ---- ADICIONAR CLASE ----
        TextField txtIdEstAdd = new TextField();
        txtIdEstAdd.setPromptText("Id estudiante");
        TextField txtIdClaseAdd = new TextField();
        txtIdClaseAdd.setPromptText("Id clase (C001, C002, ...)");
        Button btnAdd = new Button("Adicionar");
        Label lblMsgAdd = new Label();

        btnAdd.setOnAction(e -> {
            String msg = adicionarClaseAEstudiante(txtIdEstAdd.getText().trim(),
                    txtIdClaseAdd.getText().trim());
            lblMsgAdd.setText(msg);
        });

        HBox filaAdd = new HBox(10,
                new Label("Id estudiante:"), txtIdEstAdd,
                new Label("Id clase:"), txtIdClaseAdd,
                btnAdd
        );

        // ---- RETIRAR CLASE ----
        TextField txtIdEstRet = new TextField();
        txtIdEstRet.setPromptText("Id estudiante");
        TextField txtIdClaseRet = new TextField();
        txtIdClaseRet.setPromptText("Id clase");
        Button btnRet = new Button("Retirar");
        Label lblMsgRet = new Label();

        btnRet.setOnAction(e -> {
            String msg = retirarClaseDeEstudiante(txtIdEstRet.getText().trim(),
                    txtIdClaseRet.getText().trim());
            lblMsgRet.setText(msg);
        });

        HBox filaRet = new HBox(10,
                new Label("Id estudiante:"), txtIdEstRet,
                new Label("Id clase:"), txtIdClaseRet,
                btnRet
        );

        // ---- CAMBIAR CLASE ----
        TextField txtIdEstCamb = new TextField();
        txtIdEstCamb.setPromptText("Id estudiante");
        TextField txtIdClaseAct = new TextField();
        txtIdClaseAct.setPromptText("Id clase actual");
        TextField txtIdClaseNueva = new TextField();
        txtIdClaseNueva.setPromptText("Id clase nueva");
        Button btnCamb = new Button("Cambiar");
        Label lblMsgCamb = new Label();

        btnCamb.setOnAction(e -> {
            String msg = cambiarClaseDeEstudiante(
                    txtIdEstCamb.getText().trim(),
                    txtIdClaseAct.getText().trim(),
                    txtIdClaseNueva.getText().trim()
            );
            lblMsgCamb.setText(msg);
        });

        HBox filaCamb = new HBox(10,
                new Label("Id estudiante:"), txtIdEstCamb,
                new Label("Clase actual:"), txtIdClaseAct,
                new Label("Clase nueva:"), txtIdClaseNueva,
                btnCamb
        );

        // ---- LISTA DE TODAS LAS CLASES DISPONIBLES ----
        ListView<String> listaClasesSistema = new ListView<>();
        ObservableList<String> itemsClases = FXCollections.observableArrayList();
        for (Asignatura a : asignaturas) {
            if (a.getClases() == null) continue;
            for (Clase c : a.getClases()) {
                itemsClases.add(formatearClase(c) + " | Asig: " + a.getCodigoAsignatura());
            }
        }
        listaClasesSistema.setItems(itemsClases);

        VBox root = new VBox(10,
                titulo,
                new Label("---- Ver clases de un estudiante ----"),
                filaVer,
                lblEstVer,
                lblMensajeVer,
                listaClasesEst,
                new Label("---- Adicionar clase ----"),
                filaAdd,
                lblMsgAdd,
                new Label("---- Retirar clase ----"),
                filaRet,
                lblMsgRet,
                new Label("---- Cambiar clase ----"),
                filaCamb,
                lblMsgCamb,
                new Label("---- Clases disponibles en el sistema ----"),
                listaClasesSistema
        );
        root.setPadding(new Insets(15));

        tab.setContent(root);
        return tab;
    }

    // 4. Consultar estudiantes inscritos en una asignatura
    private Tab crearTabEstudiantesAsignatura() {
        Tab tab = new Tab("Estudiantes por Asig.");

        Label titulo = new Label("Consultar estudiantes inscritos en una asignatura específica");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código de la asignatura");

        Button btnBuscar = new Button("Buscar");

        Label lblInfoAsig = new Label();
        Label lblMensaje = new Label();

        ListView<String> listaEstudiantes = new ListView<>();

        btnBuscar.setOnAction(e -> {
            lblInfoAsig.setText("");
            lblMensaje.setText("");
            listaEstudiantes.getItems().clear();

            String cod = txtCodigo.getText().trim();
            if (cod.isEmpty()) {
                lblMensaje.setText("Debe ingresar un código de asignatura.");
                return;
            }

            Asignatura asig = buscarAsignaturaPorCodigo(cod);
            if (asig == null) {
                lblMensaje.setText("No se encontró la asignatura con código: " + cod);
                return;
            }

            lblInfoAsig.setText("Asignatura: " + asig.getCodigoAsignatura()
                    + " - " + asig.getNombreAsignatura());

            List<Estudiante> ests = obtenerEstudiantesDeAsignatura(asig);
            if (ests.isEmpty()) {
                lblMensaje.setText("No hay estudiantes inscritos en esta asignatura.");
            } else {
                ObservableList<String> items = FXCollections.observableArrayList();
                for (Estudiante est : ests) {
                    items.add(est.getIdEstudiante() + " | " + est.getNombre()
                            + " | " + est.getEmail());
                }
                listaEstudiantes.setItems(items);
            }
        });

        HBox filaEntrada = new HBox(10, new Label("Código:"), txtCodigo, btnBuscar);
        filaEntrada.setPadding(new Insets(10));

        VBox root = new VBox(10,
                titulo,
                filaEntrada,
                lblInfoAsig,
                lblMensaje,
                listaEstudiantes
        );
        root.setPadding(new Insets(15));

        tab.setContent(root);
        return tab;
    }

    // 5. Consultar asignaturas por departamento
    private Tab crearTabAsignaturasPorDepartamento() {
        Tab tab = new Tab("Asig. por Depto.");

        Label titulo = new Label("Consultar las asignaturas ofrecidas por un departamento específico");

        TextField txtIdDep = new TextField();
        txtIdDep.setPromptText("Id departamento (1, 2, ...)");

        Button btnBuscar = new Button("Buscar");

        Label lblInfoDep = new Label();
        Label lblMensaje = new Label();

        ListView<String> listaAsignaturas = new ListView<>();

        // Mostrar también los deptos disponibles
        ListView<String> listaDepartamentos = new ListView<>();
        ObservableList<String> itemsDep = FXCollections.observableArrayList();
        for (Departamento d : departamentos) {
            itemsDep.add(d.getIdDepartamento() + " - " + d.getNombreDepartamento());
        }
        listaDepartamentos.setItems(itemsDep);

        btnBuscar.setOnAction(e -> {
            lblInfoDep.setText("");
            lblMensaje.setText("");
            listaAsignaturas.getItems().clear();

            String txt = txtIdDep.getText().trim();
            if (txt.isEmpty()) {
                lblMensaje.setText("Debe ingresar el id del departamento.");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(txt);
            } catch (NumberFormatException ex) {
                lblMensaje.setText("El id del departamento debe ser un número entero.");
                return;
            }

            Departamento dep = buscarDepartamentoPorId(id);
            if (dep == null) {
                lblMensaje.setText("No se encontró departamento con id: " + id);
                return;
            }

            lblInfoDep.setText("Departamento: " + dep.getNombreDepartamento()
                    + " (id " + dep.getIdDepartamento() + ")");

            List<Asignatura> asigs = dep.getAsignaturas();
            if (asigs == null || asigs.isEmpty()) {
                lblMensaje.setText("El departamento no tiene asignaturas registradas.");
            } else {
                ObservableList<String> items = FXCollections.observableArrayList();
                for (Asignatura a : asigs) {
                    items.add(a.getCodigoAsignatura() + " - " + a.getNombreAsignatura());
                }
                listaAsignaturas.setItems(items);
            }
        });

        HBox filaEntrada = new HBox(10, new Label("Id depto:"), txtIdDep, btnBuscar);
        filaEntrada.setPadding(new Insets(10));

        VBox root = new VBox(10,
                titulo,
                new Label("Departamentos disponibles:"),
                listaDepartamentos,
                filaEntrada,
                lblInfoDep,
                lblMensaje,
                new Label("Asignaturas del departamento:"),
                listaAsignaturas
        );
        root.setPadding(new Insets(15));

        tab.setContent(root);
        return tab;
    }

    // 6. Consultar información de un profesor
    private Tab crearTabInfoProfesor() {
        Tab tab = new Tab("Info Profesor");

        Label titulo = new Label("Consultar información de un profesor");

        TextField txtId = new TextField();
        txtId.setPromptText("id_Profesor o codDocente");

        Button btnBuscar = new Button("Buscar");

        Label lblInfo = new Label();
        Label lblMensaje = new Label();

        btnBuscar.setOnAction(e -> {
            lblInfo.setText("");
            lblMensaje.setText("");

            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                lblMensaje.setText("Debe ingresar el id del profesor.");
                return;
            }

            Profesor p = buscarProfesorPorIdOCod(id);
            if (p == null) {
                lblMensaje.setText("El profesor no existe.");
            } else {
                lblInfo.setText("Nombre: " + p.getNombre()
                        + " | Correo: " + p.getEmail()
                        + " | id_Profesor: " + p.getId_Profesor()
                        + " | codDocente: " + p.getCodDocente()
                        + " | Horas semanales: " + p.getHorasSemanales()
                        + " | Sueldo: " + p.getSueldo());
            }
        });

        HBox fila = new HBox(10, new Label("Id profesor:"), txtId, btnBuscar);
        fila.setPadding(new Insets(10));

        VBox root = new VBox(10,
                titulo,
                fila,
                lblMensaje,
                lblInfo
        );
        root.setPadding(new Insets(15));

        tab.setContent(root);
        return tab;
    }

    // 7. Crear nuevas asignaturas
    private Tab crearTabCrearAsignatura() {
        Tab tab = new Tab("Crear Asignatura");

        Label titulo = new Label("Crear nueva asignatura");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código (único)");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre");

        TextField txtCreditos = new TextField();
        txtCreditos.setPromptText("Créditos (número)");

        CheckBox chkIngles = new CheckBox("Requiere examen de inglés");

        TextField txtIdDep = new TextField();
        txtIdDep.setPromptText("Id departamento (opcional)");

        Button btnCrear = new Button("Crear");

        Label lblMensaje = new Label();

        btnCrear.setOnAction(e -> {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String credTxt = txtCreditos.getText().trim();
            String depTxt = txtIdDep.getText().trim();
            boolean reqIngles = chkIngles.isSelected();

            lblMensaje.setText("");

            if (codigo.isEmpty() || nombre.isEmpty() || credTxt.isEmpty()) {
                lblMensaje.setText("Código, nombre y créditos son obligatorios.");
                return;
            }

            int creditos;
            try {
                creditos = Integer.parseInt(credTxt);
            } catch (NumberFormatException ex) {
                lblMensaje.setText("Los créditos deben ser un número entero.");
                return;
            }

            if (buscarAsignaturaPorCodigo(codigo) != null) {
                lblMensaje.setText("Ya existe una asignatura con el código " + codigo);
                return;
            }

            Asignatura nueva = new Asignatura(codigo, nombre, creditos, reqIngles);
            asignaturas.add(nueva);

            if (!depTxt.isEmpty()) {
                try {
                    int idDep = Integer.parseInt(depTxt);
                    Departamento dep = buscarDepartamentoPorId(idDep);
                    if (dep != null) {
                        dep.getAsignaturas().add(nueva);
                        nueva.setDepartamento(dep);
                    } else {
                        lblMensaje.setText("Asignatura creada, pero no se encontró el departamento con id " + idDep);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    lblMensaje.setText("Asignatura creada, pero el id de departamento no es numérico.");
                    return;
                }
            }

            lblMensaje.setText("Asignatura creada correctamente: "
                    + nueva.getCodigoAsignatura() + " - " + nueva.getNombreAsignatura());
        });

        VBox root = new VBox(10,
                titulo,
                new HBox(10, new Label("Código:"), txtCodigo),
                new HBox(10, new Label("Nombre:"), txtNombre),
                new HBox(10, new Label("Créditos:"), txtCreditos),
                chkIngles,
                new HBox(10, new Label("Id departamento:"), txtIdDep),
                btnCrear,
                lblMensaje
        );
        root.setPadding(new Insets(15));

        tab.setContent(root);
        return tab;
    }

    // ============================================================
    // ===============  LÓGICA / "CONTROLADOR"  ===================
    // ============================================================

    private void inicializarDatosEjemplo() {
        profesores = new ArrayList<>();
        asignaturas = new ArrayList<>();
        estudiantes = new ArrayList<>();
        departamentos = new ArrayList<>();

        // Profesores
        Profesor p1 = new Profesor("juan.perez@uni.edu", "Juan Perez",
                16, 3000000, "DOC001", "P001");
        Profesor p2 = new Profesor("ana.gomez@uni.edu", "Ana Gomez",
                12, 2800000, "DOC002", "P002");
        profesores.add(p1);
        profesores.add(p2);

        // Asignaturas
        Asignatura a1 = new Asignatura("ADS101", "Analisis y Diseno de SW", 3, false);
        Asignatura a2 = new Asignatura("BD102", "Bases de Datos", 4, false);
        Asignatura a3 = new Asignatura("RED103", "Redes de Computadores", 3, false);
        asignaturas.add(a1);
        asignaturas.add(a2);
        asignaturas.add(a3);

        // Clases
        Clase c1 = new Clase("C001", "2025-1", "Lu-Mi", "8-10", "A101", 40, p1);
        Clase c2 = new Clase("C002", "2025-1", "Ma-Ju", "10-12", "A102", 35, p1);
        Clase c3 = new Clase("C003", "2025-2", "Lu-Mi", "14-16", "B201", 30, p1);
        Clase c4 = new Clase("C004", "2025-1", "Ma-Ju", "8-10", "B202", 40, p2);

        a1.getClases().add(c1);
        a2.getClases().add(c2);
        a3.getClases().add(c3);
        a1.getClases().add(c4);

        // Estudiantes
        Estudiante e1 = new Estudiante("Carlos Lopez", "E001",
                "carlos@uni.edu", "Ingenieria de Sistemas", true);
        Estudiante e2 = new Estudiante("Maria Ruiz", "E002",
                "maria@uni.edu", "Ingenieria Industrial", false);
        estudiantes.add(e1);
        estudiantes.add(e2);

        // inscripciones
        c1.getEstudiantesInscritos().add(e1);
        c2.getEstudiantesInscritos().add(e1);
        c1.getEstudiantesInscritos().add(e2);
        c3.getEstudiantesInscritos().add(e2);

        // Departamentos
        Departamento depSis = new Departamento(1, "Ingenieria de Sistemas");
        depSis.getAsignaturas().add(a1);
        depSis.getAsignaturas().add(a2);

        Departamento depTele = new Departamento(2, "Telematica");
        depTele.getAsignaturas().add(a3);

        departamentos.add(depSis);
        departamentos.add(depTele);

        // Enlazar asignaturas con departamento (solo para mostrar en info)
        a1.setDepartamento(depSis);
        a2.setDepartamento(depSis);
        a3.setDepartamento(depTele);
    }

    private Profesor buscarProfesorPorIdOCod(String id) {
        if (id == null) return null;
        for (Profesor p : profesores) {
            if (id.equals(p.getId_Profesor()) || id.equals(p.getCodDocente())) {
                return p;
            }
        }
        return null;
    }

    private Asignatura buscarAsignaturaPorCodigo(String codigo) {
        if (codigo == null) return null;
        for (Asignatura a : asignaturas) {
            if (codigo.equals(a.getCodigoAsignatura())) {
                return a;
            }
        }
        return null;
    }

    private Estudiante buscarEstudiantePorId(String id) {
        if (id == null) return null;
        for (Estudiante e : estudiantes) {
            if (id.equals(e.getIdEstudiante())) {
                return e;
            }
        }
        return null;
    }

    private Departamento buscarDepartamentoPorId(int id) {
        for (Departamento d : departamentos) {
            if (d.getIdDepartamento() == id) {
                return d;
            }
        }
        return null;
    }

    private List<Asignatura> obtenerAsignaturasProfesorSemestre(Profesor profesor, String semestre) {
        List<Asignatura> resultado = new ArrayList<>();
        if (profesor == null || semestre == null) return resultado;

        String idProf = profesor.getId_Profesor();
        for (Asignatura a : asignaturas) {
            if (a.getClases() == null) continue;
            boolean agrega = false;
            for (Clase c : a.getClases()) {
                if (c == null || c.getProfesor() == null) continue;
                if (idProf.equals(c.getProfesor().getId_Profesor())
                        && semestre.equals(c.getSemestre())) {
                    agrega = true;
                    break;
                }
            }
            if (agrega) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    private List<Clase> obtenerClasesEstudiante(Estudiante estudiante) {
        List<Clase> resultado = new ArrayList<>();
        if (estudiante == null) return resultado;

        for (Asignatura a : asignaturas) {
            if (a.getClases() == null) continue;
            for (Clase c : a.getClases()) {
                if (c.getEstudiantesInscritos() != null &&
                        c.getEstudiantesInscritos().contains(estudiante)) {
                    resultado.add(c);
                }
            }
        }
        return resultado;
    }

    private Clase buscarClasePorId(String idClase) {
        if (idClase == null) return null;
        for (Asignatura a : asignaturas) {
            if (a.getClases() == null) continue;
            for (Clase c : a.getClases()) {
                if (idClase.equals(c.getIdClase())) {
                    return c;
                }
            }
        }
        return null;
    }

    private String adicionarClaseAEstudiante(String idEstudiante, String idClase) {
        Estudiante est = buscarEstudiantePorId(idEstudiante);
        if (est == null) {
            return "No se encontró estudiante con id " + idEstudiante;
        }

        Clase clase = buscarClasePorId(idClase);
        if (clase == null) {
            return "No se encontró clase con id " + idClase;
        }

        if (clase.getEstudiantesInscritos().contains(est)) {
            return "El estudiante ya está inscrito en esa clase.";
        }

        if (clase.getEstudiantesInscritos().size() >= clase.getCupoMaximo()) {
            return "La clase está llena; no se puede adicionar.";
        }

        clase.getEstudiantesInscritos().add(est);
        return "Se adicionó la clase " + clase.getIdClase()
                + " al estudiante " + est.getNombre();
    }

    private String retirarClaseDeEstudiante(String idEstudiante, String idClase) {
        Estudiante est = buscarEstudiantePorId(idEstudiante);
        if (est == null) {
            return "No se encontró estudiante con id " + idEstudiante;
        }

        Clase clase = buscarClasePorId(idClase);
        if (clase == null) {
            return "No se encontró clase con id " + idClase;
        }

        if (!clase.getEstudiantesInscritos().contains(est)) {
            return "El estudiante no está inscrito en esa clase.";
        }

        clase.getEstudiantesInscritos().remove(est);
        return "Se retiró la clase " + clase.getIdClase()
                + " del estudiante " + est.getNombre();
    }

    private String cambiarClaseDeEstudiante(String idEstudiante,
                                            String idClaseActual,
                                            String idClaseNueva) {

        Estudiante est = buscarEstudiantePorId(idEstudiante);
        if (est == null) {
            return "No se encontró estudiante con id " + idEstudiante;
        }

        Clase claseActual = buscarClasePorId(idClaseActual);
        if (claseActual == null) {
            return "No se encontró la clase actual con id " + idClaseActual;
        }

        Clase claseNueva = buscarClasePorId(idClaseNueva);
        if (claseNueva == null) {
            return "No se encontró la clase nueva con id " + idClaseNueva;
        }

        if (!claseActual.getEstudiantesInscritos().contains(est)) {
            return "El estudiante no está inscrito en la clase actual.";
        }package com.example.proyectoads;

import Modelo.*;
import Serializacion.Serializacion; // Asegurate de tener esta clase o usar la logica interna
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

        public class MainFX extends Application {

            // ====== DATOS EN MEMORIA ======
            private List<Profesor> profesores;
            private List<Asignatura> asignaturas;
            private List<Estudiante> estudiantes;
            private List<Departamento> departamentos;

            // Rutas de archivos para persistencia
            private final String FILE_PROFESORES = "datos/profesores.json";
            private final String FILE_ASIGNATURAS = "datos/asignaturas.json";
            private final String FILE_ESTUDIANTES = "datos/estudiantes.json";
            private final String FILE_DEPARTAMENTOS = "datos/departamentos.json";

            @Override
            public void start(Stage primaryStage) {
                // 1. Cargar datos al iniciar
                cargarDatos();

                // Si no hay datos (primera vez), cargar ejemplos
                if (profesores.isEmpty() && asignaturas.isEmpty()) {
                    inicializarDatosEjemplo();
                }

                TabPane tabPane = new TabPane();

                tabPane.getTabs().addAll(
                        crearTabProfesorSemestre(),
                        crearTabInfoAsignatura(),
                        crearTabGestionEstudiante(),
                        crearTabEstudiantesAsignatura(),
                        crearTabAsignaturasPorDepartamento(),
                        crearTabInfoProfesor(),
                        crearTabCrearAsignatura()
                );

                tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

                Scene scene = new Scene(tabPane, 950, 650);
                primaryStage.setTitle("Sistema de Gestión de Asignaturas - JavaFX");
                primaryStage.setScene(scene);

                // 2. Guardar datos al cerrar la ventana
                primaryStage.setOnCloseRequest(event -> {
                    guardarDatos();
                    System.out.println("Datos guardados correctamente al salir.");
                });

                primaryStage.show();
            }

            public static void main(String[] args) {
                launch(args);
            }

            // ============================================================
            // ==================== PERSISTENCIA (JSON) ===================
            // ============================================================

            private void guardarDatos() {
                crearDirectorioDatos();
                guardarListaJson(profesores, FILE_PROFESORES);
                guardarListaJson(asignaturas, FILE_ASIGNATURAS);
                guardarListaJson(estudiantes, FILE_ESTUDIANTES);
                guardarListaJson(departamentos, FILE_DEPARTAMENTOS);
            }

            private void cargarDatos() {
                profesores = cargarListaJson(FILE_PROFESORES, new TypeToken<List<Profesor>>(){}.getType());
                asignaturas = cargarListaJson(FILE_ASIGNATURAS, new TypeToken<List<Asignatura>>(){}.getType());
                estudiantes = cargarListaJson(FILE_ESTUDIANTES, new TypeToken<List<Estudiante>>(){}.getType());
                departamentos = cargarListaJson(FILE_DEPARTAMENTOS, new TypeToken<List<Departamento>>(){}.getType());

                // Inicializar listas si falló la carga
                if (profesores == null) profesores = new ArrayList<>();
                if (asignaturas == null) asignaturas = new ArrayList<>();
                if (estudiantes == null) estudiantes = new ArrayList<>();
                if (departamentos == null) departamentos = new ArrayList<>();

                // Reconstruir relaciones (enlaces) si es necesario,
                // ya que JSON guarda copias, no referencias.
                // Para un proyecto académico simple, esto puede dejarse así,
                // pero idealmente deberías reconectar los objetos por ID.
            }

            private void crearDirectorioDatos() {
                File dir = new File("datos");
                if (!dir.exists()) {
                    dir.mkdir();
                }
            }

            private <T> void guardarListaJson(List<T> lista, String ruta) {
                try (Writer writer = new FileWriter(ruta)) {
                    Gson gson = new Gson();
                    gson.toJson(lista, writer);
                } catch (IOException e) {
                    System.err.println("Error guardando en " + ruta + ": " + e.getMessage());
                }
            }

            private <T> List<T> cargarListaJson(String ruta, Type tipoLista) {
                File archivo = new File(ruta);
                if (!archivo.exists()) return new ArrayList<>();

                try (Reader reader = new FileReader(ruta)) {
                    Gson gson = new Gson();
                    return gson.fromJson(reader, tipoLista);
                } catch (IOException e) {
                    System.err.println("Error cargando de " + ruta + ": " + e.getMessage());
                    return new ArrayList<>();
                }
            }

            // ============================================================
            // ================  TABS / CASOS DE USO  =====================
            // ============================================================

            // 1. Consultar asignaturas por profesor y semestre
            private Tab crearTabProfesorSemestre() {
                Tab tab = new Tab("Prof. vs Semestre");
                Label titulo = new Label("Consultar asignaturas dictadas por un profesor en un semestre");
                titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                TextField txtIdProfesor = new TextField();
                txtIdProfesor.setPromptText("ID Profesor o CodDocente");

                TextField txtSemestre = new TextField();
                txtSemestre.setPromptText("Semestre (ej: 2025-1)");

                Button btnBuscar = new Button("Buscar");
                Label lblProfesor = new Label();
                Label lblMensaje = new Label();
                lblMensaje.setStyle("-fx-text-fill: red;");

                ListView<String> listaAsignaturas = new ListView<>();

                btnBuscar.setOnAction(e -> {
                    lblProfesor.setText("");
                    lblMensaje.setText("");
                    listaAsignaturas.getItems().clear();

                    String id = txtIdProfesor.getText().trim();
                    String semestre = txtSemestre.getText().trim();

                    if (id.isEmpty() || semestre.isEmpty()) {
                        lblMensaje.setText("Debe ingresar ambos campos.");
                        return;
                    }

                    Profesor profesor = buscarProfesorPorIdOCod(id);
                    if (profesor == null) {
                        lblMensaje.setText("Profesor no encontrado.");
                        return;
                    }

                    List<Asignatura> resultado = obtenerAsignaturasProfesorSemestre(profesor, semestre);
                    lblProfesor.setText("Profesor: " + profesor.getNombre());

                    if (resultado.isEmpty()) {
                        lblMensaje.setText("No se encontraron asignaturas para ese semestre.");
                    } else {
                        ObservableList<String> items = FXCollections.observableArrayList();
                        for (Asignatura a : resultado) {
                            items.add(a.getCodigoAsignatura() + " - " + a.getNombreAsignatura());
                        }
                        listaAsignaturas.setItems(items);
                    }
                });

                VBox root = new VBox(10, titulo, new HBox(10, new Label("ID Prof:"), txtIdProfesor, new Label("Semestre:"), txtSemestre, btnBuscar), lblProfesor, lblMensaje, listaAsignaturas);
                root.setPadding(new Insets(15));
                tab.setContent(root);
                return tab;
            }

            // 2. Consultar información de una asignatura
            private Tab crearTabInfoAsignatura() {
                Tab tab = new Tab("Info Asignatura");
                Label titulo = new Label("Información detallada de Asignatura");
                titulo.setStyle("-fx-font-weight: bold;");

                TextField txtCodigo = new TextField();
                txtCodigo.setPromptText("Código (ej: ADS101)");
                Button btnBuscar = new Button("Buscar");

                TextArea areaInfo = new TextArea();
                areaInfo.setEditable(false);
                areaInfo.setPrefHeight(300);

                btnBuscar.setOnAction(e -> {
                    areaInfo.clear();
                    String codigo = txtCodigo.getText().trim();
                    if (codigo.isEmpty()) {
                        areaInfo.setText("Ingrese un código.");
                        return;
                    }

                    Asignatura a = buscarAsignaturaPorCodigo(codigo);
                    if (a == null) {
                        areaInfo.setText("Asignatura no encontrada.");
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    String dep = (a.getDepartamento() != null) ? a.getDepartamento().getNombreDepartamento() : "Sin departamento";

                    sb.append("Código: ").append(a.getCodigoAsignatura()).append("\n");
                    sb.append("Nombre: ").append(a.getNombreAsignatura()).append("\n");
                    sb.append("Créditos: ").append(a.getCreditos()).append("\n");
                    sb.append("Departamento: ").append(dep).append("\n");
                    sb.append("------------------------------------------------\n");
                    sb.append("CLASES DISPONIBLES:\n");

                    if (a.getClases() != null) {
                        for (Clase c : a.getClases()) {
                            String prof = (c.getProfesor() != null) ? c.getProfesor().getNombre() : "Sin asignar";
                            sb.append(String.format(" - ID: %s | Sem: %s | Horario: %s %s | Prof: %s\n",
                                    c.getIdClase(), c.getSemestre(), c.getDias(), c.getHoras(), prof));
                        }
                    }
                    areaInfo.setText(sb.toString());
                });

                VBox root = new VBox(10, titulo, new HBox(10, new Label("Código:"), txtCodigo, btnBuscar), areaInfo);
                root.setPadding(new Insets(15));
                tab.setContent(root);
                return tab;
            }

            // 3. Gestión de Estudiantes (Adicionar/Retirar Clases)
            private Tab crearTabGestionEstudiante() {
                Tab tab = new Tab("Gestión Inscripciones");
                Label titulo = new Label("Inscripción de Materias");
                titulo.setStyle("-fx-font-weight: bold;");

                // Ver Clases
                TextField txtIdEst = new TextField();
                txtIdEst.setPromptText("ID Estudiante");
                Button btnVer = new Button("Ver Inscritas");
                ListView<String> listaInscritas = new ListView<>();

                // Acciones
                TextField txtIdClase = new TextField();
                txtIdClase.setPromptText("ID Clase");
                Button btnInscribir = new Button("Inscribir");
                Button btnRetirar = new Button("Retirar");
                Label lblEstado = new Label();

                // Lógica Ver
                btnVer.setOnAction(e -> {
                    listaInscritas.getItems().clear();
                    String id = txtIdEst.getText().trim();
                    Estudiante est = buscarEstudiantePorId(id);
                    if (est == null) {
                        lblEstado.setText("Estudiante no encontrado.");
                        return;
                    }
                    lblEstado.setText("Estudiante: " + est.getNombre());
                    List<Clase> clases = obtenerClasesEstudiante(est);
                    for (Clase c : clases) {
                        listaInscritas.getItems().add(c.getIdClase() + " - " + c.getSemestre() + " (" + c.getDias() + ")");
                    }
                });

                // Lógica Inscribir
                btnInscribir.setOnAction(e -> {
                    String res = adicionarClaseAEstudiante(txtIdEst.getText().trim(), txtIdClase.getText().trim());
                    lblEstado.setText(res);
                    btnVer.fire(); // Actualizar lista
                });

                // Lógica Retirar
                btnRetirar.setOnAction(e -> {
                    String res = retirarClaseDeEstudiante(txtIdEst.getText().trim(), txtIdClase.getText().trim());
                    lblEstado.setText(res);
                    btnVer.fire(); // Actualizar lista
                });

                VBox root = new VBox(10, titulo,
                        new HBox(10, new Label("Estudiante:"), txtIdEst, btnVer),
                        listaInscritas,
                        new Separator(),
                        new HBox(10, new Label("Clase:"), txtIdClase, btnInscribir, btnRetirar),
                        lblEstado
                );
                root.setPadding(new Insets(15));
                tab.setContent(root);
                return tab;
            }

            // 4. Estudiantes por Asignatura
            private Tab crearTabEstudiantesAsignatura() {
                Tab tab = new Tab("Listas de Clase");
                Label titulo = new Label("Estudiantes inscritos en una Asignatura");

                TextField txtCodAsig = new TextField();
                txtCodAsig.setPromptText("Código Asignatura");
                Button btnListar = new Button("Listar Estudiantes");
                ListView<String> listaResultados = new ListView<>();
                Label lblInfo = new Label();

                btnListar.setOnAction(e -> {
                    listaResultados.getItems().clear();
                    String cod = txtCodAsig.getText().trim();
                    Asignatura asig = buscarAsignaturaPorCodigo(cod);

                    if (asig == null) {
                        lblInfo.setText("Asignatura no encontrada.");
                        return;
                    }

                    List<Estudiante> inscritos = obtenerEstudiantesDeAsignatura(asig);
                    lblInfo.setText("Total inscritos: " + inscritos.size());

                    if (inscritos.isEmpty()) {
                        listaResultados.getItems().add("No hay estudiantes inscritos.");
                    } else {
                        for (Estudiante est : inscritos) {
                            listaResultados.getItems().add(est.getIdEstudiante() + " - " + est.getNombre() + " (" + est.getEmail() + ")");
                        }
                    }
                });

                VBox root = new VBox(10, titulo, new HBox(10, txtCodAsig, btnListar), lblInfo, listaResultados);
                root.setPadding(new Insets(15));
                tab.setContent(root);
                return tab;
            }

            // 5. Asignaturas por Departamento
            private Tab crearTabAsignaturasPorDepartamento() {
                Tab tab = new Tab("Departamentos");
                TextField txtIdDep = new TextField();
                txtIdDep.setPromptText("ID Departamento (Numérico)");
                Button btnBuscar = new Button("Consultar");
                ListView<String> lista = new ListView<>();
                Label lblError = new Label();

                btnBuscar.setOnAction(e -> {
                    lista.getItems().clear();
                    lblError.setText("");
                    try {
                        int id = Integer.parseInt(txtIdDep.getText().trim());
                        Departamento dep = buscarDepartamentoPorId(id);
                        if (dep == null) {
                            lblError.setText("Departamento no encontrado.");
                            return;
                        }
                        lista.getItems().add("Departamento: " + dep.getNombreDepartamento());
                        if (dep.getAsignaturas() != null) {
                            for (Asignatura a : dep.getAsignaturas()) {
                                lista.getItems().add(" > " + a.getCodigoAsignatura() + " - " + a.getNombreAsignatura());
                            }
                        }
                    } catch (NumberFormatException ex) {
                        lblError.setText("El ID debe ser numérico.");
                    }
                });

                VBox root = new VBox(10, new Label("Consulta por Departamento"), new HBox(10, txtIdDep, btnBuscar), lblError, lista);
                root.setPadding(new Insets(15));
                tab.setContent(root);
                return tab;
            }

            // 6. Información Profesor
            private Tab crearTabInfoProfesor() {
                Tab tab = new Tab("Info Profesor");
                TextField txtId = new TextField();
                txtId.setPromptText("ID Profesor");
                Button btnBuscar = new Button("Buscar");
                TextArea area = new TextArea();
                area.setEditable(false);

                btnBuscar.setOnAction(e -> {
                    Profesor p = buscarProfesorPorIdOCod(txtId.getText().trim());
                    if (p != null) {
                        area.setText("Nombre: " + p.getNombre() + "\nEmail: " + p.getEmail() +
                                "\nSueldo: " + p.getSueldo() + "\nHoras: " + p.getHorasSemanales());
                    } else {
                        area.setText("Profesor no encontrado.");
                    }
                });

                VBox root = new VBox(10, new Label("Información de Profesor"), new HBox(10, txtId, btnBuscar), area);
                root.setPadding(new Insets(15));
                tab.setContent(root);
                return tab;
            }

            // 7. Crear Asignatura
            private Tab crearTabCrearAsignatura() {
                Tab tab = new Tab("Crear Asignatura");
                TextField txtCod = new TextField(); txtCod.setPromptText("Código");
                TextField txtNom = new TextField(); txtNom.setPromptText("Nombre");
                TextField txtCred = new TextField(); txtCred.setPromptText("Créditos");
                CheckBox chkIngles = new CheckBox("Requiere Inglés");
                TextField txtDep = new TextField(); txtDep.setPromptText("ID Departamento (Opcional)");
                Button btnCrear = new Button("Guardar Asignatura");
                Label lblRes = new Label();

                btnCrear.setOnAction(e -> {
                    try {
                        String codigo = txtCod.getText().trim();
                        if (buscarAsignaturaPorCodigo(codigo) != null) {
                            lblRes.setText("Error: El código ya existe.");
                            return;
                        }
                        int creditos = Integer.parseInt(txtCred.getText().trim());

                        Asignatura nueva = new Asignatura(codigo, txtNom.getText().trim(), creditos, chkIngles.isSelected());
                        asignaturas.add(nueva);

                        if (!txtDep.getText().isEmpty()) {
                            Departamento d = buscarDepartamentoPorId(Integer.parseInt(txtDep.getText().trim()));
                            if (d != null) {
                                d.getAsignaturas().add(nueva);
                                nueva.setDepartamento(d);
                                lblRes.setText("Asignatura creada y vinculada a " + d.getNombreDepartamento());
                            } else {
                                lblRes.setText("Asignatura creada (Depto no encontrado).");
                            }
                        } else {
                            lblRes.setText("Asignatura creada exitosamente.");
                        }
                        // Limpiar campos
                        txtCod.clear(); txtNom.clear(); txtCred.clear(); txtDep.clear();
                    } catch (Exception ex) {
                        lblRes.setText("Error: Verifique los datos numéricos.");
                    }
                });

                VBox root = new VBox(10, new Label("Nueva Asignatura"), txtCod, txtNom, txtCred, chkIngles, txtDep, btnCrear, lblRes);
                root.setPadding(new Insets(20));
                tab.setContent(root);
                return tab;
            }

            // ============================================================
            // =================  LOGICA DE NEGOCIO  ======================
            // ============================================================

            private void inicializarDatosEjemplo() {
                // Solo se llama si no hay datos cargados
                Profesor p1 = new Profesor("juan@uni.edu", "Juan Perez", 16, 3000, "DOC1", "P1");
                profesores.add(p1);

                Asignatura a1 = new Asignatura("MAT1", "Matematicas", 3, false);
                asignaturas.add(a1);

                Clase c1 = new Clase("C1", "2025-1", "Lun-Mie", "8-10", "101", 30, p1);
                a1.getClases().add(c1);

                Estudiante e1 = new Estudiante("Carlos", "E1", "carlos@mail.com", "Sistemas", true);
                estudiantes.add(e1);
            }

            private Profesor buscarProfesorPorIdOCod(String id) {
                if (id == null) return null;
                return profesores.stream()
                        .filter(p -> id.equals(p.getId_Profesor()) || id.equals(p.getCodDocente()))
                        .findFirst().orElse(null);
            }

            private Asignatura buscarAsignaturaPorCodigo(String codigo) {
                if (codigo == null) return null;
                return asignaturas.stream()
                        .filter(a -> codigo.equals(a.getCodigoAsignatura()))
                        .findFirst().orElse(null);
            }

            private Estudiante buscarEstudiantePorId(String id) {
                if (id == null) return null;
                return estudiantes.stream()
                        .filter(e -> id.equals(e.getIdEstudiante()))
                        .findFirst().orElse(null);
            }

            private Departamento buscarDepartamentoPorId(int id) {
                return departamentos.stream()
                        .filter(d -> d.getIdDepartamento() == id)
                        .findFirst().orElse(null);
            }

            private List<Asignatura> obtenerAsignaturasProfesorSemestre(Profesor profesor, String semestre) {
                List<Asignatura> resultado = new ArrayList<>();
                for (Asignatura a : asignaturas) {
                    if (a.getClases() != null) {
                        for (Clase c : a.getClases()) {
                            if (c.getProfesor() != null &&
                                    c.getProfesor().getId_Profesor().equals(profesor.getId_Profesor()) &&
                                    c.getSemestre().equals(semestre)) {
                                resultado.add(a);
                                break;
                            }
                        }
                    }
                }
                return resultado;
            }

            private List<Clase> obtenerClasesEstudiante(Estudiante est) {
                List<Clase> resultado = new ArrayList<>();
                for (Asignatura a : asignaturas) {
                    if (a.getClases() != null) {
                        for (Clase c : a.getClases()) {
                            // Nota: En un sistema real usaríamos equals() en Estudiante,
                            // aquí comparamos IDs para asegurar la persistencia JSON
                            boolean estaInscrito = c.getEstudiantesInscritos().stream()
                                    .anyMatch(e -> e.getIdEstudiante().equals(est.getIdEstudiante()));
                            if (estaInscrito) {
                                resultado.add(c);
                            }
                        }
                    }
                }
                return resultado;
            }

            private List<Estudiante> obtenerEstudiantesDeAsignatura(Asignatura asig) {
                Set<String> idsUnicos = new HashSet<>();
                List<Estudiante> resultado = new ArrayList<>();

                if (asig.getClases() != null) {
                    for (Clase c : asig.getClases()) {
                        for (Estudiante e : c.getEstudiantesInscritos()) {
                            if (idsUnicos.add(e.getIdEstudiante())) { // Evita duplicados si el estudiante esta en 2 clases
                                resultado.add(e);
                            }
                        }
                    }
                }
                return resultado;
            }

            private String adicionarClaseAEstudiante(String idEst, String idClase) {
                Estudiante e = buscarEstudiantePorId(idEst);
                if (e == null) return "Estudiante no existe.";

                // Buscar la clase en todas las asignaturas
                Clase clase = null;
                for(Asignatura a : asignaturas) {
                    if(a.getClases() != null) {
                        for(Clase c : a.getClases()) {
                            if(c.getIdClase().equals(idClase)) {
                                clase = c; break;
                            }
                        }
                    }
                }
                if (clase == null) return "Clase no encontrada.";

                // Validar cupo
                if (clase.getEstudiantesInscritos().size() >= clase.getCupoMaximo()) return "Clase llena.";

                // Validar duplicado
                boolean yaInscrito = clase.getEstudiantesInscritos().stream()
                        .anyMatch(est -> est.getIdEstudiante().equals(e.getIdEstudiante()));

                if (yaInscrito) return "Ya está inscrito.";

                clase.getEstudiantesInscritos().add(e);
                return "Inscripción exitosa.";
            }

            private String retirarClaseDeEstudiante(String idEst, String idClase) {
                Estudiante e = buscarEstudiantePorId(idEst);
                if (e == null) return "Estudiante no existe.";

                boolean borrado = false;
                for(Asignatura a : asignaturas) {
                    if(a.getClases() != null) {
                        for(Clase c : a.getClases()) {
                            if(c.getIdClase().equals(idClase)) {
                                borrado = c.getEstudiantesInscritos().removeIf(est -> est.getIdEstudiante().equals(e.getIdEstudiante()));
                            }
                        }
                    }
                }
                return borrado ? "Retiro exitoso." : "No estaba inscrito o clase no existe.";
            }
        }

        if (claseNueva.getEstudiantesInscritos().contains(est)) {
            return "El estudiante ya está inscrito en la clase nueva.";
        }

        if (claseNueva.getEstudiantesInscritos().size() >= claseNueva.getCupoMaximo()) {
            return "La clase nueva está llena; no se puede cambiar.";
        }

        claseActual.getEstudiantesInscritos().remove(est);
        claseNueva.getEstudiantesInscritos().add(est);

        return "Se cambió la clase " + claseActual.getIdClase()
                + " por la clase " + claseNueva.getIdClase()
                + " para el estudiante " + est.getNombre();
    }

    private List<Estudiante> obtenerEstudiantesDeAsignatura(Asignatura asignatura) {
        List<Estudiante> resultado = new ArrayList<>();
        if (asignatura == null || asignatura.getClases() == null) return resultado;

        Set<String> idsVistos = new HashSet<>();

        for (Clase c : asignatura.getClases()) {
            if (c.getEstudiantesInscritos() == null) continue;
            for (Estudiante e : c.getEstudiantesInscritos()) {
                if (e == null) continue;
                String id = e.getIdEstudiante();
                if (!idsVistos.contains(id)) {
                    idsVistos.add(id);
                    resultado.add(e);
                }
            }
        }
        return resultado;
    }

    private String formatearClase(Clase c) {
        String prof = (c.getProfesor() != null) ? c.getProfesor().getNombre() : "(sin profesor)";
        return "IdClase: " + c.getIdClase()
                + " | Semestre: " + c.getSemestre()
                + " | Días: " + c.getDias()
                + " | Horas: " + c.getHoras()
                + " | Salón: " + c.getSalon()
                + " | Profesor: " + prof;
    }
}
