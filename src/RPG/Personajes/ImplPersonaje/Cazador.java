package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

public class Cazador extends Personaje {

    private int destreza;

    public Cazador(String nombre){
        super(nombre, 200, 50, 25, 50);
        this.destreza = 20;
    }

    @Override
    public void usarHabilidad(Enemigo objetivo){
        objetivo.recibirDanio(destreza * getNivel());
    }
}
