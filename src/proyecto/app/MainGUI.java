package proyecto.app;

import proyecto.model.*;
import proyecto.persistence.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.function.Predicate;
import java.time.format.DateTimeFormatter;
import interfaces.FiltroEdad;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Interfaz gráfica básica usando Swing que permite CRUD de Pacientes, Médicos, Consultas, Equipos e Inventario.
 * Diseñada para ser simple, legible y fácil de entender.
 *
 * Cambios añadidos:
 * - Validaciones de paciente (nombre, edad, dirección) al agregar/editar.
 * - Validación de fecha de consulta al agregar.
 * - Validaciones de equipo (nombre y disponibilidad) al agregar/editar.
 * - Validación de cantidad en inventario al actualizar.
 * - Pestañas adicionales: "Buscar Pacientes" y "Buscar Consultas" (usan los métodos de filtrado ya existentes).
 */
public class MainGUI extends JFrame {
    private PersistenciaPacientes pp;
    private PersistenciaMedicos pm;
    private PersistenciaConsultas pc;
    private PersistenciaEquipos pe;
    private PersistenciaInventario pi;

    // Tables models
    private DefaultTableModel pacientesModel = new DefaultTableModel(new String[]{"ID","Nombre","Edad","Tel","Direccion"},0);
    private DefaultTableModel medicosModel = new DefaultTableModel(new String[]{"ID","Nombre","Especialidad"},0);
    private DefaultTableModel consultasModel = new DefaultTableModel(new String[]{"ID","Paciente","Doctor","Fecha"},0);
    private DefaultTableModel equiposModel = new DefaultTableModel(new String[]{"ID","Nombre","Disp"},0);
    private DefaultTableModel inventarioModel = new DefaultTableModel(new String[]{"ID","Nombre","Cantidad"},0);

    public MainGUI() {
        try {
            pp = new PersistenciaPacientes(Paths.get("data/pacientes.csv"));
            pm = new PersistenciaMedicos(Paths.get("data/medicos.csv"));
            pc = new PersistenciaConsultas(Paths.get("data/consultas.csv"));
            pe = new PersistenciaEquipos(Paths.get("data/equipos.csv"));
            pi = new PersistenciaInventario(Paths.get("data/inventario.csv"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al iniciar persistencia: " + e.getMessage());
            System.exit(1);
        }

        setTitle("Sistema Clínico - GUI");
        setSize(900,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Pacientes", buildPacientesPanel());
        tabs.addTab("Médicos", buildMedicosPanel());
        tabs.addTab("Consultas", buildConsultasPanel());
        tabs.addTab("Equipos", buildEquiposPanel());
        tabs.addTab("Inventario", buildInventarioPanel());

        // Nuevas pestañas de búsqueda (no cambian la estructura de las existentes)
        tabs.addTab("Buscar Pacientes", buildBuscarPacientesPanel());
        tabs.addTab("Buscar Consultas", buildBuscarConsultasPanel());
        // ... tus tabs anteriores ...
        tabs.addTab("Buscar Consultas", buildBuscarConsultasPanel());

        // AGREGA ESTA LÍNEA:
        tabs.addTab("Filtrar Búsquedas", buildPanelFuncional());

        add(tabs, BorderLayout.CENTER);
        refreshAll();
    }

    // --------------------------
    // Helpers de validación
    // --------------------------
    private void validarNombrePaciente(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (!nombre.matches("[A-Za-zÁÉÍÓÚáéíóúñÑ ]{3,}"))
            throw new IllegalArgumentException("El nombre debe contener solo letras y al menos 3 caracteres.");
    }

    private int validarEdad(String edadS) {
        if (edadS == null || edadS.isBlank()) throw new IllegalArgumentException("La edad no puede estar vacía.");
        if (!edadS.matches("\\d+")) throw new IllegalArgumentException("La edad debe ser un número entero.");
        int edad = Integer.parseInt(edadS);
        if (edad <= 0 || edad > 120) throw new IllegalArgumentException("La edad debe ser entre 1 y 120.");
        return edad;
    }

    private void validarDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) throw new IllegalArgumentException("La dirección no puede estar vacía.");
        if (direccion.length() < 3) throw new IllegalArgumentException("La dirección es demasiado corta.");
    }

    private LocalDateTime validarFechaConsulta(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            // Acepta formato ISO: YYYY-MM-DDTHH:MM o con segundos
            return LocalDateTime.parse(fecha);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Formato de fecha inválido. Usa: YYYY-MM-DDTHH:MM");
        }
    }

