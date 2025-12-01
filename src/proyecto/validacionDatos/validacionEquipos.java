/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.validacionDatos;

import java.util.regex.Pattern;

/**
 *
 * @author PC
 */
public class validacionEquipos {
    
        // Permite letras (con acentos y ñ) y números, hasta 30 caracteres
    private static final Pattern NOMBRE_EQUIPO_PATTERN = Pattern.compile(
        "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9]{1,30}$"
    );

    public static boolean validaNombreEquipo(String nombre) {
        if (nombre == null) return false;
        return NOMBRE_EQUIPO_PATTERN.matcher(nombre.trim()).matches();
    }

    public static boolean validaCantidadEquipo(int cantidad) {
        return cantidad > 0;
    }

}
