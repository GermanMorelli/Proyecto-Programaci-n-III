package proyecto.persistence;

import proyecto.model.Doctor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Persistencia de medicos en CSV (id|nombre|especialidad)
 */
public class PersistenciaMedicos extends PersistenciaBase {
    public PersistenciaMedicos(Path file) throws IOException { super(file); }

    public List<Doctor> obtenerTodos() throws IOException {
        List<Doctor> res = new ArrayList<>();
        for (String l: readDataLines()) {
            String[] p = l.split("\\|", -1);
            if (p.length<3) continue;
            res.add(new Doctor(Integer.parseInt(p[0]), p[1], p[2]));
        }
        return res;
    }

    public Optional<Doctor> obtenerPorId(int id) throws IOException {
        return obtenerTodos().stream().filter(d->d.getId()==id).findFirst();
    }

    public void agregar(Doctor d) throws IOException {
        if (d.getId()<=0) throw new IllegalArgumentException("ID debe ser positivo");
        if (obtenerPorId(d.getId()).isPresent()) throw new IllegalArgumentException("ID duplicado");
        appendLine(String.join("|", String.valueOf(d.getId()), safe(d.getNombre()), safe(d.getEspecialidad())));
    }

    public void saveAll(List<Doctor> list) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("#id|nombre|especialidad");
        for (Doctor d: list) lines.add(String.join("|", String.valueOf(d.getId()), safe(d.getNombre()), safe(d.getEspecialidad())));
        writeAllLines(lines);
    }

    private String safe(String s) { return s==null?"":s.replace("|"," ").trim(); }
}
