package RPG.modelo.items;

import RPG.modelo.Combatiente;

public class PocionVida  extends  Consumible{

    public PocionVida() {
        super("Pocion de vida", "Recupera hasta 30 puntos de vida");
    }

    @Override
    public void usar(Combatiente objetivo) {
        objetivo.recibirCuracion(30);
    }

    @Override
    public boolean tieneEfectoEn(Combatiente objetivo) {
        return objetivo.estaVivo() && objetivo.getVidaActual() < objetivo.getVidaMaxima();
    }
}
