package proyecto.persistence;

import proyecto.model.Equipo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Persistencia de equipos (id|nombre|descripcion|disponible)
 */
public class PersistenciaEquipos extends PersistenciaBase {
    public PersistenciaEquipos(Path file) throws IOException { super(file); }

    public List<Equipo> obtenerTodos() throws IOException {
        List<Equipo> res = new ArrayList<>();
        for (String l: readDataLines()) {
            String[] p = l.split("\\|", -1);
            if (p.length<4) continue;
            int id = Integer.parseInt(p[0]);
            String nombre = p[1];
            String desc = p[2];
            boolean disp = Boolean.parseBoolean(p[3]);
            res.add(new Equipo(id,nombre,desc,disp));
        }
        return res;
    }

    public void agregar(Equipo e) throws IOException {
        if (e.getId()<=0) throw new IllegalArgumentException("ID debe ser positivo");
        appendLine(toLine(e));
    }

    public void saveAll(List<Equipo> list) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("#id|nombre|descripcion|disponible");
        for (Equipo e: list) lines.add(toLine(e));
        writeAllLines(lines);
    }

    private String toLine(Equipo e) {
        return String.join("|", String.valueOf(e.getId()), safe(e.getNombre()), safe(e.getDescripcion()), String.valueOf(e.isDisponible()));
    }

    private String safe(String s) { return s==null?"":s.replace("|"," ").trim(); }
}
