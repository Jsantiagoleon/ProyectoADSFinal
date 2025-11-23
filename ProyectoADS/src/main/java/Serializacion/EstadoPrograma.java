package Serializacion;

import Modelo.Asignatura;
import Modelo.Departamento;
import Modelo.Estudiante;
import Modelo.Profesor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Contenedor serializable que agrupa las colecciones principales del sistema.
 */
public class EstadoPrograma implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Profesor> profesores;
    private final List<Asignatura> asignaturas;
    private final List<Estudiante> estudiantes;
    private final List<Departamento> departamentos;

    public EstadoPrograma(List<Profesor> profesores, List<Asignatura> asignaturas,
                          List<Estudiante> estudiantes, List<Departamento> departamentos) {
        this.profesores = profesores;
        this.asignaturas = asignaturas;
        this.estudiantes = estudiantes;
        this.departamentos = departamentos;
    }

    public List<Profesor> getProfesores() {
        return profesores;
    }

    public List<Asignatura> getAsignaturas() {
        return asignaturas;
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public List<Departamento> getDepartamentos() {
        return departamentos;
    }
}
