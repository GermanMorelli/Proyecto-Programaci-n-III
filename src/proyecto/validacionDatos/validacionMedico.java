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
public class validacionMedico {
    
        // Permite "Dr." o "Dra." seguido de nombres con letras y espacios, hasta 50 caracteres
    private static final Pattern NOMBRE_MEDICO_PATTERN = Pattern.compile(
        "^(Dr\\.?|Dra\\.?)\\s[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?: [A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*$"
    );

    public static boolean validaNombreMedico(String nombre) {
        if (nombre == null || nombre.length() > 50) return false;
        return NOMBRE_MEDICO_PATTERN.matcher(nombre.trim()).matches();
    }
    
        // Solo letras y espacios, hasta 30 caracteres
    private static final Pattern ESPECIALIDAD_PATTERN = Pattern.compile(
        "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?: [A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*$"
    );

    public static boolean validaEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.length() > 30) return false;
        return ESPECIALIDAD_PATTERN.matcher(especialidad.trim()).matches();
    }


}
