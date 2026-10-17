package RPG.modelo.personajes;

import RPG.modelo.habilidades.HabilidadAtaque;

// Tanque: mucha vida y defensa, daño físico. Único que puede Defender.
public class Guerrero extends Personaje {

    // nombre, vida, maná, ataque físico, ataque mágico, defensa, velocidad
    // (tabla "Estadísticas Base" del documento)
    public Guerrero(String nombre) {
        super(nombre, 50, 20, 40, 0, 40, 20,
                new HabilidadAtaque("Tajo", 10, 1.5, false),
                new HabilidadAtaque("Golpe Poderoso", 20, 2.5, false));
    }

    @Override
    public boolean puedeDefender() {
        return true;
    }

    @Override
    public String getNombreClase() {
        return "Guerrero";
    }
}
