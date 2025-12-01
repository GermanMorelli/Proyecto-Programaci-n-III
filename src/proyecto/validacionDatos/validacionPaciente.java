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
public class validacionPaciente {
    
        // Regex: solo letras (incluye acentos y ñ), espacios intermedios, máximo 50 chars
    private static final Pattern NOMBRE_PACIENTE_PATTERN = Pattern.compile(
        "^(?=.{1,50}$)[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?: [A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*$"
    );

    public static boolean validaNombrePaciente(String nombre) {
        if (nombre == null) return false;
        return NOMBRE_PACIENTE_PATTERN.matcher(nombre.trim()).matches();
    }
    
    public static boolean validaEdad(int edad) {
        return edad >= 0 && edad <= 120;
    }
    
    // Permite letras, números, espacios, comas, puntos y guiones
    private static final Pattern DIRECCION_PATTERN = Pattern.compile(
        "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 ,.-]{1,100}$"
    );

    public static boolean validaDireccion(String direccion) {
        if (direccion == null) return false;
        return DIRECCION_PATTERN.matcher(direccion.trim()).matches();
    }

}
