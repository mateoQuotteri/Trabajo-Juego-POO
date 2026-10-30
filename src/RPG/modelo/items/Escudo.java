package RPG.modelo.items;

import RPG.modelo.Combatiente;

public class Escudo extends Consumible {

    public Escudo() {
        super("Escudo", "Reduce a la mitad el daño recibido durante 2 turnos.");
    }

    @Override
    public void usar(Combatiente objetivo) {
        objetivo.aplicarEscudo(2);
    }

    @Override
    public boolean tieneEfectoEn(Combatiente objetivo) {
        return objetivo.estaVivo() && objetivo.getTurnosEscudoRestantes() == 0;
    }
}
