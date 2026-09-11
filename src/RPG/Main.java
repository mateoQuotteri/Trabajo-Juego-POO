package RPG;

import RPG.Personajes.*;
import RPG.Personajes.ImplPersonaje.Cazador;
import RPG.Personajes.ImplPersonaje.Guerrero;
import RPG.Personajes.ImplPersonaje.Mago;
import RPG.Personajes.ImplPersonaje.Sacerdote;

public class Main {

    public static void main(String[] args) {

        Party party = new Party();

        Batalla batalla = new Batalla("Bosque de Elwynn", party);

        Guerrero Mateo = new Guerrero("Mateo");
        Mago Jose = new Mago("Jose");
        Cazador Conrado = new Cazador("Conrado");
        Sacerdote Evelyn = new Sacerdote("Evelyn");

        party.agregarMiembro(Mateo);
        party.agregarMiembro(Jose);
        party.agregarMiembro(Conrado);
        party.agregarMiembro(Evelyn);

        Esqueleto esqueleto1 = new Esqueleto("Esqueleto 1");
        Esqueleto esqueleto2 = new Esqueleto("Esqueleto 2");
        Esqueleto esqueleto3 = new Esqueleto("Esqueleto 3");
        Esqueleto esqueleto4 = new Esqueleto("Esqueleto 4");

        batalla.agregarEnemigo(esqueleto1);
        batalla.agregarEnemigo(esqueleto2);
        batalla.agregarEnemigo(esqueleto3);
        batalla.agregarEnemigo(esqueleto4);

        Mateo.atacar(esqueleto1);

        System.out.println(esqueleto1.getVida());

    }
}