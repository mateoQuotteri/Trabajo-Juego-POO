package RPG.modelo.enemigos;

import RPG.modelo.personajes.Personaje;

// Enemigo común. Documento: "70% ataca / 30% intenta aturdir".
// Stats piso 1: Vida 35, Ataque 20, Defensa 12, Velocidad 28.
public class Goblin extends Enemigo {

    private static final String[] ACCIONES = {
            "atacar", "atacar", "atacar", "atacar", "atacar", "atacar", "atacar", // 70%
            "aturdir", "aturdir", "aturdir"                                       // 30%
    };

    // vida, ataque, defensa, velocidad, experiencia que otorga
    public Goblin(String nombre) {
        super(nombre, 35, 20, 12, 28, 50);
    }

    @Override
    public String actuar(Personaje objetivo) {
        return ejecutarAccionSobre(elegirAccionPonderada(ACCIONES), objetivo);
    }
}
