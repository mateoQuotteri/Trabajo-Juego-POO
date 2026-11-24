package RPG.modelo.items;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

public class Arma extends Item {

    private static final int BONUS_ATAQUE_FISICO = 10;

    public Arma() {
        super("Espada de hierro", "Aumenta en 10 el ataque físico del personaje equipado.");
    }

    public int getBonusAtaqueFisico() {
        return BONUS_ATAQUE_FISICO;
    }

    @Override
    public void usar(Combatiente objetivo) {
        if (!(objetivo instanceof Personaje)) {
            throw new IllegalArgumentException("Un arma solo se puede equipar a un personaje.");
        }
        Personaje personaje = (Personaje) objetivo;
        if (personaje.getArmaEquipada() != null) {
            throw new IllegalStateException("El personaje ya tiene un arma equipada.");
        }
        personaje.equiparArma(this);
    }
}
