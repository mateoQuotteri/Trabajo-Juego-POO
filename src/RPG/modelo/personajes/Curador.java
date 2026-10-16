package RPG.modelo.personajes;

import RPG.modelo.habilidades.Revivir;
import RPG.modelo.habilidades.Sanacion;

// Ataque bajo y maná alto. Sus 2 habilidades son de APOYO (apuntan a un
// aliado): Habilidad.esDeApoyo() devuelve true y el controlador le ofrece
// aliados como objetivo, sin necesidad de preguntar "¿es un Curador?".
public class Curador extends Personaje {

    public Curador(String nombre) {
        super(nombre, 35, 40, 15, 20, 22, 28,
                new Sanacion(),
                new Revivir());
    }

    @Override
    public String getNombreClase() {
        return "Curador";
    }
}
