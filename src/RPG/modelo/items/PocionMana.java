package RPG.modelo.items;


import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

// Documento 1.5: "Poción de mana: recupera 20 de mana".
public class PocionMana extends Consumible {

    public PocionMana() {
        super("Poción de Mana", "Recupera hasta 20 puntos de mana.");
    }

    // Pisa la regla general: además de estar vivo, le tiene que faltar vida.
    @Override                                              // Pisa la regla general de Consumible ("está vivo")
    public boolean tieneEfectoEn(Combatiente objetivo) {   // Recibe un Combatiente (puede ser personaje o enemigo)
        return objetivo.estaVivo()                         // Filtro 1: tiene que estar vivo
                && objetivo instanceof Personaje           // Filtro 2: ¿es un personaje? (Es vital que se personaje ya que los enemigos no tienen mana)
                && ((Personaje) objetivo).getManaActual()  // Filtro 3: cast a Personaje para leer su maná actual...
                < ((Personaje) objetivo).getManaMaximo(); // ...y ver si es menor que el máximo (le falta maná) - Si su mana actual es igual a su maximo no le falta mana
    }

    @Override
    public void usar(Combatiente objetivo) {

        if (objetivo instanceof  Personaje){   // Confirmamos que el objetivo es un personaje y NO un enemigo - Recordemos que los enemigos no tienen mana
            // Casteamos a Perosnaje y le sumamos a 20 de mana
            ((Personaje) objetivo).recuperarMana(20);
        }
    }
}