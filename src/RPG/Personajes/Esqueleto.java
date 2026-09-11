package RPG.Personajes;

public class Esqueleto extends Enemigo {

    public Esqueleto(String nombre) {
        super(nombre, 80, 15);
    }

    @Override
    public void usarHabilidad(Personaje objetivo){
        objetivo.recibirDanio(25 * getNivel());
    }
}