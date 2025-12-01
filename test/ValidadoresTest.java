import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import proyecto.model.*;

import java.time.LocalDateTime;

public class ValidadoresTest {

    /* ===========================
            PACIENTE
       =========================== */

    @Test
    void testSetNombrePaciente() {
        Paciente p = new Paciente();
        p.setNombre("Carlos");
        assertEquals("Carlos", p.getNombre());
    }

    @Test
    void testSetNombrePacienteVacio() {
        Paciente p = new Paciente();
        p.setNombre("");
        assertEquals("", p.getNombre());
    }

    @Test
    void testSetEdadPaciente() {
        Paciente p = new Paciente();
        p.setEdad(30);
        assertEquals(30, p.getEdad());
    }

    @Test
    void testSetEdadPacienteNegativa() {
        Paciente p = new Paciente();
        p.setEdad(-5);
        assertEquals(-5, p.getEdad());
    }

    /* ===========================
            CONSULTA
       =========================== */

    @Test
    void testCrearConsulta() {
        LocalDateTime fecha = LocalDateTime.now();
        Consulta c = new Consulta(1, 2, 3, fecha, "Dolor", "Ninguna");
        assertEquals(fecha, c.getFecha());
    }

    @Test
    void testSetFechaConsulta() {
        Consulta c = new Consulta();
        LocalDateTime f = LocalDateTime.of(2024, 5, 10, 12, 0);
        c.setFecha(f);
        assertEquals(f, c.getFecha());
    }

    /* ===========================
              EQUIPO
       =========================== */

    @Test
    void testSetNombreEquipo() {
        Equipo e = new Equipo();
        e.setNombre("Rayos X");
        assertEquals("Rayos X", e.getNombre());
    }

    @Test
    void testSetDisponibleEquipo() {
        Equipo e = new Equipo();
        e.setDisponible(10);
        assertEquals(10, e.getDisponible());
    }

    /* ===========================
           INVENTARIO ITEM
       =========================== */

    @Test
    void testInventarioItem() {
        InventarioItem item = new InventarioItem();
        item.setNombre("Jeringas");
        item.setCantidad(50);
        assertEquals("Jeringas", item.getNombre());
        assertEquals(50, item.getCantidad());
    }

    /* ===========================
               DOCTOR
       =========================== */

    @Test
    void testDoctor() {
        Doctor d = new Doctor();
        d.setNombre("Roberto");
        d.setEspecialidad("Cardiólogo");
        assertEquals("Roberto", d.getNombre());
        assertEquals("Cardiólogo", d.getEspecialidad());
    }
}
