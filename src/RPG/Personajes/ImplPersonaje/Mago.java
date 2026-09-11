package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

public class Mago extends Personaje {

    private int inteligencia;

    public Mago(String nombre){
        super(nombre, 100, 100, 10, 5);
        this.inteligencia = 20;
    }

    @Override
    public void usarHabilidad(Enemigo objetivo){
        objetivo.recibirDanio(inteligencia * getNivel());
    }
}
