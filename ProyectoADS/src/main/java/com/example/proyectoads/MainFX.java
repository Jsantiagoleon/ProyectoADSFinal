package com.example.proyectoads;

import Modelo.Asignatura;
import Modelo.Clase;
import Modelo.Departamento;
import Modelo.Estudiante;
import Modelo.Profesor;
import Serializacion.EstadoPrograma;
import Serializacion.GestorPersistencia;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainFX extends Application {

    private List<Profesor> profesores = new ArrayList<>();
    private List<Asignatura> asignaturas = new ArrayList<>();
    private List<Estudiante> estudiantes = new ArrayList<>();
    private List<Departamento> departamentos = new ArrayList<>();

    private GestorPersistencia gestorPersistencia;

    @Override
    public void start(Stage primaryStage) {
        gestorPersistencia = new GestorPersistencia(Path.of("datos", "estado_programa.bin"));
        cargarDatos();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("main-tabs");
        tabPane.getTabs().addAll(
                crearTabProfesorSemestre(),
                crearTabInfoAsignatura(),
                crearTabGestionEstudiante(),
                crearTabEstudiantesAsignatura(),
                crearTabAsignaturasPorDepartamento(),
                crearTabInfoProfesor(),
                crearTabCrearAsignatura()
        );

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(crearHeader());
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1150, 720);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/styles/theme.css")).toExternalForm());

        primaryStage.setTitle("Sistema de Gestión de Asignaturas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox crearHeader() {
        Label titulo = new Label("Sistema de Gestión de Asignaturas");
        titulo.getStyleClass().add("app-title");

        Label subtitulo = new Label("Consultas académicas y registro de estudiantes en un solo lugar");
        subtitulo.getStyleClass().add("app-subtitle");

        Button btnGuardar = new Button("💾 Guardar cambios");
        btnGuardar.getStyleClass().add("primary-button");
        btnGuardar.setOnAction(e -> persistirEstado());

        HBox acciones = new HBox(10, btnGuardar);
        acciones.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(6, titulo, subtitulo, acciones);
        header.setPadding(new Insets(18, 20, 14, 20));
        header.getStyleClass().add("header");
        return header;
    }

    private Tab crearTabProfesorSemestre() {
        Tab tab = new Tab("Prof. vs Semestre");

        Label titulo = new Label("Consultar asignaturas dictadas por un profesor en un semestre");
        titulo.getStyleClass().add("section-title");

        TextField txtIdProfesor = new TextField();
        txtIdProfesor.setPromptText("id_Profesor o codDocente (P001, DOC001, etc.)");

        TextField txtSemestre = new TextField();
        txtSemestre.setPromptText("Semestre (ej: 2025-1)");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("primary-button");

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
                List<String> items = new ArrayList<>();
                for (Asignatura a : resultado) {
                    items.add(a.getCodigoAsignatura() + " - " + a.getNombreAsignatura());
                }
                listaAsignaturas.getItems().setAll(items);
            }
        });

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(8);
        formulario.addRow(0, new Label("Id profesor:"), txtIdProfesor);
        formulario.addRow(1, new Label("Semestre:"), txtSemestre);
        formulario.add(btnBuscar, 1, 2);

        VBox contenido = new VBox(12,
                titulo,
                crearTarjeta("Búsqueda", formulario),
                crearTarjeta("Resultado", lblProfesor, lblMensaje, listaAsignaturas)
        );
        contenido.getStyleClass().add("tab-content");

        tab.setContent(contenido);
        return tab;
    }

    private Tab crearTabInfoAsignatura() {
        Tab tab = new Tab("Info Asignatura");

        Label titulo = new Label("Consultar información de una asignatura");
        titulo.getStyleClass().add("section-title");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código de la asignatura (ADS101, BD102, ...)");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("primary-button");

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
                List<String> items = new ArrayList<>();
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
                listaClases.getItems().setAll(items);
            }
        });

        HBox filaEntrada = new HBox(10, new Label("Código:"), txtCodigo, btnBuscar);
        filaEntrada.setAlignment(Pos.CENTER_LEFT);

        VBox contenido = new VBox(12,
                titulo,
                crearTarjeta("Búsqueda", filaEntrada),
                crearTarjeta("Datos básicos", lblDatosBasicos, lblMensaje),
                crearTarjeta("Clases de la asignatura", listaClases)
        );
        contenido.getStyleClass().add("tab-content");

        tab.setContent(contenido);
        return tab;
    }

    private Tab crearTabGestionEstudiante() {
        Tab tab = new Tab("Gestión Estudiante");

        Label titulo = new Label("Gestionar asignaturas de un estudiante (adicionar, retirar, cambiar)");
        titulo.getStyleClass().add("section-title");

        TextField txtIdEstVer = new TextField();
        txtIdEstVer.setPromptText("Id estudiante (E001, E002, ...)");
        Button btnVer = new Button("Ver clases");
        btnVer.getStyleClass().add("primary-button");
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
                List<String> items = new ArrayList<>();
                for (Clase c : clases) {
                    items.add(formatearClase(c));
                }
                listaClasesEst.getItems().setAll(items);
            }
        });

        HBox filaVer = new HBox(10, new Label("Id estudiante:"), txtIdEstVer, btnVer);
        filaVer.setAlignment(Pos.CENTER_LEFT);

        TextField txtIdEstAdd = new TextField();
        txtIdEstAdd.setPromptText("Id estudiante");
        TextField txtIdClaseAdd = new TextField();
        txtIdClaseAdd.setPromptText("Id clase (C001, C002, ...)");
        Button btnAdd = new Button("Adicionar");
        btnAdd.getStyleClass().add("primary-button");
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
        filaAdd.setAlignment(Pos.CENTER_LEFT);

        TextField txtIdEstRet = new TextField();
        txtIdEstRet.setPromptText("Id estudiante");
        TextField txtIdClaseRet = new TextField();
        txtIdClaseRet.setPromptText("Id clase");
        Button btnRet = new Button("Retirar");
        btnRet.getStyleClass().add("danger-button");
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
        filaRet.setAlignment(Pos.CENTER_LEFT);

        TextField txtIdEstCamb = new TextField();
        txtIdEstCamb.setPromptText("Id estudiante");
        TextField txtIdClaseAct = new TextField();
        txtIdClaseAct.setPromptText("Id clase actual");
        TextField txtIdClaseNueva = new TextField();
        txtIdClaseNueva.setPromptText("Id clase nueva");
        Button btnCamb = new Button("Cambiar");
        btnCamb.getStyleClass().add("accent-button");
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
        filaCamb.setAlignment(Pos.CENTER_LEFT);

        ListView<String> listaClasesSistema = new ListView<>();
        List<String> itemsClases = new ArrayList<>();
        for (Asignatura a : asignaturas) {
            if (a.getClases() == null) continue;
            for (Clase c : a.getClases()) {
                itemsClases.add(formatearClase(c) + " | Asig: " + a.getCodigoAsignatura());
            }
        }
        listaClasesSistema.getItems().setAll(itemsClases);

        VBox contenido = new VBox(12,
                titulo,
                crearTarjeta("Ver clases de un estudiante", filaVer, lblEstVer, lblMensajeVer, listaClasesEst),
                crearTarjeta("Adicionar clase", filaAdd, lblMsgAdd),
                crearTarjeta("Retirar clase", filaRet, lblMsgRet),
                crearTarjeta("Cambiar clase", filaCamb, lblMsgCamb),
                crearTarjeta("Clases disponibles en el sistema", listaClasesSistema)
        );
        contenido.getStyleClass().add("tab-content");

        tab.setContent(contenido);
        return tab;
    }

    private Tab crearTabEstudiantesAsignatura() {
        Tab tab = new Tab("Estudiantes por Asig.");

        Label titulo = new Label("Consultar estudiantes inscritos en una asignatura específica");
        titulo.getStyleClass().add("section-title");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código de la asignatura");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("primary-button");

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
                List<String> items = new ArrayList<>();
                for (Estudiante est : ests) {
                    items.add(est.getIdEstudiante() + " | " + est.getNombre()
                            + " | " + est.getEmail());
                }
                listaEstudiantes.getItems().setAll(items);
            }
        });

        HBox filaEntrada = new HBox(10, new Label("Código:"), txtCodigo, btnBuscar);
        filaEntrada.setAlignment(Pos.CENTER_LEFT);

        VBox contenido = new VBox(12,
                titulo,
                crearTarjeta("Búsqueda", filaEntrada),
                crearTarjeta("Detalle", lblInfoAsig, lblMensaje, listaEstudiantes)
        );
        contenido.getStyleClass().add("tab-content");

        tab.setContent(contenido);
        return tab;
    }

    private Tab crearTabAsignaturasPorDepartamento() {
        Tab tab = new Tab("Asig. por Depto.");

        Label titulo = new Label("Consultar las asignaturas ofrecidas por un departamento específico");
        titulo.getStyleClass().add("section-title");

        TextField txtIdDep = new TextField();
        txtIdDep.setPromptText("Id departamento (1, 2, ...)");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("primary-button");

        Label lblInfoDep = new Label();
        Label lblMensaje = new Label();

        ListView<String> listaAsignaturas = new ListView<>();
        ListView<String> listaDepartamentos = new ListView<>();
        List<String> itemsDep = new ArrayList<>();
        for (Departamento d : departamentos) {
            itemsDep.add(d.getIdDepartamento() + " - " + d.getNombreDepartamento());
        }
        listaDepartamentos.getItems().setAll(itemsDep);

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
                List<String> items = new ArrayList<>();
                for (Asignatura a : asigs) {
                    items.add(a.getCodigoAsignatura() + " - " + a.getNombreAsignatura());
                }
                listaAsignaturas.getItems().setAll(items);
            }
        });

        HBox filaEntrada = new HBox(10, new Label("Id depto:"), txtIdDep, btnBuscar);
        filaEntrada.setAlignment(Pos.CENTER_LEFT);

        VBox contenido = new VBox(12,
                titulo,
                crearTarjeta("Departamentos disponibles", listaDepartamentos),
                crearTarjeta("Consulta", filaEntrada, lblInfoDep, lblMensaje, new Label("Asignaturas del departamento:"), listaAsignaturas)
        );
        contenido.getStyleClass().add("tab-content");

        tab.setContent(contenido);
        return tab;
    }

    private Tab crearTabInfoProfesor() {
        Tab tab = new Tab("Info Profesor");

        Label titulo = new Label("Consultar información de un profesor");
        titulo.getStyleClass().add("section-title");

        TextField txtId = new TextField();
        txtId.setPromptText("id_Profesor o codDocente");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("primary-button");

        TextArea areaInfo = new TextArea();
        areaInfo.setEditable(false);
        areaInfo.setPrefRowCount(8);

        btnBuscar.setOnAction(e -> {
            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                areaInfo.setText("Ingrese un id o código de profesor.");
                return;
            }

            Profesor p = buscarProfesorPorIdOCod(id);
            if (p == null) {
                areaInfo.setText("No se encontró profesor con id/código: " + id);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Nombre: ").append(p.getNombre()).append('\n');
            sb.append("Id: ").append(p.getId_Profesor()).append(" | Código Docente: ").append(p.getCodDocente()).append('\n');
            sb.append("Email: ").append(p.getEmail()).append('\n');
            sb.append("Horas semanales: ").append(p.getHorasSemanales()).append('\n');
            sb.append("Sueldo: ").append(p.getSueldo());
            areaInfo.setText(sb.toString());
        });

        HBox filaEntrada = new HBox(10, new Label("Id o código:"), txtId, btnBuscar);
        filaEntrada.setAlignment(Pos.CENTER_LEFT);

        VBox contenido = new VBox(12,
                titulo,
                crearTarjeta("Búsqueda", filaEntrada),
                crearTarjeta("Detalle", areaInfo)
        );
        contenido.getStyleClass().add("tab-content");

        tab.setContent(contenido);
        return tab;
    }

    private Tab crearTabCrearAsignatura() {
        Tab tab = new Tab("Crear Asignatura");

        Label titulo = new Label("Crear una nueva asignatura");
        titulo.getStyleClass().add("section-title");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Código (ej. ADS101)");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la asignatura");
        TextField txtCreditos = new TextField();
        txtCreditos.setPromptText("Créditos");
        CheckBox chkIngles = new CheckBox("Requiere examen de inglés");
        TextField txtIdDepartamento = new TextField();
        txtIdDepartamento.setPromptText("Id Departamento (opcional)");
        Button btnCrear = new Button("Crear asignatura");
        btnCrear.getStyleClass().add("primary-button");
        Label lblMensaje = new Label();

        btnCrear.setOnAction(e -> {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String creditosTxt = txtCreditos.getText().trim();

            if (codigo.isEmpty() || nombre.isEmpty() || creditosTxt.isEmpty()) {
                lblMensaje.setText("Complete código, nombre y créditos.");
                return;
            }

            if (buscarAsignaturaPorCodigo(codigo) != null) {
                lblMensaje.setText("Ya existe una asignatura con ese código.");
                return;
            }

            int creditos;
            try {
                creditos = Integer.parseInt(creditosTxt);
            } catch (NumberFormatException ex) {
                lblMensaje.setText("Los créditos deben ser un número.");
                return;
            }

            Asignatura nueva = new Asignatura(codigo, nombre, creditos, chkIngles.isSelected());
            asignaturas.add(nueva);

            if (!txtIdDepartamento.getText().trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(txtIdDepartamento.getText().trim());
                    Departamento dep = buscarDepartamentoPorId(id);
                    if (dep != null) {
                        nueva.setDepartamento(dep);
                        dep.getAsignaturas().add(nueva);
                    }
                } catch (NumberFormatException ignored) {
                    lblMensaje.setText("Id de departamento inválido, se guardó sin departamento.");
                }
            }

            persistirEstado();
            lblMensaje.setText("Asignatura creada correctamente.");
            txtCodigo.clear();
            txtNombre.clear();
            txtCreditos.clear();
            txtIdDepartamento.clear();
            chkIngles.setSelected(false);
        });

        VBox contenido = new VBox(12,
                titulo,
                crearTarjeta("Datos de la asignatura",
                        new Label("Código:"), txtCodigo,
                        new Label("Nombre:"), txtNombre,
                        new Label("Créditos:"), txtCreditos,
                        chkIngles,
                        new Label("Departamento:"), txtIdDepartamento,
                        btnCrear,
                        lblMensaje)
        );
        contenido.getStyleClass().add("tab-content");

        tab.setContent(contenido);
        return tab;
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
        persistirEstado();
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
        persistirEstado();
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

        if (claseNueva.getEstudiantesInscritos().contains(est)) {
            return "El estudiante ya está inscrito en la clase nueva.";
        }

        if (claseNueva.getEstudiantesInscritos().size() >= claseNueva.getCupoMaximo()) {
            return "La clase nueva está llena; no se puede cambiar.";
        }

        claseActual.getEstudiantesInscritos().remove(est);
        claseNueva.getEstudiantesInscritos().add(est);
        persistirEstado();

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

    private VBox crearTarjeta(String titulo, javafx.scene.Node... contenido) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("card-title");

        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.getChildren().add(lblTitulo);
        card.getChildren().addAll(contenido);
        return card;
    }

    private void cargarDatos() {
        EstadoPrograma estado = gestorPersistencia.cargar();
        if (estado != null) {
            profesores = estado.getProfesores() != null ? estado.getProfesores() : new ArrayList<>();
            asignaturas = estado.getAsignaturas() != null ? estado.getAsignaturas() : new ArrayList<>();
            estudiantes = estado.getEstudiantes() != null ? estado.getEstudiantes() : new ArrayList<>();
            departamentos = estado.getDepartamentos() != null ? estado.getDepartamentos() : new ArrayList<>();
        } else {
            inicializarDatosEjemplo();
            persistirEstado();
        }
    }

    private void persistirEstado() {
        gestorPersistencia.guardar(new EstadoPrograma(profesores, asignaturas, estudiantes, departamentos));
    }

    private void inicializarDatosEjemplo() {
        profesores = new ArrayList<>();
        asignaturas = new ArrayList<>();
        estudiantes = new ArrayList<>();
        departamentos = new ArrayList<>();

        Profesor p1 = new Profesor("juan@uni.edu", "Juan Perez", 20, 3000000, "DOC001", "P001");
        Profesor p2 = new Profesor("ana@uni.edu", "Ana Gomez", 18, 2800000, "DOC002", "P002");
        profesores.add(p1);
        profesores.add(p2);

        Asignatura a1 = new Asignatura("ADS101", "Análisis y Diseño de Sistemas", 3, true);
        Asignatura a2 = new Asignatura("BD102", "Bases de Datos", 4, false);
        Asignatura a3 = new Asignatura("RED103", "Redes de Computadores", 3, false);
        asignaturas.add(a1);
        asignaturas.add(a2);
        asignaturas.add(a3);

        Clase c1 = new Clase("C001", "2025-1", "Lu-Mi", "8-10", "A101", 40, p1);
        Clase c2 = new Clase("C002", "2025-1", "Ma-Ju", "10-12", "A102", 35, p1);
        Clase c3 = new Clase("C003", "2025-2", "Lu-Mi", "14-16", "B201", 30, p1);
        Clase c4 = new Clase("C004", "2025-1", "Ma-Ju", "8-10", "B202", 40, p2);

        a1.getClases().add(c1);
        a2.getClases().add(c2);
        a3.getClases().add(c3);
        a1.getClases().add(c4);

        Estudiante e1 = new Estudiante("Carlos Lopez", "E001",
                "carlos@uni.edu", "Ingenieria de Sistemas", true);
        Estudiante e2 = new Estudiante("Maria Ruiz", "E002",
                "maria@uni.edu", "Ingenieria Industrial", false);
        estudiantes.add(e1);
        estudiantes.add(e2);

        c1.getEstudiantesInscritos().add(e1);
        c2.getEstudiantesInscritos().add(e1);
        c1.getEstudiantesInscritos().add(e2);
        c3.getEstudiantesInscritos().add(e2);

        Departamento depSis = new Departamento(1, "Ingenieria de Sistemas");
        depSis.getAsignaturas().add(a1);
        depSis.getAsignaturas().add(a2);

        Departamento depTele = new Departamento(2, "Telematica");
        depTele.getAsignaturas().add(a3);

        departamentos.add(depSis);
        departamentos.add(depTele);

        a1.setDepartamento(depSis);
        a2.setDepartamento(depSis);
        a3.setDepartamento(depTele);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
