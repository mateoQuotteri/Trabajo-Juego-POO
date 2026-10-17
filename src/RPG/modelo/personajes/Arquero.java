package RPG.modelo.personajes;

import RPG.modelo.habilidades.HabilidadAtaque;

// El más rápido (casi siempre actúa primero), daño físico.
public class Arquero extends Personaje {

    public Arquero(String nombre) {
        super(nombre, 30, 20, 35, 5, 20, 45,
                new HabilidadAtaque("Disparo Certero", 10, 1.5, false),
                new HabilidadAtaque("Lluvia de Flechas", 20, 2.5, false));
    }

    @Override
    public String getNombreClase() {
        return "Arquero";
    }
}
