package proyecto.persistence;

import proyecto.model.Paciente;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import proyecto.validacionDatos.validacionPaciente;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Persistencia de pacientes en CSV (id|nombre|direccion|edad|telefono|fecha)
 */
public class PersistenciaPacientes extends PersistenciaBase {
    private validacionPaciente vp;
    public PersistenciaPacientes(Path file) throws IOException { super(file); }

    public List<Paciente> obtenerTodos() throws IOException {
        List<Paciente> res = new ArrayList<>();
        for (String l : readDataLines()) {
            String[] p = l.split("\\|", -1);
            if (p.length < 4) continue;
            int id = Integer.parseInt(p[0]);
            String nombre = p[1];
            String direccion = p[2];
            int edad = Integer.parseInt(p[3]);
            String telefono = p[4];
            res.add(new Paciente(id,nombre,direccion,edad,telefono));
        }
        return res;
    }
    public List<Paciente> buscarPorNombre(String patron) throws IOException {
    List<Paciente> resultados = new ArrayList<>();
    Pattern pattern = Pattern.compile(patron, Pattern.CASE_INSENSITIVE);

    for (String l : readDataLines()) {
        String[] p = l.split("\\|", -1);
        if (p.length < 5) continue;

        int id = Integer.parseInt(p[0]);
        String nombre = p[1];
        String direccion = p[2];
        int edad = Integer.parseInt(p[3]);
        String telefono = p[4];

        if (pattern.matcher(nombre).find()) {
            resultados.add(new Paciente(id, nombre, direccion, edad, telefono));
        }
    }
    return resultados;
}

    
    public Optional<Paciente> obtenerPorId(int id) throws IOException {
        return obtenerTodos().stream().filter(x->x.getId()==id).findFirst();
    }

    public void agregar(Paciente p) throws IOException {
        if (p.getId()<=0) throw new IllegalArgumentException("ID debe ser positivo");
        if (vp.validaNombrePaciente(p.getNombre()) == false) throw new IllegalArgumentException("Nombre erroneo");
        if (vp.validaEdad(p.getEdad()) == false) throw new IllegalArgumentException("Edad imposible");
        if (vp.validaDireccion(p.getDireccion()) == false) throw new IllegalArgumentException("Formato de Direccion erronea");
        if (obtenerPorId(p.getId()).isPresent()) throw new IllegalArgumentException("ID duplicado");
        appendLine(toLine(p));
    
    }

    public void actualizar(Paciente p) throws IOException {
        List<Paciente> todos = obtenerTodos();
        boolean found=false;
        for (int i=0;i<todos.size();i++){
            if (todos.get(i).getId()==p.getId()){
                todos.set(i,p); found=true; break;
            }
        }
        if (!found) throw new NoSuchElementException("Paciente no encontrado");
        List<String> lines = new ArrayList<>();
        lines.add("#id|nombre|direccion|edad|telefono|fecha");
        for (Paciente x: todos) lines.add(toLine(x));
        writeAllLines(lines);
    }

    public void eliminar(int id) throws IOException {
        List<Paciente> todos = obtenerTodos();
        boolean removed = todos.removeIf(x->x.getId()==id);
        if (!removed) throw new NoSuchElementException("Paciente no encontrado");
        List<String> lines = new ArrayList<>();
        lines.add("#id|nombre|direccion|edad|telefono|fecha");
        for (Paciente x: todos) lines.add(toLine(x));
        writeAllLines(lines);
    }

    private String toLine(Paciente p) {
        return String.join("|", String.valueOf(p.getId()), safe(p.getNombre()), safe(p.getDireccion()),
                String.valueOf(p.getEdad()), safe(p.getTelefono()));
    }

    private String safe(String s) { return s==null?"":s.replace("|"," ").trim(); }
}
