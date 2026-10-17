package RPG.modelo.personajes;

import RPG.modelo.habilidades.HabilidadAtaque;

// Poca vida y defensa, el mayor ataque mágico y mucho maná.
public class Mago extends Personaje {

    public Mago(String nombre) {
        super(nombre, 25, 45, 10, 40, 15, 25,
                new HabilidadAtaque("Chispa", 10, 1.5, true),
                new HabilidadAtaque("Bola de Fuego", 30, 2.5, true));
    }

    @Override
    public String getNombreClase() {
        return "Mago";
    }
}
