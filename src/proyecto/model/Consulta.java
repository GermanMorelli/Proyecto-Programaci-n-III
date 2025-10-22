package proyecto.model;

import java.time.LocalDateTime;

/**
 * Representa una consulta médica sencilla.
 */
public class Consulta {
    private int id;
    private int pacienteId;
    private int doctorId;
    private LocalDateTime fecha;
    private String motivo;
    private String notas;

    public Consulta() {}

    public Consulta(int id, int pacienteId, int doctorId, LocalDateTime fecha, String motivo, String notas) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.doctorId = doctorId;
        this.fecha = fecha;
        this.motivo = motivo;
        this.notas = notas;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPacienteId() { return pacienteId; }
    public void setPacienteId(int pacienteId) { this.pacienteId = pacienteId; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    @Override
    public String toString() {
        return id + " | Pac:" + pacienteId + " Dr:" + doctorId + " " + fecha;
    }
}
