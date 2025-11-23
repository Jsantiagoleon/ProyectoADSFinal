package Modelo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Horario extends Clase implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<Clase> clases;

    public Horario(){
        super();
        this.clases= new ArrayList<>();
    }
    public List<Clase> getClases()
    { return clases; }

    public void setClases(List<Clase> clases)
    { this.clases = clases; }
}




