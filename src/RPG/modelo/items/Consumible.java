package RPG.modelo.items;

import RPG.modelo.Combatiente;

public abstract class Consumible extends Item {


    public Consumible(String nombre, String descripcion) {
        super(nombre, descripcion);
     }

    public boolean tieneEfectoEn(Combatiente objetivo) {
        if (objetivo.estaVivo()){
            return true;
        }
        return false;
    }
}