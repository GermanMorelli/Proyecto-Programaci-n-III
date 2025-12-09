package proyecto.persistence;

import proyecto.model.Consulta;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Persistencia de consultas (id|pacienteId|doctorId|fechaISO|motivo|notas)
 */
public class PersistenciaConsultas extends PersistenciaBase {
    public PersistenciaConsultas(Path file) throws IOException { super(file); }

    public List<Consulta> obtenerTodos() throws IOException {
        List<Consulta> res = new ArrayList<>();
        for (String l: readDataLines()) {
            String[] p = l.split("\\|", -1);
            if (p.length<6) continue;
            int id = Integer.parseInt(p[0]);
            int pacienteId = Integer.parseInt(p[1]);
            int doctorId = Integer.parseInt(p[2]);
            LocalDateTime fecha = p[3].isEmpty()?null:LocalDateTime.parse(p[3]);
            String motivo = p[4];
            String notas = p[5];
            res.add(new Consulta(id,pacienteId,doctorId,fecha,motivo,notas));
        }
        return res;
    }
    
    public List<Consulta> buscarConsultasPorFecha(String patronFecha) throws IOException {
        List<Consulta> resultados = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        for (String l : readDataLines()) {
            String[] p = l.split("\\|", -1);
            if (p.length < 6) continue;

            int id = Integer.parseInt(p[0]);
            int pacienteId = Integer.parseInt(p[1]);
            int doctorId = Integer.parseInt(p[2]);
            LocalDateTime fecha = p[3].isEmpty() ? null : LocalDateTime.parse(p[3]);
            String motivo = p[4];
            String notas = p[5];

            if (fecha != null) {
                String fechaTexto = fecha.format(formatter);
                if (patronFecha == null || patronFecha.isBlank() || fechaTexto.startsWith(patronFecha)) {
                    resultados.add(new Consulta(id, pacienteId, doctorId, fecha, motivo, notas));
                }
            } else if (patronFecha == null || patronFecha.isBlank()) {
                // Si no hay fecha y el patrón está vacío, también se incluye
                resultados.add(new Consulta(id, pacienteId, doctorId, null, motivo, notas));
            }
        }
        return resultados;
    }


    public Optional<Consulta> obtenerPorId(int id) throws IOException {
            return obtenerTodos().stream().filter(x->x.getId()==id).findFirst();
        }
    

    public void agregar(Consulta c) throws IOException {
        if (c.getId()<=0) throw new IllegalArgumentException("ID debe ser positivo");
        if (c.getPacienteId()<=0) throw new IllegalArgumentException("ID debe ser positivo");
        if (c.getDoctorId()<=0) throw new IllegalArgumentException("ID debe ser positivo");
        if (obtenerPorId(c.getId()).isPresent()) throw new IllegalArgumentException("ID duplicado");
        // no check uniqueness for simplicity
        appendLine(toLine(c));
    }

    public void saveAll(List<Consulta> list) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("#id|pacienteId|doctorId|fechaISO|motivo|notas");
        for (Consulta c: list) lines.add(toLine(c));
        writeAllLines(lines);
    }

    private String toLine(Consulta c) {
        return String.join("|", String.valueOf(c.getId()), String.valueOf(c.getPacienteId()),
                String.valueOf(c.getDoctorId()), c.getFecha()==null?"":c.getFecha().toString(),
                safe(c.getMotivo()), safe(c.getNotas()));
    }

    private String safe(String s) { return s==null?"":s.replace("|"," ").trim(); }
}
