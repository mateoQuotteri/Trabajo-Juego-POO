package RPG.modelo.personajes;

import RPG.modelo.habilidades.AtaqueConMultiplicador;

// Velocidad muy por encima del resto (actúa primero casi siempre), daño físico.
// Documento: Disparo Certero (físico x1.5, 10 MP) y Lluvia de Flechas
// (físico x2.5, 20 MP).
public class Arquero extends Personaje {

    public Arquero(String nombre) {
        super(nombre, 30, 20, 35, 5, 20, 45,
                new AtaqueConMultiplicador("Disparo Certero", 10, 1.5, false),
                new AtaqueConMultiplicador("Lluvia de Flechas", 20, 2.5, false));
    }

    @Override
    public String getNombreClase() {
        return "Arquero";
    }
}
