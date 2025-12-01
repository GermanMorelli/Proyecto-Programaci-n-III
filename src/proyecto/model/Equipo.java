package proyecto.model;

/**
 * Representa un equipo médico o dispositivo.
 */
public class Equipo {
    private int id;
    private String nombre;
    private String descripcion;
    private int disponible;

    public Equipo() {}

    public Equipo(int id, String nombre, String descripcion, int disponible) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.disponible = disponible;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getDisponible() { return disponible; }
    public void setDisponible(int disponible) { this.disponible = disponible; }

    @Override
    public String toString() {
        return id + " | " + nombre + (disponible );
    }
}
