package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

public class Sacerdote extends Personaje {

    private int espiritu;

    public Sacerdote(String nombre){
        super(nombre, 120, 300, 15, 20);
        this.espiritu = 20;
    }

    @Override
    public void usarHabilidad(Enemigo objetivo){
        objetivo.recibirDanio(espiritu * getNivel());
    }
}