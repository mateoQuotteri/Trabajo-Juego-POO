package RPG.modelo.items;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

public class Armadura extends Item {

    private static final int BONUS_DEFENSA = 10;

    public Armadura() {
        super("Armadura de cuero", "Aumenta en 10 la defensa del personaje equipado.");
    }

    public int getBonusDefensa() {
        return BONUS_DEFENSA;
    }

    @Override
    public void usar(Combatiente objetivo) {
        if (!(objetivo instanceof Personaje)) {
            throw new IllegalArgumentException("Una armadura solo se puede equipar a un personaje.");
        }
        Personaje personaje = (Personaje) objetivo;
        if (personaje.getArmaduraEquipada() != null) {
            throw new IllegalStateException("El personaje ya tiene una armadura equipada.");
        }
        personaje.equiparArmadura(this);
    }
}
