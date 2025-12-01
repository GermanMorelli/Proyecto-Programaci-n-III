package proyecto.model;

import java.time.LocalDate;

/**
 * Representa un paciente.
 */
public class Paciente {
    private int id;
    private String nombre;
    private String direccion;
    private int edad;
    private String telefono;

    public Paciente() {}

    public Paciente(int id, String nombre, String direccion, int edad, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.edad = edad;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return id + " | " + nombre + " | " + edad + " años";
    }
}
