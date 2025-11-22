package Modelo;

public class Estudiante
{
    private int id_Estudiante;
    private String nombre;
    private String idEstudiante;
    private String email;
    private String carrera;
    private boolean requisitoInglesCumplido;
    private Horario horario;

    public Estudiante() {
        this.horario= new Horario();
    }

    public Estudiante(String nombre, String idEstudiante, String email, String carrera, boolean requisitoInglesCumplido) {
        this.nombre = nombre;
        this.idEstudiante = idEstudiante;
        this.email = email;
        this.carrera = carrera;
        this.requisitoInglesCumplido = requisitoInglesCumplido;
        this.horario= new Horario();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(String idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public boolean isRequisitoInglesCumplido() {
        return requisitoInglesCumplido;
    }

    public void setRequisitoInglesCumplido(boolean requisitoInglesCumplido) {
        this.requisitoInglesCumplido = requisitoInglesCumplido;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario)
    {
        this.horario = horario;
    }
}
