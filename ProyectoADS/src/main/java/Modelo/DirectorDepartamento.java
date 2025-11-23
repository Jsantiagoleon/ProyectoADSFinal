package Modelo;

import java.io.Serial;
import java.io.Serializable;

public class DirectorDepartamento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String nombreDepa;
    private String id_Departamento;

    public DirectorDepartamento() {
    }

    public DirectorDepartamento(String nombreDepa, String id_Departamento) {
        this.nombreDepa = nombreDepa;
        this.id_Departamento = id_Departamento;
    }

    public String getNombreDepa() {
        return nombreDepa;
    }

    public void setNombreDepa(String nombreDepa) {
        this.nombreDepa = nombreDepa;
    }

    public String getId_Departamento() {
        return id_Departamento;
    }

    public void setId_Departamento(String id_Departamento) {
        this.id_Departamento = id_Departamento;
    }
}
