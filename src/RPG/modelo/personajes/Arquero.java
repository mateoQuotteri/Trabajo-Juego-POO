package RPG.modelo.personajes;

import RPG.modelo.habilidades.AtaqueConMultiplicador;
import RPG.modelo.habilidades.DisparoCertero;

// Velocidad muy por encima del resto (actúa primero casi siempre), daño físico.
public class Arquero extends Personaje {

    public Arquero(String nombre) {
        super(nombre, 30, 20, 35, 5, 20, 45,
                new DisparoCertero(),
                new AtaqueConMultiplicador("Lluvia de Flechas", 20, 2.5, false));
    }

    @Override
    public String getNombreClase() {
        return "Arquero";
    }
}
