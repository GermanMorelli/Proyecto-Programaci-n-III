package proyecto.persistence;

import proyecto.model.Equipo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import proyecto.validacionDatos.validacionEquipos;

/**
 * Persistencia de equipos (id|nombre|descripcion|disponible)
 */
public class PersistenciaEquipos extends PersistenciaBase {
    private validacionEquipos ve;
    public PersistenciaEquipos(Path file) throws IOException { super(file); }

    public List<Equipo> obtenerTodos() throws IOException {
        List<Equipo> res = new ArrayList<>();
        for (String l: readDataLines()) {
            String[] p = l.split("\\|", -1);
            if (p.length<4) continue;
            int id = Integer.parseInt(p[0]);
            String nombre = p[1];
            String desc = p[2];
            int disp = Integer.parseInt(p[3]);
            res.add(new Equipo(id,nombre,desc,disp));
        }
        return res;
    }
    public Optional<Equipo> obtenerPorId(int id) throws IOException {
        return obtenerTodos().stream().filter(x->x.getId()==id).findFirst();
    }

    public void agregar(Equipo e) throws IOException {
        if (e.getId()<=0) throw new IllegalArgumentException("ID debe ser positivo");
        if (ve.validaNombreEquipo(e.getNombre()) == false) throw new IllegalArgumentException("Nombre erroneo");
        if (ve.validaCantidadEquipo(e.getDisponible()) == false) throw new IllegalArgumentException("Debe ser positivo");
        if (obtenerPorId(e.getId()).isPresent()) throw new IllegalArgumentException("ID duplicado");
        appendLine(toLine(e));
    }

    public void actualizar(Equipo e) throws IOException {
        List<Equipo> todos = obtenerTodos();
        boolean found = false;
        for (int i = 0; i <todos.size(); i++){
            if (todos.get(i).getId()==e.getId()){
                todos.set(i, e); found = true; break;
            }
        }
        if (!found) throw new NoSuchElementException("Equipo no encontrado");
        List<String> lines = new ArrayList<>();
        lines.add("#id|nombre|descripcion|6");
        for (Equipo x: todos) lines.add(toLine(x));
        writeAllLines(lines);
    }
    
    public void eliminar(int id) throws IOException {
        List<Equipo> todos = obtenerTodos();
        boolean removed = todos.removeIf(x->x.getId()==id);
        if (!removed) throw new NoSuchElementException("Equipo no encontrado");
        List<String> lines = new ArrayList<>();
        lines.add("#id|nombre|descripcion|6");
        for (Equipo x: todos) lines.add(toLine(x));
        writeAllLines(lines);
    }
 
    private String toLine(Equipo e) {
        return String.join("|", String.valueOf(e.getId()), safe(e.getNombre()), safe(e.getDescripcion()), String.valueOf(e.getDisponible()));
    }

    private String safe(String s) { return s==null?"":s.replace("|"," ").trim(); }
}
