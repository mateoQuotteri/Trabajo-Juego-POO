package RPG.modelo.items;

import RPG.modelo.Combatiente;
import RPG.modelo.personajes.Personaje;

public class Consumible extends Item {
    private int curacionVida;
    private int recuperacionMana;

    public Consumible(String nombre, String descripcion, int curacionVida, int recuperacionMana) {
        super(nombre, descripcion);
        this.curacionVida = Math.max(0, curacionVida);
        this.recuperacionMana = Math.max(0, recuperacionMana);
    }

    public boolean tieneEfectoEn(Combatiente objetivo) {
        boolean puedeCurar = curacionVida > 0 && objetivo.getVidaActual() < objetivo.getVidaMaxima();
        boolean puedeRecuperarMana = recuperacionMana > 0
                && objetivo instanceof Personaje
                && ((Personaje) objetivo).getManaActual() < ((Personaje) objetivo).getManaMaximo();
        return puedeCurar || puedeRecuperarMana;
    }

    @Override
    public void usar(Combatiente objetivo) {
        if (curacionVida > 0) {
            objetivo.recibirCuracion(curacionVida);
        }
        if (recuperacionMana > 0 && objetivo instanceof Personaje) {
            ((Personaje) objetivo).recuperarMana(recuperacionMana);
        }
    }
}