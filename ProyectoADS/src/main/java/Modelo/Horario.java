package Modelo;

import java.util.List;
import java.util.ArrayList;

public class Horario extends Clase
{
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




