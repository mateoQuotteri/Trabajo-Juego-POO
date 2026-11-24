package RPG.servicios;

import RPG.modelo.Party;
import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.enemigos.Jefe;
import RPG.modelo.enemigos.JefeFinal;
import RPG.modelo.items.Arma;
import RPG.modelo.items.Armadura;
import RPG.modelo.items.Escudo;
import RPG.modelo.items.Item;
import RPG.modelo.items.PocionMana;
import RPG.modelo.items.PocionVida;

public class GestorDeRecompensas {

    public Resultado entregar(Party party, Enemigo enemigo) {
        boolean esJefe = enemigo instanceof Jefe || enemigo instanceof JefeFinal;
        int oroGanado = esJefe ? 100 + (int) (Math.random() * 101)
                : 10 + (int) (Math.random() * 21);
        party.agregarOro(oroGanado);
        String recompensa = "Oro: +" + oroGanado + " (total: " + party.getOro() + ")";
        String log = recompensa + "\n";

        if (esJefe || Math.random() < 0.30) {
            Item item = generarItem();
            if (party.getInventario().agregarItem(item)) {
                recompensa = recompensa + "\nÍtem: " + item.getNombre();
                log = log + "Ítem obtenido: " + item.getNombre() + ".\n";
            } else {
                recompensa = recompensa + "\nÍtem no obtenido: inventario lleno";
                log = log + "El inventario está lleno; no se pudo guardar " + item.getNombre() + ".\n";
            }
        } else {
            recompensa = recompensa + "\nSin ítem adicional";
        }
        return new Resultado(log, recompensa);
    }

    private Item generarItem() {
        int seleccion = (int) (Math.random() * 11);
        if (seleccion < 4) {
            return new PocionVida();
        }
        if (seleccion < 7) {
            return new PocionMana();
        }
        if (seleccion < 9) {
            return new Escudo();
        }
        return seleccion == 9 ? new Arma() : new Armadura();
    }

    public static final class Resultado {
        private final String log;
        private final String resumen;

        private Resultado(String log, String resumen) {
            this.log = log;
            this.resumen = resumen;
        }

        public String getLog() {
            return log;
        }

        public String getResumen() {
            return resumen;
        }
    }
}
