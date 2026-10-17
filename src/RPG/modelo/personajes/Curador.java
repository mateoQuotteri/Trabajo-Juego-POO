package RPG.modelo.personajes;

import RPG.modelo.habilidades.HabilidadCuracion;

// Ataque bajo, mucho maná. Sus habilidades curan a un aliado.
public class Curador extends Personaje {

    public Curador(String nombre) {
        super(nombre, 35, 40, 15, 20, 22, 28,
                new HabilidadCuracion("Sanación", 10, 20),
                new HabilidadCuracion("Sanación Mayor", 30, 45));
    }

    @Override
    public String getNombreClase() {
        return "Curador";
    }
}
