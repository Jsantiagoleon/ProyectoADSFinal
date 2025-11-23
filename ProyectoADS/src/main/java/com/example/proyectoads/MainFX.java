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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainFX extends Application {

    // ====== DATOS EN MEMORIA ======
    private final List<Profesor> profesores = new ArrayList<>();
    private final List<Asignatura> asignaturas = new ArrayList<>();
    private final List<Estudiante> estudiantes = new ArrayList<>();
    private final List<Departamento> departamentos = new ArrayList<>();

    private static final String CARD_STYLE = "-fx-background-color: rgba(255,255,255,0.9);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 14;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10,0,0,4);";

    @Override
    public void start(Stage primaryStage) {
        inicializarDatosEjemplo();

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f172a, #1e3a8a);" +
                "-fx-padding: 18;");

        Label header = new Label("Sistema de Gestión de Asignaturas");
        header.setTextFill(Color.WHITE);
        header.setFont(Font.font("Inter", FontWeight.BOLD, 26));
        layout.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 12, 6));

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-radius: 12; -fx-padding: 6;" +
                "-fx-background-color: transparent;");

        tabPane.getTabs().addAll(
                crearTabProfesorSemestre(),
                crearTabInfoAsignatura(),
                crearTabGestionEstudiante(),
                crearTabEstudiantesAsignatura(),
                crearTabAsignaturasPorDepartamento(),
                crearTabInfoProfesor(),
                crearTabCrearAsignatura()
        );

        layout.setCenter(tabPane);

        Scene scene = new Scene(layout, 1050, 700);
        scene.getStylesheets().add(MainFX.class.getResource("/com/example/proyectoads/style.css").toExternalForm());
        primaryStage.setTitle("Sistema de Gestión de Asignaturas");
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

        VBox card = crearCardBase("Asignaturas por profesor y semestre",
                "Consulta rápida de materias asignadas en un periodo específico.");

        TextField txtIdProfesor = crearTextField("id_Profesor o codDocente (P001, DOC001, ...)");
        TextField txtSemestre = crearTextField("Semestre (ej: 2025-1)");
        Button btnBuscar = crearBotonPrimario("Buscar");

        Label lblProfesor = crearLabelSecundario();
        Label lblMensaje = crearLabelError();
        ListView<String> listaAsignaturas = new ListView<>();
        listaAsignaturas.setPrefHeight(220);

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
        filaEntrada.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(filaEntrada, lblProfesor, lblMensaje, listaAsignaturas);
        tab.setContent(card);
        return tab;
    }

    // 2. Consultar información de una asignatura
    private Tab crearTabInfoAsignatura() {
        Tab tab = new Tab("Info Asignatura");

        VBox card = crearCardBase("Información de asignatura",
                "Datos completos de la materia, sus clases y docentes.");

        TextField txtCodigo = crearTextField("Código de la asignatura (ADS101, BD102, ...)");
        Button btnBuscar = crearBotonPrimario("Buscar");

        Label lblDatosBasicos = crearLabelSecundario();
        Label lblMensaje = crearLabelError();
        ListView<String> listaClases = new ListView<>();
        listaClases.setPrefHeight(240);

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
        filaEntrada.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(filaEntrada, lblDatosBasicos, lblMensaje,
                new Label("Clases de la asignatura:"), listaClases);
        tab.setContent(card);
        return tab;
    }

    // 3. Gestionar asignaturas de un estudiante
    private Tab crearTabGestionEstudiante() {
        Tab tab = new Tab("Gestión Estudiante");

        VBox card = crearCardBase("Inscripción y gestión de clases",
                "Consulta, inscribe, retira o cambia materias de forma guiada.");

        // ---- VER CLASES DEL ESTUDIANTE ----
        TextField txtIdEstVer = crearTextField("Id estudiante (E001, E002, ...)");
        Button btnVer = crearBotonPrimario("Ver clases");
        Label lblEstVer = crearLabelSecundario();
        ListView<String> listaClasesEst = new ListView<>();
        listaClasesEst.setPrefHeight(160);
        Label lblMensajeVer = crearLabelError();

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
        filaVer.setAlignment(Pos.CENTER_LEFT);

        // ---- ADICIONAR CLASE ----
        TextField txtIdEstAdd = crearTextField("Id estudiante");
        TextField txtIdClaseAdd = crearTextField("Id clase (C001, C002, ...)");
        Button btnAdd = crearBotonSecundario("Adicionar");
        Label lblMsgAdd = crearLabelSecundario();

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
        filaAdd.setAlignment(Pos.CENTER_LEFT);

        // ---- RETIRAR CLASE ----
        TextField txtIdEstRet = crearTextField("Id estudiante");
        TextField txtIdClaseRet = crearTextField("Id clase");
        Button btnRet = crearBotonSecundario("Retirar");
        Label lblMsgRet = crearLabelSecundario();

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
        filaRet.setAlignment(Pos.CENTER_LEFT);

        // ---- CAMBIAR CLASE ----
        TextField txtIdEstCamb = crearTextField("Id estudiante");
        TextField txtIdClaseAct = crearTextField("Id clase actual");
        TextField txtIdClaseNueva = crearTextField("Id clase nueva");
        Button btnCamb = crearBotonSecundario("Cambiar");
        Label lblMsgCamb = crearLabelSecundario();

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
        filaCamb.setAlignment(Pos.CENTER_LEFT);

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
        listaClasesSistema.setPrefHeight(170);

        card.getChildren().addAll(
                new Label("---- Ver clases de un estudiante ----"),
                filaVer,
                lblEstVer,
                lblMensajeVer,
                listaClasesEst,
                new Separator(),
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

        tab.setContent(card);
        return tab;
    }

    // 4. Consultar estudiantes inscritos en una asignatura
    private Tab crearTabEstudiantesAsignatura() {
        Tab tab = new Tab("Estudiantes por Asig.");

        VBox card = crearCardBase("Estudiantes inscritos",
                "Encuentra rápidamente quién cursa cada asignatura.");

        TextField txtCodigo = crearTextField("Código de la asignatura");
        Button btnBuscar = crearBotonPrimario("Buscar");

        Label lblInfoAsig = crearLabelSecundario();
        Label lblMensaje = crearLabelError();
        ListView<String> listaEstudiantes = new ListView<>();
        listaEstudiantes.setPrefHeight(240);

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
        filaEntrada.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(
                filaEntrada,
                lblInfoAsig,
                lblMensaje,
                listaEstudiantes
        );

        tab.setContent(card);
        return tab;
    }

    // 5. Consultar asignaturas por departamento
    private Tab crearTabAsignaturasPorDepartamento() {
        Tab tab = new Tab("Asig. por Depto.");

        VBox card = crearCardBase("Asignaturas por departamento",
                "Visualiza el catálogo de materias en cada área académica.");

        TextField txtIdDep = crearTextField("Id departamento (1, 2, ...)");
        Button btnBuscar = crearBotonPrimario("Buscar");

        Label lblInfoDep = crearLabelSecundario();
        Label lblMensaje = crearLabelError();
        ListView<String> listaAsignaturas = new ListView<>();
        listaAsignaturas.setPrefHeight(220);

        // Mostrar también los deptos disponibles
        ListView<String> listaDepartamentos = new ListView<>();
        ObservableList<String> itemsDep = FXCollections.observableArrayList();
        for (Departamento d : departamentos) {
            itemsDep.add(d.getIdDepartamento() + " - " + d.getNombreDepartamento());
        }
        listaDepartamentos.setItems(itemsDep);
        listaDepartamentos.setPrefHeight(120);

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
        filaEntrada.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(
                new Label("Departamentos disponibles:"),
                listaDepartamentos,
                filaEntrada,
                lblInfoDep,
                lblMensaje,
                new Label("Asignaturas del departamento:"),
                listaAsignaturas
        );

        tab.setContent(card);
        return tab;
    }

    // 6. Consultar información de un profesor
    private Tab crearTabInfoProfesor() {
        Tab tab = new Tab("Info Profesor");

        VBox card = crearCardBase("Información de profesor",
                "Consulta datos de contacto y clases asignadas.");

        TextField txtId = crearTextField("id_Profesor o codDocente");
        Button btnBuscar = crearBotonPrimario("Buscar");
        Label lblInfo = crearLabelSecundario();
        Label lblMensaje = crearLabelError();
        ListView<String> listaClases = new ListView<>();
        listaClases.setPrefHeight(220);

        btnBuscar.setOnAction(e -> {
            lblInfo.setText("");
            lblMensaje.setText("");
            listaClases.getItems().clear();

            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                lblMensaje.setText("Debe ingresar el id o código del profesor.");
                return;
            }

            Profesor p = buscarProfesorPorIdOCod(id);
            if (p == null) {
                lblMensaje.setText("No se encontró profesor con id/código: " + id);
                return;
            }

            lblInfo.setText("Profesor: " + p.getNombre() + " | Email: " + p.getEmail());

            ObservableList<String> items = FXCollections.observableArrayList();
            for (Asignatura a : asignaturas) {
                if (a.getClases() == null) continue;
                for (Clase c : a.getClases()) {
                    if (c.getProfesor() != null && p.getId_Profesor().equals(c.getProfesor().getId_Profesor())) {
                        items.add(a.getCodigoAsignatura() + " - " + a.getNombreAsignatura()
                                + " | Clase " + c.getIdClase()
                                + " | Semestre: " + c.getSemestre()
                                + " | Horario: " + c.getDias() + " " + c.getHoras());
                    }
                }
            }

            if (items.isEmpty()) {
                lblMensaje.setText("El profesor no tiene clases asignadas.");
            } else {
                listaClases.setItems(items);
            }
        });

        HBox filaEntrada = new HBox(10, new Label("Id/Código:"), txtId, btnBuscar);
        filaEntrada.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(filaEntrada, lblInfo, lblMensaje, listaClases);
        tab.setContent(card);
        return tab;
    }

    // 7. Crear nueva asignatura
    private Tab crearTabCrearAsignatura() {
        Tab tab = new Tab("Crear Asignatura");

        VBox card = crearCardBase("Crear nueva asignatura",
                "Registra rápidamente una materia y asígnala a un departamento.");

        TextField txtCodigo = crearTextField("Código (ej: PRG201)");
        TextField txtNombre = crearTextField("Nombre de la asignatura");
        TextField txtCreditos = crearTextField("Créditos");
        CheckBox chkIngles = new CheckBox("Requiere examen de inglés");
        ComboBox<Departamento> cbDepartamento = new ComboBox<>();
        cbDepartamento.setPromptText("Departamento");
        cbDepartamento.setItems(FXCollections.observableArrayList(departamentos));
        cbDepartamento.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Departamento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombreDepartamento());
            }
        });
        cbDepartamento.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Departamento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Selecciona un departamento" : item.getNombreDepartamento());
            }
        });

        Button btnCrear = crearBotonPrimario("Crear asignatura");
        Label lblMensaje = crearLabelSecundario();

        btnCrear.setOnAction(e -> {
            lblMensaje.setText("");
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String creditosTxt = txtCreditos.getText().trim();
            Departamento dep = cbDepartamento.getValue();

            if (codigo.isEmpty() || nombre.isEmpty() || creditosTxt.isEmpty()) {
                lblMensaje.setText("Complete todos los campos obligatorios.");
                return;
            }

            int creditos;
            try {
                creditos = Integer.parseInt(creditosTxt);
            } catch (NumberFormatException ex) {
                lblMensaje.setText("Los créditos deben ser un número.");
                return;
            }

            if (buscarAsignaturaPorCodigo(codigo) != null) {
                lblMensaje.setText("Ya existe una asignatura con ese código.");
                return;
            }

            Asignatura nueva = new Asignatura(codigo, nombre, creditos, chkIngles.isSelected());
            asignaturas.add(nueva);
            if (dep != null) {
                nueva.setDepartamento(dep);
                dep.getAsignaturas().add(nueva);
            }

            lblMensaje.setText("Asignatura creada exitosamente.");
            txtCodigo.clear();
            txtNombre.clear();
            txtCreditos.clear();
            chkIngles.setSelected(false);
            cbDepartamento.setValue(null);
        });

        VBox.setVgrow(cbDepartamento, Priority.NEVER);
        card.getChildren().addAll(
                new Label("Código:"), txtCodigo,
                new Label("Nombre:"), txtNombre,
                new Label("Créditos:"), txtCreditos,
                chkIngles,
                new Label("Departamento:"), cbDepartamento,
                btnCrear,
                lblMensaje
        );

        tab.setContent(card);
        return tab;
    }

    // ============================================================
    // ====================  DATOS MOCK  ==========================
    // ============================================================

    private void inicializarDatosEjemplo() {
        // Profesores
        Profesor p1 = new Profesor("Juan Perez", "P001", "DOC001", "juan@uni.edu", 10, true);
        Profesor p2 = new Profesor("Ana Garcia", "P002", "DOC002", "ana@uni.edu", 5, false);
        profesores.add(p1);
        profesores.add(p2);

        // Asignaturas
        Asignatura a1 = new Asignatura("ADS101", "Análisis y Diseño de Sistemas", 4, true);
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

    // ============================================================
    // ====================  OPERACIONES  =========================
    // ============================================================

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
        }

        if (claseNueva.getEstudiantesInscritos().size() >= claseNueva.getCupoMaximo()) {
            return "La clase nueva está llena.";
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

    // ============================================================
    // =======================  UI HELPERS  =======================
    // ============================================================

    private VBox crearCardBase(String titulo, String subtitulo) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("Inter", FontWeight.BOLD, 18));
        lblTitulo.setTextFill(Color.web("#0f172a"));

        Label lblSub = new Label(subtitulo);
        lblSub.setTextFill(Color.web("#334155"));

        VBox card = new VBox(12, lblTitulo, lblSub);
        card.setPadding(new Insets(14));
        card.setStyle(CARD_STYLE);
        return card;
    }

    private TextField crearTextField(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;" +
                "-fx-padding: 8 10; -fx-border-color: #cbd5e1;" +
                "-fx-focus-color: #2563eb; -fx-faint-focus-color: rgba(37,99,235,0.2);");
        return tf;
    }

    private Button crearBotonPrimario(String texto) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: linear-gradient(to right, #2563eb, #1d4ed8);" +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;" +
                "-fx-padding: 8 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6,0,0,2);");
        return btn;
    }

    private Button crearBotonSecundario(String texto) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a;" +
                "-fx-background-radius: 10; -fx-padding: 8 14;" +
                "-fx-border-color: #cbd5e1; -fx-border-radius: 10;");
        return btn;
    }

    private Label crearLabelSecundario() {
        Label lbl = new Label();
        lbl.setTextFill(Color.web("#0f172a"));
        return lbl;
    }

    private Label crearLabelError() {
        Label lbl = new Label();
        lbl.setTextFill(Color.web("#b91c1c"));
        return lbl;
    }
}
