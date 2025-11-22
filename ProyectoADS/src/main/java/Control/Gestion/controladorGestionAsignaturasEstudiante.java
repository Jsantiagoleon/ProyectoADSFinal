package Control.Gestion;

import Modelo.Asignatura;
import Modelo.Clase;
import Modelo.Estudiante;
import Vista.InterfazGestionAsignaturasEstudiante;

import java.util.ArrayList;
import java.util.List;

public class controladorGestionAsignaturasEstudiante {

    private List<Estudiante> estudiantes;
    private List<Asignatura> asignaturas;
    private InterfazGestionAsignaturasEstudiante vista;

    public controladorGestionAsignaturasEstudiante(List<Estudiante> estudiantes,
                                                   List<Asignatura> asignaturas) {
        this.estudiantes = estudiantes;
        this.asignaturas = asignaturas;
    }

    public void setVista(InterfazGestionAsignaturasEstudiante vista) {
        this.vista = vista;
    }

    //OPERACIONES PRINCIPALES

    public void adicionarClaseAEstudiante(String idEstudiante, String idClase) {

        Estudiante estudiante= buscarEstudiantePorId(idEstudiante);
        if (estudiante==null) {
            if (vista !=null) {
                vista.mostrarError("No se encontro estudiante con id " + idEstudiante);
            }
            return;
        }

        Clase clase=buscarClasePorId(idClase);
        if (clase==null) {
            if (vista!=null) {
                vista.mostrarError("No se encontro clase con id " + idClase);
            }
            return;
        }

        if (clase.getEstudiantesInscritos().contains(estudiante)) {
            if (vista!=null) {
                vista.mostrarError("El estudiante ya esta inscrito en esta clase");
            }
            return;
        }

        if (clase.getEstudiantesInscritos().size() >= clase.getCupoMaximo()) {
            if (vista!=null) {
                vista.mostrarError("La clase esta llena, no se puede adicionar");
            }
            return;
        }

        clase.getEstudiantesInscritos().add(estudiante);

        Asignatura asignatura = buscarAsignaturaDeClase(clase);

        if (vista != null) {
            vista.mostrarMensaje("Se adiciono la clase "
                    + clase.getIdClase() + " de la asignatura "
                    + obtenerNombreAsignatura(asignatura) + " al estudiante " + estudiante.getNombre());
        }
    }

    public void retirarClaseDeEstudiante(String idEstudiante, String idClase) {

        Estudiante estudiante = buscarEstudiantePorId(idEstudiante);
        if (estudiante ==null) {
            if (vista !=null) {
                vista.mostrarError("No se encontro estudiante con id " + idEstudiante);
            }
            return;
        }

        Clase clase = buscarClasePorId(idClase);
        if (clase ==null) {
            if (vista !=null) {
                vista.mostrarError("No se encontro clase con id " + idClase);
            }
            return;
        }

        if (!clase.getEstudiantesInscritos().contains(estudiante)) {
            if (vista !=null) {
                vista.mostrarError("El estudiante no esta inscrito en esta clase");
            }
            return;
        }

        clase.getEstudiantesInscritos().remove(estudiante);

        Asignatura asignatura = buscarAsignaturaDeClase(clase);

        if (vista !=null) {
            vista.mostrarMensaje("Se retiro la clase " + clase.getIdClase()
                    + " de la asignatura " + obtenerNombreAsignatura(asignatura)
                    + " del estudiante " + estudiante.getNombre());
        }
    }

