package Control.Consultas;

import Modelo.Asignatura;
import Modelo.Clase;
import Modelo.Estudiante;
import Vista.InterfazConsultaEstudiantes;

import java.util.ArrayList;
import java.util.List;

public class controladorConsultaEstudiantes {

    private List<Asignatura> asignaturas;
    private InterfazConsultaEstudiantes vista;

    public controladorConsultaEstudiantes(List<Asignatura> asignaturas) {
        this.asignaturas = asignaturas;
    }

    public void setVista(InterfazConsultaEstudiantes vista) {
        this.vista = vista;
    }


    //CONSULTAR LOS ESTUDIANTES INSCRITOS EN UNA ASIGNATURA ESPECIFICA.
    public void consultarEstudiantesPorAsignatura(String codigoAsignatura) {
        Asignatura asignatura = buscarAsignaturaPorCodigo(codigoAsignatura);
        if (asignatura == null) {
            if (vista != null) {
                vista.mostrarError("No se encontro la asignatura con codigo: " + codigoAsignatura);
            }
            return;
        }

        List<Estudiante> estudiantes = obtenerEstudiantesDeAsignatura(asignatura);

        if (vista != null) {
            vista.mostrarEstudiantesAsignatura(asignatura, estudiantes);
        }
    }

    private Asignatura buscarAsignaturaPorCodigo(String codigo) {
        if (codigo==null||asignaturas==null) {
            return null;
        }

        for (Asignatura a : asignaturas) {
            if (codigo.equals(a.getCodigoAsignatura())) {
                return a;
            }
        }
        return null;
    }

    private List<Estudiante> obtenerEstudiantesDeAsignatura(Asignatura asignatura) {

        List<Estudiante> resultado = new ArrayList<Estudiante>();

        if (asignatura==null || asignatura.getClases()==null) {
            return resultado;
        }

        for (Clase c : asignatura.getClases()) {
            if (c == null || c.getEstudiantesInscritos() == null) {
                continue;
            }

            for (Estudiante e : c.getEstudiantesInscritos()) {
                if (e != null && !contieneEstudiante(resultado, e)) {
                    resultado.add(e);
                }
            }
        }

        return resultado;
    }

    private boolean contieneEstudiante(List<Estudiante> lista, Estudiante estudiante) {
        if (lista==null|| estudiante==null) {
            return false;
        }

        String id = estudiante.getIdEstudiante();

        for (Estudiante e : lista) {
            if (e == null) {
                continue;
            }

            String idLista = e.getIdEstudiante();
            if (id != null && id.equals(idLista)) {
                return true;
            }
        }
        return false;
    }
}
