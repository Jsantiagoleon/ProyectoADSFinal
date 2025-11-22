package Control.Gestion;

import Modelo.Asignatura;
import Modelo.Departamento;
import Vista.InterfazCrearAsignatura;

import java.util.List;

public class controladorCrearAsignatura {

    private List<Asignatura> asignaturas;
    private List<Departamento> departamentos;
    private InterfazCrearAsignatura vista;

    public controladorCrearAsignatura(List<Asignatura> asignaturas,
                                      List<Departamento> departamentos) {
        this.asignaturas = asignaturas;
        this.departamentos = departamentos;
    }

    public void setVista(InterfazCrearAsignatura vista) {
        this.vista = vista;
    }

    // Crear una nueva asignatura
    public void crearAsignatura(String codigo,
                                String nombre,
                                int creditos,
                                boolean requiereExamenIngles,
                                Integer idDepartamento) {

        // Validar que el codigo no exista
        if (existeAsignaturaConCodigo(codigo)) {
            if (vista != null) {
                vista.mostrarError("Ya existe una asignatura con el codigo " + codigo);
            }
            return;
        }
        Asignatura nueva = new Asignatura(codigo, nombre, creditos, requiereExamenIngles);
        // Agregar a la lista general del sistema
        asignaturas.add(nueva);
        // Asocia el departamento si se envio uno
        if (idDepartamento != null) {
            Departamento dep = buscarDepartamentoPorId(idDepartamento);
            if (dep != null) {
                dep.getAsignaturas().add(nueva);
                nueva.setDepartamento(dep);
            } else {
                if (vista != null) {
                    vista.mostrarError("No se encontro el departamento con id " + idDepartamento
                            + ". La asignatura se creo sin departamento.");
                }
            }
        }

        if (vista != null) {
            vista.mostrarMensaje("Asignatura creada correctamente: "
                    + nueva.getCodigoAsignatura() + " - " + nueva.getNombreAsignatura());
        }
    }

    // Validación de la asignatura

    private boolean existeAsignaturaConCodigo(String codigo) {
        if (codigo == null || asignaturas == null) {
            return false;
        }
        for (Asignatura a : asignaturas) {
            if (a != null && codigo.equals(a.getCodigoAsignatura())) {
                return true;
            }
        }
        return false;
    }

    private Departamento buscarDepartamentoPorId(int idDepartamento) {
        if (departamentos == null) {
            return null;
        }
        for (Departamento d : departamentos) {
            if (d.getIdDepartamento() == idDepartamento) {
                return d;
            }
        }
        return null;
    }
}
