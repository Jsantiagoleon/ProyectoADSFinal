package Modelo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Clase implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String idClase;
    private String semestre;
    private String dias;
    private String horas;
    private String salon;
    private int cupoMaximo;
    private List<Estudiante> estudiantesInscritos;


    private Profesor profesor;

    public Clase() {
        this.estudiantesInscritos = new ArrayList<>();
    }


    public Clase(String idClase, String semestre, String dias, String horas,
                 String salon, int cupoMaximo, Profesor profesor) {
        this.idClase = idClase;
        this.semestre = semestre;
        this.dias = dias;
        this.horas = horas;
        this.salon = salon;
        this.cupoMaximo = cupoMaximo;
        this.estudiantesInscritos = new ArrayList<>();
        this.profesor = profesor;
    }


    public Clase(String idClase, String semestre, String dias, String horas,
                 String salon, int cupoMaximo) {
        this(idClase, semestre, dias, horas, salon, cupoMaximo, null);
    }

    public String getIdClase() {
        return idClase;
    }

    public void setIdClase(String idClase) {
        this.idClase = idClase;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public List<Estudiante> getEstudiantesInscritos() {
        return estudiantesInscritos;
    }

    public void setEstudiantesInscritos(List<Estudiante> estudiantesInscritos) {
        this.estudiantesInscritos = estudiantesInscritos;
    }


    public Profesor getProfesor() {
        return profesor;
    }
    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }


}
