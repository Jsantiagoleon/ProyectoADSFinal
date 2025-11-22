package Control.Consultas;

import Modelo.Asignatura;
import Modelo.Clase;
import Modelo.Departamento;
import Modelo.Profesor;
import Vista.InterfazConsultaAsignaturas;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class controladorConsultaAsignaturas {

    private List<Departamento> departamentos;
    private List<Profesor> profesores;
    private List<Asignatura> asignaturas;
    private InterfazConsultaAsignaturas vista;

    public controladorConsultaAsignaturas(List<Departamento> departamentos)
    {
        this.departamentos=departamentos;
    }

    public controladorConsultaAsignaturas(List<Profesor> profesores, List<Asignatura> asignaturas) {
        this.profesores = profesores;
        this.asignaturas = asignaturas;
    }







    //CONSULTAR ASIGNATURAS POR DEPARTAMENTO

    public List<Asignatura> consultarAsignaturasPorDepartamento(int idDepa) {
        if (departamentos==null) {
            return null;
        }
        for (Departamento d : departamentos) {
            if (d.getIdDepartamento() == idDepa) {
                return d.getAsignaturas();
            }
        }
        return null;
    }

    public List<Departamento> getDepartamentos() {
        return departamentos;
    }
    public void setDepartamentos(List<Departamento> departamentos) {
        this.departamentos = departamentos;
    }

    public void setVista(InterfazConsultaAsignaturas vista) {
        this.vista = vista;
    }







    // CONSULTAR ASIGNATURAS POR PROFESOR Y SEMESTRE

    public void buscarPorProfesorSemestre(String idProfesor, String semestre) {

        Profesor profesor = consultarProfesor(idProfesor);

        if (profesor == null) {
            if (vista != null) {
                vista.mostrarError("No se encontro el profesor con id: " + idProfesor);
            }
            return;
        }

        List<Asignatura> asignaturasProfesor =
                consultarAsignaturasProfesorSemestre(profesor, semestre);

        if (vista != null) {
            vista.mostrarResultados(profesor, asignaturasProfesor, semestre);
        }
    }

    private Profesor consultarProfesor(String idProfesor) {
        if (idProfesor == null || profesores == null) {
            return null;
        }

        for (Profesor p : profesores) {
            if (idProfesor.equals(p.getId_Profesor()) || idProfesor.equals(p.getCodDocente())) {
                return p;
            }
        }
        return null;
    }

    private List<Asignatura> consultarAsignaturasProfesorSemestre(Profesor profesor, String semestre) {
        List<Asignatura> resultado = new ArrayList<Asignatura>();

        if (profesor == null || semestre == null || asignaturas == null) {
            return resultado;
        }

        String idProfBuscado = profesor.getId_Profesor();

        for (Asignatura asignatura : asignaturas) {

            if (asignatura.getClases() == null) {
                continue;
            }

            for (Clase clase : asignatura.getClases()) {
                if (clase == null) {
                    continue;
                }

                Profesor profClase = clase.getProfesor();
                String semestreClase = clase.getSemestre();

                if (profClase == null) {
                    continue;
                }

                boolean mismoProfesor = Objects.equals(idProfBuscado, profClase.getId_Profesor());
                boolean mismoSemestre = Objects.equals(semestre, semestreClase);

                if (mismoProfesor && mismoSemestre) {
                    if (!resultado.contains(asignatura)) {
                        resultado.add(asignatura);
                    }
                    break;
                }
            }
        }

        return resultado;
    }

    // CONSULTAR INFORMACION DE UNA ASIGNATURA

    public void buscarAsignaturaPorCodigo(String codigoAsignatura) {

        Asignatura asignatura = consultarAsignaturaPorCodigo(codigoAsignatura);

        if (asignatura == null) {
            if (vista != null) {
                vista.mostrarError("No se encontro la asignatura con codigo: " + codigoAsignatura);
            }
            return;
        }

        if (vista != null) {
            vista.mostrarInfoAsignatura(asignatura);
        }
    }

    private Asignatura consultarAsignaturaPorCodigo(String codigo) {
        if (codigo == null || asignaturas == null) {
            return null;
        }

        for (Asignatura a : asignaturas) {
            if (codigo.equals(a.getCodigoAsignatura())) {
                return a;
            }
        }
        return null;
    }
}
