package Modelo;

public class DirectorCarrera
{
    private String nombre;
    private String email;

    public DirectorCarrera(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public DirectorCarrera() {
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
}

