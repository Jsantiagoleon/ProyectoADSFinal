package Modelo;

import java.io.Serial;
import java.io.Serializable;

public class Profesor implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String email;
    private int horasSemanales;
    private int sueldo;
    private String codDocente;
    private String id_Profesor;



    public Profesor() {
    }

    public Profesor(String email, String nombre, int horasSemanales, int sueldo, String codDocente, String id_Profesor) {
        this.email = email;
        this.nombre = nombre;
        this.horasSemanales = horasSemanales;
        this.sueldo = sueldo;
        this.codDocente = codDocente;
        this.id_Profesor = id_Profesor;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getHorasSemanales() {
        return horasSemanales;
    }
    public void setHorasSemanales(int horasSemanales) {
        this.horasSemanales = horasSemanales;
    }
    public int getSueldo() {
        return sueldo;
    }
    public void setSueldo(int sueldo) {
        this.sueldo = sueldo;
    }

    public String getCodDocente() {
        return codDocente;
    }

    public String getId_Profesor() {
        return id_Profesor;
    }

    public void setCodDocente(String codDocente) {
        this.codDocente = codDocente;
    }

    public void setId_Profesor(String id_Profesor) {
        this.id_Profesor = id_Profesor;
    }

}

