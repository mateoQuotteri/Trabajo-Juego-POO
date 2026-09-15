package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

// Personaje mágico ofensivo: es la clase que más aprovecha la Inteligencia
// en su habilidad especial. Es frágil en combate cuerpo a cuerpo (la Fuerza
// le rinde poco).
public class Mago extends Personaje {

    // nombre, vida, mana, ataque, defensa (los atributos los pone la clase
    // base, al azar, y se completan con el reparto de puntos).
    public Mago(String nombre){
        // Velocidad intermedia: 20.
        super(nombre, 100, 100, 10, 5, 20);
    }

    // ---------- Efectividad de cada atributo para esta clase ----------
    // La Fuerza y la Destreza (combate físico) le rinden poco: es un
    // personaje frágil cuerpo a cuerpo.
    @Override
    public double multiplicadorFuerza() {
        return 0.25;
    }

    @Override
    public double multiplicadorDestreza() {
        return 0.25;
    }

    // La Inteligencia es su atributo principal (multiplicador 1.0): de ahí
    // sale el daño de su habilidad especial.
    @Override
    public double multiplicadorInteligencia() {
        return 1.0;
    }

    // El Espiritu (magia de soporte) le rinde a medias, porque comparte
    // algo de esa afinidad mágica con el Sacerdote.
    @Override
    public double multiplicadorEspiritu() {
        return 0.5;
    }

    // Habilidad especial del Mago: un hechizo ofensivo basado en su
    // Inteligencia (con el multiplicador de esta clase, que es 1.0) y su nivel.
    @Override
    public void usarHabilidad(Enemigo objetivo){
        int danio = bonoDe(getInteligencia(), multiplicadorInteligencia()) * getNivel();
        objetivo.recibirDanio(danio);
    }
}
