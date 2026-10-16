package RPG.modelo.personajes;

import RPG.modelo.habilidades.AtaqueConMultiplicador;

// Tanque: vida y defensa altas, daño puramente físico, poco maná.
// Es el único que puede Defender.
public class Guerrero extends Personaje {

    // Valores de Nivel 1 (documento, tabla "Estadísticas Base").
    public Guerrero(String nombre) {
        super(nombre, 50, 20, 40, 0, 40, 20,
                new AtaqueConMultiplicador("Tajo", 10, 1.5, false),
                new AtaqueConMultiplicador("Golpe Poderoso", 20, 2.5, false));
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