    public void cambiarClaseDeEstudiante(String idEstudiante, String idClaseActual, String idClaseNueva) {

        Estudiante estudiante = buscarEstudiantePorId(idEstudiante);
        if (estudiante ==null) {
            if (vista !=null) {
                vista.mostrarError("No se encontro estudiante con id " + idEstudiante);
            }
            return;
        }

        Clase claseActual = buscarClasePorId(idClaseActual);
        if (claseActual==null) {
            if (vista !=null) {
                vista.mostrarError("No se encontro la clase actual con id " + idClaseActual);
            }
            return;
        }

        Clase claseNueva = buscarClasePorId(idClaseNueva);
        if (claseNueva ==null) {
            if (vista !=null) {
                vista.mostrarError("No se encontro la clase nueva con id " + idClaseNueva);
            }
            return;
        }

        if (!claseActual.getEstudiantesInscritos().contains(estudiante)) {
            if (vista !=null) {
                vista.mostrarError("El estudiante no esta inscrito en la clase actual");
            }
            return;
        }

        if (claseNueva.getEstudiantesInscritos().contains(estudiante)) {
            if (vista !=null) {
                vista.mostrarError("El estudiante ya esta inscrito en la clase nueva");
            }
            return;
        }

        if (claseNueva.getEstudiantesInscritos().size() >= claseNueva.getCupoMaximo()) {
            if (vista !=null) {
                vista.mostrarError("La clase nueva esta llena, no se puede cambiar");
            }
            return;
        }

        claseActual.getEstudiantesInscritos().remove(estudiante);
        claseNueva.getEstudiantesInscritos().add(estudiante);

        Asignatura asignActual = buscarAsignaturaDeClase(claseActual);
        Asignatura asignNueva = buscarAsignaturaDeClase(claseNueva);

        if (vista !=null) {
            vista.mostrarMensaje("Se cambio la clase " + claseActual.getIdClase()
                    + " de la asignatura " + obtenerNombreAsignatura(asignActual)
                    + " por la clase " + claseNueva.getIdClase()
                    + " de la asignatura " + obtenerNombreAsignatura(asignNueva)
                    + " para el estudiante " + estudiante.getNombre());
        }
    }

    public void mostrarClasesDeEstudiante(String idEstudiante) {

        Estudiante estudiante = buscarEstudiantePorId(idEstudiante);
        if (estudiante ==null) {
            if (vista !=null) {
                vista.mostrarError("No se encontro estudiante con id " + idEstudiante);
            }
            return;
        }

        List<Clase> clases = obtenerClasesDeEstudiante(estudiante);

        if (vista !=null) {
            vista.mostrarClasesEstudiante(estudiante, clases);
        }
    }

    //METODOS AUXILIARES

    private Estudiante buscarEstudiantePorId(String idEstudiante) {
        if (idEstudiante ==null || estudiantes ==null) {
            return null;
        }

        for (Estudiante e : estudiantes) {
            if (idEstudiante.equals(e.getIdEstudiante())) {
                return e;
            }
        }
        return null;
    }

    private Clase buscarClasePorId(String idClase) {
        if (idClase ==null || asignaturas ==null) {
            return null;
        }

        for (Asignatura a : asignaturas) {
            if (a.getClases() == null) {
                continue;
            }
            for (Clase c : a.getClases()) {
                if (c != null && idClase.equals(c.getIdClase())) {
                    return c;
                }
            }
        }
        return null;
    }

    private Asignatura buscarAsignaturaDeClase(Clase clase) {
        if (clase == null || asignaturas == null) {
            return null;
        }
        for (Asignatura a : asignaturas) {
            if (a.getClases() == null) {
                continue;
            }
            for (Clase c : a.getClases()) {
                if (c == clase) {
                    return a;
                }
            }
        }
        return null;
    }

    private String obtenerNombreAsignatura(Asignatura asignatura) {
        if (asignatura ==null) {
            return "(sin nombre)";
        }
        return asignatura.getNombreAsignatura();
    }

    private List<Clase> obtenerClasesDeEstudiante(Estudiante estudiante) {
        List<Clase> resultado = new ArrayList<Clase>();

        if (estudiante ==null || asignaturas ==null) {
            return resultado;
        }

        for (Asignatura a : asignaturas) {
            if (a.getClases() ==null) {
                continue;
            }
            for (Clase c : a.getClases()) {
                if (c != null && c.getEstudiantesInscritos() != null
                        && c.getEstudiantesInscritos().contains(estudiante)) {
                    resultado.add(c);
                }
            }
        }

        return resultado;
    }
}
