package proyecto.model;

/**
 * Representa un doctor.
 */
public class Doctor {
    private int id;
    private String nombre;
    private String especialidad;

    public Doctor() {}

    public Doctor(int id, String nombre, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    @Override
    public String toString() {
        return id + " | " + nombre + " (" + especialidad + ")";
    }
}
