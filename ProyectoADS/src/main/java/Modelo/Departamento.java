package Modelo;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Departamento implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int idDepartamento;
    private String nombreDepartamento;
    private List<Profesor> profesores;
    private List<Asignatura> asignaturas;

    public Departamento() {
        this.profesores = new ArrayList<>();
        this.asignaturas = new ArrayList<>();
    }

    public Departamento(int idDepartamento, String nombreDepartamento) {
        this.idDepartamento =idDepartamento;
        this.nombreDepartamento =nombreDepartamento;
        this.profesores =new ArrayList<>();
        this.asignaturas = new ArrayList<>();
    }

    //GETTERS & SETTERS

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }

    public List<Profesor> getProfesores() {
        return profesores;
    }

    public void setProfesores(List<Profesor> profesores) {
        this.profesores = profesores;
    }

    public List<Asignatura> getAsignaturas() {
        return asignaturas;
    }

    public void setAsignaturas(List<Asignatura> asignaturas) {
        this.asignaturas = asignaturas;
    }
}
