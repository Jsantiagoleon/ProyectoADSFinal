package Modelo;

import java.io.Serial;
import java.io.Serializable;

public class ProfesorPlanta extends Profesor implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public ProfesorPlanta()
    {

    }
    public ProfesorPlanta(String email, String nombre, int horasSemanales, int sueldo, String codDocente, String id_Profesor)
    {
        super(email, nombre, horasSemanales, sueldo, codDocente, id_Profesor);
    }
}
