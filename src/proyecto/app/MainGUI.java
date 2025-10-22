package proyecto.app;

import proyecto.model.*;
import proyecto.persistence.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz gráfica básica usando Swing que permite CRUD de Pacientes, Médicos, Consultas, Equipos e Inventario.
 * Diseñada para ser simple, legible y fácil de entender.
 */
public class MainGUI extends JFrame {
    private PersistenciaPacientes pp;
    private PersistenciaMedicos pm;
    private PersistenciaConsultas pc;
    private PersistenciaEquipos pe;
    private PersistenciaInventario pi;

    // Tables models
    private DefaultTableModel pacientesModel = new DefaultTableModel(new String[]{"ID","Nombre","Edad","Tel"},0);
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

        add(tabs, BorderLayout.CENTER);
        refreshAll();
    }

    private JPanel buildPacientesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(pacientesModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        JButton add = new JButton("Agregar");
        JButton del = new JButton("Eliminar");
        JButton edit = new JButton("Editar");
        controls.add(add); controls.add(edit); controls.add(del);
        p.add(controls, BorderLayout.SOUTH);

        add.addActionListener(a->{
            try {
                String sid = JOptionPane.showInputDialog(this, "ID:");
                if (sid==null) return;
                int id = Integer.parseInt(sid);
                String nombre = JOptionPane.showInputDialog(this, "Nombre:");
                String edadS = JOptionPane.showInputDialog(this, "Edad:");
                int edad = Integer.parseInt(edadS);
                String tel = JOptionPane.showInputDialog(this, "Telefono:");
                String fecha = JOptionPane.showInputDialog(this, "Fecha Nacimiento (YYYY-MM-DD) o vacio:");
                LocalDate f = (fecha==null||fecha.trim().isEmpty())?null:LocalDate.parse(fecha);
                pp.agregar(new Paciente(id,nombre,"",edad,tel,f));
                refreshPacientes();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
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
                int edad = Integer.parseInt(edadS);
                String tel = JOptionPane.showInputDialog(this, "Telefono:", pOld.getTelefono());
                pOld.setNombre(nombre); pOld.setEdad(edad); pOld.setTelefono(tel);
                pp.actualizar(pOld); refreshPacientes();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        return p;
    }

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

    private JPanel buildConsultasPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(consultasModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = new JPanel();
        JButton add = new JButton("Agregar");
        JButton del = new JButton("Eliminar");
        controls.add(add); controls.add(del);
        p.add(controls, BorderLayout.SOUTH);

        add.addActionListener(a->{
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog(this, "ID:" ));
                int pid = Integer.parseInt(JOptionPane.showInputDialog(this, "Paciente ID:" ));
                int did = Integer.parseInt(JOptionPane.showInputDialog(this, "Doctor ID:" ));
                String fecha = JOptionPane.showInputDialog(this, "Fecha y hora (YYYY-MM-DDTHH:MM)");
                LocalDateTime dt = fecha==null||fecha.isEmpty()?LocalDateTime.now():LocalDateTime.parse(fecha);
                String motivo = JOptionPane.showInputDialog(this, "Motivo:");
                String notas = JOptionPane.showInputDialog(this, "Notas:");
                pc.agregar(new Consulta(id,pid,did,dt,motivo,notas));
                refreshConsultas();
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

        return p;
    }

    private JPanel buildEquiposPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(equiposModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = new JPanel();
        JButton add = new JButton("Agregar");
        JButton toggle = new JButton("Toggle Disp");
        JButton del = new JButton("Eliminar");
        controls.add(add); controls.add(toggle); controls.add(del);
        p.add(controls, BorderLayout.SOUTH);

        add.addActionListener(a->{
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog(this, "ID:" ));
                String nombre = JOptionPane.showInputDialog(this, "Nombre:");
                String desc = JOptionPane.showInputDialog(this, "Descripcion:");
                pe.agregar(new Equipo(id,nombre,desc,true));
                refreshEquipos();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        toggle.addActionListener(a->{
            try {
                int row = table.getSelectedRow();
                if (row<0) { JOptionPane.showMessageDialog(this, "Selecciona un equipo"); return; }
                int id = Integer.parseInt(equiposModel.getValueAt(row,0).toString());
                var list = pe.obtenerTodos();
                for (Equipo e: list) if (e.getId()==id) e.setDisponible(!e.isDisponible());
                pe.saveAll(list); refreshEquipos();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        del.addActionListener(a->{
            try {
                int row = table.getSelectedRow();
                if (row<0) { JOptionPane.showMessageDialog(this, "Selecciona un equipo"); return; }
                int id = Integer.parseInt(equiposModel.getValueAt(row,0).toString());
                var list = pe.obtenerTodos();
                list.removeIf(e->e.getId()==id);
                pe.saveAll(list); refreshEquipos();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        return p;
    }

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
                int cant = Integer.parseInt(JOptionPane.showInputDialog(this, "Cantidad:"));
                String unidad = JOptionPane.showInputDialog(this, "Unidad:");
                pi.agregar(new InventarioItem(id,nombre,cant,unidad));
                refreshInventario();
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
                        int nueva = Integer.parseInt(JOptionPane.showInputDialog(this, "Nueva cantidad:", it.getCantidad()));
                        it.setCantidad(nueva);
                    }
                }
                pi.saveAll(list); refreshInventario();
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
            for (Paciente p: list) pacientesModel.addRow(new Object[]{p.getId(), p.getNombre(), p.getEdad(), p.getTelefono()});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
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

    private void refreshEquipos() {
        try {
            var list = pe.obtenerTodos();
            equiposModel.setRowCount(0);
            for (Equipo e: list) equiposModel.addRow(new Object[]{e.getId(), e.getNombre(), e.isDisponible()});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void refreshInventario() {
        try {
            var list = pi.obtenerTodos();
            inventarioModel.setRowCount(0);
            for (InventarioItem it: list) inventarioModel.addRow(new Object[]{it.getId(), it.getNombre(), it.getCantidad()});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new MainGUI().setVisible(true);
        });
    }
}
