package RPG.modelo.personajes;

import RPG.modelo.habilidades.Curacion;

// Ataque bajo y maná alto. Sus 2 habilidades son de APOYO (apuntan a un
// aliado): Habilidad.esDeApoyo() devuelve true y el controlador le ofrece
// aliados como objetivo, sin preguntar "¿es un Curador?".
// Documento: Sanación (cura 20, 10 MP) y Sanación Mayor (cura 45, 30 MP).
public class Curador extends Personaje {

    public Curador(String nombre) {
        super(nombre, 35, 40, 15, 20, 22, 28,
                new Curacion("Sanación", 10, 20),
                new Curacion("Sanación Mayor", 30, 45));
    }

    @Override
    public String getNombreClase() {
        return "Curador";
    }
}
