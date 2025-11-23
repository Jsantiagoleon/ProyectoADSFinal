package Modelo;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Asignatura implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String codigoAsignatura;
    private String nombreAsignatura;
    private int creditos;
    private List<Asignatura> prerrequisitos;
    private List<Asignatura> correquisitos;
    private boolean requiereExamenIngles;
    private List<Clase> clases;
    private Departamento departamento;

    public Asignatura()
    {
        this.prerrequisitos = new ArrayList<>();
        this.correquisitos = new ArrayList<>();
        this.clases = new ArrayList<>();
    }

    public Asignatura(String codigoAsignatura, String nombreAsignatura, int creditos,
                       boolean requiereExamenIngles){
        this.codigoAsignatura = codigoAsignatura;
        this.nombreAsignatura = nombreAsignatura;
        this.creditos = creditos;
        this.prerrequisitos = prerrequisitos;
        this.correquisitos = correquisitos;
        this.requiereExamenIngles = requiereExamenIngles;
        this.prerrequisitos = new ArrayList<>();
        this.correquisitos = new ArrayList<>();
        this.clases = new ArrayList<>();
    }

    public String getCodigoAsignatura() {
        return codigoAsignatura;
    }

    public void setCodigoAsignatura(String codigoAsignatura) {
        this.codigoAsignatura = codigoAsignatura;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public List<Asignatura> getPrerrequisitos() { return prerrequisitos; }
    public void setPrerrequisitos(List<Asignatura> prerrequisitos) {
        this.prerrequisitos = prerrequisitos;
    }

    public List<Asignatura> getCorrequisitos() {return correquisitos; }
    public void setCorrequisitos(List<Asignatura> correquisitos) {
        this.correquisitos = correquisitos;
    }

    public boolean isRequiereExamenIngles() { return requiereExamenIngles; }
    public void setRequiereExamenIngles(boolean requiereExamenIngles) {
        this.requiereExamenIngles = requiereExamenIngles;
    }

    public List<Clase> getClases() { return clases; }
    public void setClases(List<Clase> clases) { this.clases = clases; }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    public void agregarPrerequisito(Asignatura asignatura) {
        if (asignatura == null) {
            return;
        }
        if (prerrequisitos == null) {
            prerrequisitos = new ArrayList<>();
        }
        if (!prerrequisitos.contains(asignatura)) {
            prerrequisitos.add(asignatura);
        }
    }
    public void agregarCorrequisito(Asignatura asignatura) {
        if (asignatura == null) {
            return;
        }
        if (correquisitos == null) {
            correquisitos = new ArrayList<>();
        }
        if (!correquisitos.contains(asignatura)) {
            correquisitos.add(asignatura);
        }
    }

    public boolean tieneCodigo(String codigo) {
        return codigo != null && codigo.equals(codigoAsignatura);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o ==  null) {
            return false;
        }

        if (o instanceof Asignatura) {
            Asignatura otra = (Asignatura) o;

            if (codigoAsignatura == null && otra.codigoAsignatura == null) {
                return true;
            }

            if (codigoAsignatura == null || otra.codigoAsignatura == null) {
                return false;
            }

            return codigoAsignatura.equals(otra.codigoAsignatura);
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (codigoAsignatura == null) {
            return 0;
        } else {
            return codigoAsignatura.hashCode();
        }
    }

    @Override
    public String toString() {
        return codigoAsignatura + " - " + nombreAsignatura;
    }
}
