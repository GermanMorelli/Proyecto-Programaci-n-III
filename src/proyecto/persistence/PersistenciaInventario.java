package proyecto.persistence;

import proyecto.model.InventarioItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Persistencia de inventario (id|nombre|cantidad|unidad)
 */
public class PersistenciaInventario extends PersistenciaBase {
    public PersistenciaInventario(Path file) throws IOException { super(file); }

    public List<InventarioItem> obtenerTodos() throws IOException {
        List<InventarioItem> res = new ArrayList<>();
        for (String l: readDataLines()) {
            String[] p = l.split("\\|", -1);
            if (p.length<4) continue;
            res.add(new InventarioItem(Integer.parseInt(p[0]), p[1], Integer.parseInt(p[2]), p[3]));
        }
        return res;
    }

    public void agregar(InventarioItem i) throws IOException {
        if (i.getId()<=0) throw new IllegalArgumentException("ID debe ser positivo");
        appendLine(toLine(i));
    }

    public void saveAll(List<InventarioItem> list) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("#id|nombre|cantidad|unidad");
        for (InventarioItem it: list) lines.add(toLine(it));
        writeAllLines(lines);
    }

    private String toLine(InventarioItem i) {
        return String.join("|", String.valueOf(i.getId()), safe(i.getNombre()), String.valueOf(i.getCantidad()), safe(i.getUnidad()));
    }

    private String safe(String s) { return s==null?"":s.replace("|"," ").trim(); }
}
