package RPG.modelo.enemigos;

import RPG.modelo.personajes.Personaje;

// Enemigo común. Documento: "Solo ataca, nunca aturde".
// Stats piso 1: Vida 25, Ataque 12, Defensa 5, Velocidad 12.
public class Slime extends Enemigo {

    // vida, ataque, defensa, velocidad, experiencia que otorga
    public Slime(String nombre) {
        super(nombre, 25, 12, 5, 12, 30);
    }

    // Su "lista ponderada" sería 10 veces "atacar": se simplifica a atacar
    // siempre. Es igual un comportamiento distinto al del Goblin y los jefes.
    @Override
    public String actuar(Personaje objetivo) {
        return atacar(objetivo);
    }
}
