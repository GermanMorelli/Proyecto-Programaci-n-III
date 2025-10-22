package proyecto.model;

/**
 * Item de inventario (insumos, medicamentos, etc).
 */
public class InventarioItem {
    private int id;
    private String nombre;
    private int cantidad;
    private String unidad;

    public InventarioItem() {}

    public InventarioItem(int id, String nombre, int cantidad, String unidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.unidad = unidad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    @Override
    public String toString() {
        return id + " | " + nombre + " : " + cantidad + " " + unidad;
    }
}
