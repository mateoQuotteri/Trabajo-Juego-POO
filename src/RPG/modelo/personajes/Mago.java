package RPG.modelo.personajes;

import RPG.modelo.habilidades.AtaqueConMultiplicador;

// Vida y defensa bajísimas, el mayor ataque mágico y mucho maná.
public class Mago extends Personaje {

    public Mago(String nombre) {
        super(nombre, 25, 45, 10, 40, 15, 25,
                new AtaqueConMultiplicador("Chispa", 10, 1.5, true),
                new AtaqueConMultiplicador("Bola de Fuego", 30, 2.5, true));
    }

    @Override
    public String getNombreClase() {
        return "Mago";
    }
}
