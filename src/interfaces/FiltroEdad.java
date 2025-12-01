package interfaces; 

import proyecto.model.Paciente;

@FunctionalInterface
public interface FiltroEdad {
    // Definimos el contrato: recibe un paciente y una edad mínima
    boolean validar(Paciente p, int edadMinima);
}