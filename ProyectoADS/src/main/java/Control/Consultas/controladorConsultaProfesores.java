package Control.Consultas;

import Modelo.Profesor;
import Vista.InterfazConsultaProfesores;

import java.util.List;

public class controladorConsultaProfesores {

    private List<Profesor> profesores;
    private InterfazConsultaProfesores vista;

    public controladorConsultaProfesores(List<Profesor> profesores) {
        this.profesores = profesores;
    }

    public void setVista(InterfazConsultaProfesores vista) {
        this.vista = vista;
    }

    public void consultarProfesor(String idProfesor) {

        Profesor profesor = obtenerProfesor(idProfesor);

        if (profesor==null) {
            if (vista != null) {
                vista.mostrarMensaje("El profesor no existe");
            }
        } else {
            if (vista != null) {
                vista.mostrarInformacionProfesor(profesor);
            }
        }
    }

    private Profesor obtenerProfesor(String idProfesor) {
        if (idProfesor==null||profesores==null) {
            return null;
        }

        for (Profesor p : profesores) {
            // Se busca a el profesor por el id_Profesor o por el codDocente
            if (idProfesor.equals(p.getId_Profesor())||idProfesor.equals(p.getCodDocente())) {
                return p;
            }
        }
        return null;
    }
}