    private void validarEquipo(String nombre, String dispoS) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del equipo no puede estar vacío.");
        if (!nombre.matches("[A-Za-z0-9ÁÉÍÓÚáéíóúñÑ\\- ]{3,}"))
            throw new IllegalArgumentException("Nombre del equipo inválido. Mínimo 3 caracteres.");
        if (dispoS == null || !dispoS.matches("\\d+")) throw new IllegalArgumentException("La disponibilidad debe ser un número entero.");
        int d = Integer.parseInt(dispoS);
        if (d < 0) throw new IllegalArgumentException("La disponibilidad no puede ser negativa.");
    }

    private int validarCantidadInventario(String cantS) {
        if (cantS == null || cantS.isBlank()) throw new IllegalArgumentException("La cantidad no puede estar vacía.");
        if (!cantS.matches("-?\\d+")) throw new IllegalArgumentException("La cantidad debe ser un número entero.");
        int c = Integer.parseInt(cantS);
        if (c < 0) throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        return c;
    }

    // --------------------------
    // Panel Pacientes (con validaciones)
    // --------------------------
    private JPanel buildPacientesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(pacientesModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        JButton add = new JButton("Agregar");
        JButton del = new JButton("Eliminar");
        JButton edit = new JButton("Editar");
        JButton busc = new JButton("Buscar");
        controls.add(add); controls.add(edit); controls.add(del); controls.add(busc);
        p.add(controls, BorderLayout.SOUTH);

        add.addActionListener(a->{
            try {
                String sid = JOptionPane.showInputDialog(this, "ID:");
                if (sid==null) return;
                int id = Integer.parseInt(sid);

                String nombre = JOptionPane.showInputDialog(this, "Nombre:");
                String edadS = JOptionPane.showInputDialog(this, "Edad:");
                String tel = JOptionPane.showInputDialog(this, "Telefono:");
                String direccion = JOptionPane.showInputDialog(this, "Dirección");

                // Validaciones
                validarNombrePaciente(nombre);
                int edad = validarEdad(edadS);
                validarDireccion(direccion);

                pp.agregar(new Paciente(id,nombre,direccion,edad,tel));
                refreshPacientes();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "ID o Edad inválida. Debe ser un número.");
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, "Error de validación: " + iae.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        del.addActionListener(a->{
            try {
                int row = table.getSelectedRow();
                if (row<0) { JOptionPane.showMessageDialog(this, "Selecciona un paciente"); return; }
                int id = Integer.parseInt(pacientesModel.getValueAt(row,0).toString());
                if (JOptionPane.showConfirmDialog(this, "Confirmar eliminar ID="+id)==JOptionPane.YES_OPTION){
                    pp.eliminar(id); refreshPacientes();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        edit.addActionListener(a->{
            try {
                int row = table.getSelectedRow();
                if (row<0) { JOptionPane.showMessageDialog(this, "Selecciona un paciente"); return; }
                int id = Integer.parseInt(pacientesModel.getValueAt(row,0).toString());
                var op = pp.obtenerPorId(id);
                if (op.isEmpty()) { JOptionPane.showMessageDialog(this, "Paciente no encontrado"); return; }
                Paciente pOld = op.get();

                String nombre = JOptionPane.showInputDialog(this, "Nombre:", pOld.getNombre());
                String edadS = JOptionPane.showInputDialog(this, "Edad:", String.valueOf(pOld.getEdad()));
                String tel = JOptionPane.showInputDialog(this, "Telefono:", pOld.getTelefono());
                String direccion = JOptionPane.showInputDialog(this, "Dirección:", pOld.getDireccion());

                // Validaciones
                validarNombrePaciente(nombre);
                int edad = validarEdad(edadS);
                validarDireccion(direccion);

                pOld.setNombre(nombre); pOld.setEdad(edad); pOld.setTelefono(tel); pOld.setDireccion(direccion);
                pp.actualizar(pOld); refreshPacientes();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Edad inválida. Debe ser un número.");
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, "Error de validación: " + iae.getMessage());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        busc.addActionListener(a->{
            try {
                String nombre = JOptionPane.showInputDialog(this, "Nombre a buscar:");
                refreshFiltradoPacientes(nombre);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        return p;
    }

    // --------------------------
    // Médicos (sin cambios funcionales)
    // --------------------------
    private JPanel buildMedicosPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(medicosModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = new JPanel();
        JButton add = new JButton("Agregar");
        JButton del = new JButton("Eliminar");
        controls.add(add); controls.add(del);
        p.add(controls, BorderLayout.SOUTH);

        add.addActionListener(a->{
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog(this, "ID:" ));
                String nombre = JOptionPane.showInputDialog(this, "Nombre:");
                String esp = JOptionPane.showInputDialog(this, "Especialidad:");
                pm.agregar(new Doctor(id,nombre,esp));
                refreshMedicos();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        del.addActionListener(a->{
            try {
                int row = table.getSelectedRow();
                if (row<0) { JOptionPane.showMessageDialog(this, "Selecciona un doctor"); return; }
                int id = Integer.parseInt(medicosModel.getValueAt(row,0).toString());
                var list = pm.obtenerTodos();
                list.removeIf(d->d.getId()==id);
                pm.saveAll(list);
                refreshMedicos();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        return p;
    }

    // --------------------------
    // Consultas (con validación de fecha y búsqueda ya existente)
    // --------------------------
    private JPanel buildConsultasPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(consultasModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = new JPanel();
        JButton add = new JButton("Agregar");
        JButton del = new JButton("Eliminar");
        JButton busc = new JButton("Buscar por fecha");
        controls.add(add); controls.add(del); controls.add(busc);
        p.add(controls, BorderLayout.SOUTH);

        add.addActionListener(a->{
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog(this, "ID:" ));
                int pid = Integer.parseInt(JOptionPane.showInputDialog(this, "Paciente ID:" ));
                int did = Integer.parseInt(JOptionPane.showInputDialog(this, "Doctor ID:" ));
                String fecha = JOptionPane.showInputDialog(this, "Fecha y hora (YYYY-MM-DDTHH:MM)");
                LocalDateTime dt = validarFechaConsulta(fecha);
                String motivo = JOptionPane.showInputDialog(this, "Motivo:");
                String notas = JOptionPane.showInputDialog(this, "Notas:");
                pc.agregar(new Consulta(id,pid,did,dt,motivo,notas));
                refreshConsultas();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "ID inválido. Debe ser un número.");
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, "Error de validación: " + iae.getMessage());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        del.addActionListener(a->{
            try {
                int row = table.getSelectedRow();
                if (row<0) { JOptionPane.showMessageDialog(this, "Selecciona una consulta"); return; }
                int id = Integer.parseInt(consultasModel.getValueAt(row,0).toString());
                var list = pc.obtenerTodos();
                list.removeIf(c->c.getId()==id);
                pc.saveAll(list);
                refreshConsultas();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        busc.addActionListener(a->{
            try {
                String fecha = JOptionPane.showInputDialog(this, " Inserte fecha de consulta (YYYY-MM-DD):");
                refreshConsultasFiltradas(fecha);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        return p;
    }

    // --------------------------
    // Equipos (con validaciones)
    // --------------------------
    private JPanel buildEquiposPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(equiposModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = new JPanel();
        JButton add = new JButton("Agregar");
        JButton edit = new JButton("Cambiar Disp");
        JButton del = new JButton("Eliminar");
        controls.add(add); controls.add(edit); controls.add(del);
        p.add(controls, BorderLayout.SOUTH);

        add.addActionListener(a->{
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog(this, "ID:" ));
                String nombre = JOptionPane.showInputDialog(this, "Nombre:");
                String dispoS = JOptionPane.showInputDialog(this, "Disponibilidad");
                String desc = JOptionPane.showInputDialog(this, "Descripcion:");

                // Validación
                validarEquipo(nombre, dispoS);
                int disponibilidad = Integer.parseInt(dispoS);

                pe.agregar(new Equipo(id,nombre,desc,disponibilidad));
                refreshEquipos();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "ID o Disponibilidad inválida. Debe ser un número.");
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, "Error de validación: " + iae.getMessage());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        edit.addActionListener(a->{
            try {
                int row = table.getSelectedRow();
                if (row<0) { JOptionPane.showMessageDialog(this, "Selecciona un equipo"); return; }
                int id = Integer.parseInt(equiposModel.getValueAt(row,0).toString());
                var oe = pe.obtenerPorId(id);
                if (oe.isEmpty()) { JOptionPane.showMessageDialog(this, "Equipo medico no existente"); return; }
                Equipo E01d = oe.get();

                String dispoS = JOptionPane.showInputDialog(this, "Disponibles" , String.valueOf(E01d.getDisponible()));
                // Validar nueva cantidad antes de settear
                validarEquipo(E01d.getNombre(), dispoS); // valida disponibilidad y que el nombre existente sea válido
                int disp = Integer.parseInt(dispoS);
                E01d.setDisponible(disp);
                pe.actualizar(E01d); refreshEquipos();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Disponibilidad inválida. Debe ser un número.");
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, "Error de validación: " + iae.getMessage());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        del.addActionListener(a->{
            try {
                int row = table.getSelectedRow();
                if (row<0) { JOptionPane.showMessageDialog(this, "Selecciona un equipo"); return; }
                int id = Integer.parseInt(equiposModel.getValueAt(row,0).toString());
                var list = pe.obtenerTodos();
                list.removeIf(e->e.getId()==id);
                if (JOptionPane.showConfirmDialog(this, "Confirmar eliminar ID="+id)==JOptionPane.YES_OPTION){
                    pe.eliminar(id); refreshEquipos();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        return p;
    }

    // --------------------------
    // Inventario (validación cantidad al actualizar)
    // --------------------------
    private JPanel buildInventarioPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(inventarioModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = new JPanel();
        JButton add = new JButton("Agregar");
        JButton upd = new JButton("Actualizar Cantidad");
        JButton del = new JButton("Eliminar");
        controls.add(add); controls.add(upd); controls.add(del);
        p.add(controls, BorderLayout.SOUTH);

        add.addActionListener(a->{
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog(this, "ID:" ));
                String nombre = JOptionPane.showInputDialog(this, "Nombre:");
                String cantS = JOptionPane.showInputDialog(this, "Cantidad:");
                int cant = validarCantidadInventario(cantS);
                String unidad = JOptionPane.showInputDialog(this, "Unidad:");
                pi.agregar(new InventarioItem(id,nombre,cant,unidad));
                refreshInventario();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "ID inválido. Debe ser un número.");
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, "Error de validación: " + iae.getMessage());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        upd.addActionListener(a->{
            try {
                int row = table.getSelectedRow();
                if (row<0) { JOptionPane.showMessageDialog(this, "Selecciona un item"); return; }
                int id = Integer.parseInt(inventarioModel.getValueAt(row,0).toString());
                var list = pi.obtenerTodos();
                for (InventarioItem it: list) {
                    if (it.getId()==id) {
                        String nuevaS = JOptionPane.showInputDialog(this, "Nueva cantidad:", it.getCantidad());
                        int nueva = validarCantidadInventario(nuevaS);
                        it.setCantidad(nueva);
                    }
                }
                pi.saveAll(list); refreshInventario();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "ID inválido. Debe ser un número.");
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, "Error de validación: " + iae.getMessage());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        del.addActionListener(a->{
            try {
                int row = table.getSelectedRow();
                if (row<0) { JOptionPane.showMessageDialog(this, "Selecciona un item"); return; }
                int id = Integer.parseInt(inventarioModel.getValueAt(row,0).toString());
                var list = pi.obtenerTodos();
                list.removeIf(it->it.getId()==id);
                pi.saveAll(list); refreshInventario();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        return p;
    }

    // --------------------------
    // Refreshers
    // --------------------------
    private void refreshAll() {
        refreshPacientes();
        refreshMedicos();
        refreshConsultas();
        refreshEquipos();
        refreshInventario();
    }

    private void refreshPacientes() {
        try {
            List<Paciente> list = pp.obtenerTodos();
            pacientesModel.setRowCount(0);
            for (Paciente p: list) pacientesModel.addRow(new Object[]{p.getId(), p.getNombre(), p.getEdad(), p.getTelefono() , p.getDireccion()});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void refreshFiltradoPacientes(String patron) {
        try {
            List<Paciente> filtrados = pp.buscarPorNombre(patron); // usa el patrón como filtro
            pacientesModel.setRowCount(0); // limpia la tabla

            for (Paciente p : filtrados) {
                pacientesModel.addRow(new Object[]{ p.getId(), p.getNombre(), p.getEdad(), p.getTelefono(), p.getDireccion() });
           }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar pacientes: " + e.getMessage());
        }
    }

    private void refreshMedicos() {
        try {
            List<Doctor> list = pm.obtenerTodos();
            medicosModel.setRowCount(0);
            for (Doctor d: list) medicosModel.addRow(new Object[]{d.getId(), d.getNombre(), d.getEspecialidad()});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void refreshConsultas() {
        try {
            var list = pc.obtenerTodos();
            consultasModel.setRowCount(0);
            for (Consulta c: list) consultasModel.addRow(new Object[]{c.getId(), c.getPacienteId(), c.getDoctorId(), c.getFecha()});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void refreshConsultasFiltradas(String patronFecha) {
        try {
            var list = pc.buscarConsultasPorFecha(patronFecha);
            consultasModel.setRowCount(0);
            for (Consulta c : list) {
                consultasModel.addRow(new Object[]{ c.getId(), c.getPacienteId(), c.getDoctorId(), c.getFecha()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar consultas: " + e.getMessage());
        }
    }

    private void refreshEquipos() {
        try {
            var list = pe.obtenerTodos();
            equiposModel.setRowCount(0);
            for (Equipo e: list) equiposModel.addRow(new Object[]{e.getId(), e.getNombre(), e.getDisponible()});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void refreshInventario() {
        try {
            var list = pi.obtenerTodos();
            inventarioModel.setRowCount(0);
            for (InventarioItem it: list) inventarioModel.addRow(new Object[]{it.getId(), it.getNombre(), it.getCantidad()});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    // --------------------------
    // Paneles de búsqueda reutilizando los modelos existentes
    // --------------------------
    private JPanel buildBuscarPacientesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(pacientesModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel();
        JTextField txt = new JTextField(20);
        JButton btn = new JButton("Buscar");
        top.add(new JLabel("Nombre (patrón):"));
        top.add(txt);
        top.add(btn);
        p.add(top, BorderLayout.NORTH);

        btn.addActionListener(e -> refreshFiltradoPacientes(txt.getText()));
        return p;
    }

    private JPanel buildBuscarConsultasPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(consultasModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel();
        JTextField txt = new JTextField(12);
        JButton btn = new JButton("Buscar por fecha (YYYY-MM-DD)");
        top.add(new JLabel("Fecha:"));
        top.add(txt);
        top.add(btn);
        p.add(top, BorderLayout.NORTH);

        btn.addActionListener(e -> refreshConsultasFiltradas(txt.getText()));
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new MainGUI().setVisible(true);
        });
    }
    
    // ---------------------------------------------------------
    // PROYECTO 4: PESTAÑA DE PROGRAMACIÓN FUNCIONAL
    // ---------------------------------------------------------
    private JPanel buildPanelFuncional() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // Dividido en 4 cuadrantes
        
        // --- CUADRANTE 1: PACIENTES (Filtro Personalizado + Predicate) ---
        JPanel pPacientes = new JPanel(new BorderLayout());
        pPacientes.setBorder(BorderFactory.createTitledBorder("1. Pacientes (Lambda Propia + Predicate)"));
        
        JPanel pPacControles = new JPanel(new GridLayout(3, 2));
        JTextField txtEdad = new JTextField();
        JTextField txtDir = new JTextField();
        JButton btnFiltrarPac = new JButton("Filtrar (Edad y Dir)");
        
        pPacControles.add(new JLabel("Edad Mínima:")); pPacControles.add(txtEdad);
        pPacControles.add(new JLabel("Dirección (contiene):")); pPacControles.add(txtDir);
        pPacControles.add(new JLabel("")); pPacControles.add(btnFiltrarPac);
        
        JTextArea txtResPacientes = new JTextArea(); // Usamos area de texto para ver resultados rápido
        pPacientes.add(pPacControles, BorderLayout.NORTH);
        pPacientes.add(new JScrollPane(txtResPacientes), BorderLayout.CENTER);

        btnFiltrarPac.addActionListener(e -> {
            try {
                int edadMin = txtEdad.getText().isEmpty() ? 0 : Integer.parseInt(txtEdad.getText());
                String dirBuscar = txtDir.getText().toLowerCase();
                
                // 1. Uso de la Interfaz Funcional Personalizada (Requisito)
                FiltroEdad criterioEdad = (p, edad) -> p.getEdad() >= edad;
                
                // 2. Uso de Predicate estándar
                Predicate<Paciente> criterioDireccion = p -> 
                    dirBuscar.isEmpty() || p.getDireccion().toLowerCase().contains(dirBuscar);
                
                // 3. Stream Filter + Collect
                var listaFiltrada = pp.obtenerTodos().stream()
                    .filter(p -> criterioEdad.validar(p, edadMin)) // Usa interfaz propia
                    .filter(criterioDireccion)                     // Usa Predicate
                    .map(Paciente::toString)                       // Transformar a String
                    .collect(Collectors.joining("\n"));            // Unir con saltos de linea
                
                txtResPacientes.setText(listaFiltrada.isEmpty() ? "No hay coincidencias" : listaFiltrada);
                
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error en datos: " + ex.getMessage()); }
        });

        // --- CUADRANTE 2: MÉDICOS (Ordenar y Filtrar) ---
        JPanel pMedicos = new JPanel(new BorderLayout());
        pMedicos.setBorder(BorderFactory.createTitledBorder("2. Médicos (Sort & Filter)"));
        
        JPanel pMedControles = new JPanel();
        JTextField txtEsp = new JTextField(10);
        JButton btnMedicos = new JButton("Filtrar y Ordenar");
        pMedControles.add(new JLabel("Especialidad:")); pMedControles.add(txtEsp); pMedControles.add(btnMedicos);
        
        JTextArea txtResMedicos = new JTextArea();
        pMedicos.add(pMedControles, BorderLayout.NORTH);
        pMedicos.add(new JScrollPane(txtResMedicos), BorderLayout.CENTER);
        
        btnMedicos.addActionListener(e -> {
            try {
                String esp = txtEsp.getText().toLowerCase();
                var resultado = pm.obtenerTodos().stream()
                        .filter(d -> esp.isEmpty() || d.getEspecialidad().toLowerCase().contains(esp)) // Filtro
                        .sorted(Comparator.comparing(Doctor::getEspecialidad)
                                .thenComparing(Doctor::getNombre)) // Ordenamiento doble (Esp luego Nombre)
                        .map(Doctor::toString)
                        .collect(Collectors.joining("\n"));
                
                txtResMedicos.setText(resultado.isEmpty() ? "No encontrados" : resultado);
            } catch (IOException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // --- CUADRANTE 3: EQUIPOS (Reportes, Map, Suma) ---
        JPanel pEquipos = new JPanel(new BorderLayout());
        pEquipos.setBorder(BorderFactory.createTitledBorder("3. Equipos (Reporte & Stocks)"));
        
        JPanel pEqTop = new JPanel(new GridLayout(2, 2));
        JTextField txtUmbral = new JTextField("5");
        JButton btnBajoStock = new JButton("Buscar Bajo Stock");
        JButton btnReporte = new JButton("Generar Reporte Completo");
        JLabel lblTotalStock = new JLabel("Total Items: -");
        
        pEqTop.add(new JLabel("Umbral Stock:")); pEqTop.add(txtUmbral);
        pEqTop.add(btnBajoStock); pEqTop.add(btnReporte);
        
        JTextArea txtResEquipos = new JTextArea();
        pEquipos.add(pEqTop, BorderLayout.NORTH);
        pEquipos.add(new JScrollPane(txtResEquipos), BorderLayout.CENTER);
        pEquipos.add(lblTotalStock, BorderLayout.SOUTH);
        
        btnBajoStock.addActionListener(e -> {
            try {
                int umbral = Integer.parseInt(txtUmbral.getText());
                String bajos = pe.obtenerTodos().stream()
                    .filter(eq -> eq.getDisponible() < umbral)
                    .map(eq -> "ALERTA: " + eq.getNombre() + " (Quedan: " + eq.getDisponible() + ")")
                    .collect(Collectors.joining("\n"));
                txtResEquipos.setText(bajos.isEmpty() ? "Todos los stocks están bien." : bajos);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Número inválido"); }
        });
        
        btnReporte.addActionListener(e -> {
            try {
                // A. Generar texto (Map)
                String reporte = pe.obtenerTodos().stream()
                        .map(eq -> "Equipo: " + eq.getNombre() + " | Disponibles: " + eq.getDisponible()) // Transformación
                        .collect(Collectors.joining("\n"));
                txtResEquipos.setText("--- REPORTE GENERAL ---\n" + reporte);
                
                // B. Calcular Suma Total (MapToInt + Sum)
                int total = pe.obtenerTodos().stream()
                        .mapToInt(proyecto.model.Equipo::getDisponible) // Extraer entero
                        .sum(); // Sumar
                lblTotalStock.setText("Stock Total en Almacén: " + total + " unidades.");
            } catch (IOException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // --- CUADRANTE 4: CONSULTAS (Rango Fechas) ---
        JPanel pConsultas = new JPanel(new BorderLayout());
        pConsultas.setBorder(BorderFactory.createTitledBorder("4. Consultas (Rango Fechas)"));
        
        JPanel pConsTop = new JPanel(new GridLayout(3, 2));
        JTextField txtInicio = new JTextField("2023-01-01");
        JTextField txtFin = new JTextField("2025-12-31");
        JButton btnBuscarCons = new JButton("Buscar en Rango");
        
        pConsTop.add(new JLabel("Inicio (YYYY-MM-DD):")); pConsTop.add(txtInicio);
        pConsTop.add(new JLabel("Fin (YYYY-MM-DD):")); pConsTop.add(txtFin);
        pConsTop.add(new JLabel("")); pConsTop.add(btnBuscarCons);
        
        JTextArea txtResConsultas = new JTextArea();
        pConsultas.add(pConsTop, BorderLayout.NORTH);
        pConsultas.add(new JScrollPane(txtResConsultas), BorderLayout.CENTER);
        
        btnBuscarCons.addActionListener(e -> {
            try {
                // Convertir texto a fechas
                java.time.LocalDate fInicio = java.time.LocalDate.parse(txtInicio.getText());
                java.time.LocalDate fFin = java.time.LocalDate.parse(txtFin.getText());
                
                String res = pc.obtenerTodos().stream()
                    .filter(c -> {
                        java.time.LocalDate fConsulta = c.getFecha().toLocalDate();
                        // Lógica: No antes del inicio Y no después del fin
                        return !fConsulta.isBefore(fInicio) && !fConsulta.isAfter(fFin);
                    })
                    .map(c -> "Consulta ID: " + c.getId() + " - Fecha: " + c.getFecha())
                    .collect(Collectors.joining("\n"));
                
                txtResConsultas.setText(res.isEmpty() ? "Sin consultas en ese rango" : res);
                
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Formato fecha incorrecto (use YYYY-MM-DD)"); }
        });

        // Agregar todo al panel principal
        mainPanel.add(pPacientes);
        mainPanel.add(pMedicos);
        mainPanel.add(pEquipos);
        mainPanel.add(pConsultas);
        
        return mainPanel;
    }
}
