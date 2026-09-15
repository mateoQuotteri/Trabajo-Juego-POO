package RPG.Personajes.ImplPersonaje;

import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;

// Personaje ágil (arquero): es la clase que más aprovecha la Destreza en su
// habilidad especial. El ataque básico (heredado de Personaje) sigue
// usando Fuerza para todas las clases, pero al Cazador le rinde solo a la
// mitad.
public class Cazador extends Personaje {

    // nombre, vida, mana, ataque, defensa (los atributos los pone la clase
    // base, al azar, y se completan con el reparto de puntos).
    public Cazador(String nombre){
        // Es el más veloz de las 4 clases (arquero ágil): velocidad 25.
        super(nombre, 200, 50, 25, 50, 25);
    }

    // ---------- Efectividad de cada atributo para esta clase ----------
    // La Fuerza le sirve solo a medias en el ataque cuerpo a cuerpo.
    @Override
    public double multiplicadorFuerza() {
        return 0.5;
    }

    // La Destreza es su atributo principal (multiplicador 1.0).
    @Override
    public double multiplicadorDestreza() {
        return 1.0;
    }

    // La magia (Inteligencia/Espiritu) casi no le aporta nada.
    @Override
    public double multiplicadorInteligencia() {
        return 0.25;
    }

    @Override
    public double multiplicadorEspiritu() {
        return 0.25;
    }

    // Habilidad especial del Cazador: un disparo preciso basado en su
    // Destreza (con el multiplicador de esta clase, que es 1.0) y su nivel.
    @Override
    public void usarHabilidad(Enemigo objetivo){
        int danio = bonoDe(getDestreza(), multiplicadorDestreza()) * getNivel();
        objetivo.recibirDanio(danio);
    }
}
