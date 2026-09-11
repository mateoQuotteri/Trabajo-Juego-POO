package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

public class Guerrero extends Personaje {

    private int fuerza;

    public Guerrero(String nombre){
        super(nombre, 200, 50, 25, 50);
        this.fuerza = 20;
    }

    @Override
    public void usarHabilidad(Enemigo objetivo){
        objetivo.recibirDanio(fuerza * getNivel());
    }
}
