package RPG;

import RPG.Personajes.*;
import RPG.Personajes.ImplPersonaje.Cazador;
import RPG.Personajes.ImplPersonaje.Guerrero;
import RPG.Personajes.ImplPersonaje.Mago;
import RPG.Personajes.ImplPersonaje.Sacerdote;
import java.util.Scanner;

// Punto de entrada del juego. Por ahora es solo una prueba manual: arma una
// party fija, reparte atributos, arma una batalla con enemigos fijos y
// prueba un ataque. Más adelante esto se va a reemplazar por el motor de
// combate por turnos real (ver checklist-tpo.txt).
public class Main {

    public static void main(String[] args) {

        // Un solo Scanner para todo el juego (se lo pasamos a quien lo
        // necesite, como RepartidorAtributos, en vez de crear uno nuevo
        // cada vez).
        Scanner scanner = new Scanner(System.in);
        RepartidorAtributos repartidorAtributos = new RepartidorAtributos(scanner);
        RepartidorHabilidades repartidorHabilidades = new RepartidorHabilidades(scanner);

        Party party = new Party();

        Batalla batalla = new Batalla("Bosque de Elwynn", party);

        // Se crea un personaje de cada clase (cada uno con nombre de un
        // integrante del grupo). Al crearse, cada uno ya tiene su base
        // aleatoria de atributos (1 a 4) asignada sola.
        Guerrero Mateo = new Guerrero("Mateo");
        Mago Jose = new Mago("Jose");
        Cazador Conrado = new Cazador("Conrado");
        Sacerdote Evelyn = new Sacerdote("Evelyn");

        // Al iniciar la partida, cada personaje reparte sus puntos de
        // atributo por consola (además de la base aleatoria de arriba).
        repartidorAtributos.repartirAlCrear(Mateo);
        repartidorAtributos.repartirAlCrear(Jose);
        repartidorAtributos.repartirAlCrear(Conrado);
        repartidorAtributos.repartirAlCrear(Evelyn);

        // Se arma la party con los 4 personajes ya creados.
        party.agregarMiembro(Mateo);
        party.agregarMiembro(Jose);
        party.agregarMiembro(Conrado);
        party.agregarMiembro(Evelyn);

        // Se crean 4 enemigos (todos del mismo tipo por ahora: Esqueleto)
        // y se agregan a la batalla.
        Esqueleto esqueleto1 = new Esqueleto("Esqueleto 1");
        Esqueleto esqueleto2 = new Esqueleto("Esqueleto 2");
        Esqueleto esqueleto3 = new Esqueleto("Esqueleto 3");
        Esqueleto esqueleto4 = new Esqueleto("Esqueleto 4");

        batalla.agregarEnemigo(esqueleto1);
        batalla.agregarEnemigo(esqueleto2);
        batalla.agregarEnemigo(esqueleto3);
        batalla.agregarEnemigo(esqueleto4);

        // Prueba manual: Mateo ataca al primer esqueleto y se imprime la
        // vida que le queda (todavía no hay un ciclo de turnos real).
        Mateo.atacar(esqueleto1);

        System.out.println(esqueleto1.getVida());

        // Ejemplo de subida de nivel: se suma un nuevo reparto (5 puntos
        // por atributo) a lo que Mateo ya tenía, y se gasta el punto de
        // habilidad que ganó al subir de nivel (sus habilidades todavía son
        // genéricas "a definir", pero el mecanismo funciona igual).
        Mateo.subirNivel();
        repartidorAtributos.repartirPorNivel(Mateo);
        repartidorHabilidades.repartirHabilidad(Mateo);

        // Mismo ejemplo con Evelyn (Sacerdote), que sí tiene sus 4
        // habilidades reales ya definidas (Curación, Buff de Ataque, Buff
        // de Defensa y la Ulti de revivir).
        Evelyn.subirNivel();
        repartidorAtributos.repartirPorNivel(Evelyn);
        repartidorHabilidades.repartirHabilidad(Evelyn);

    }
}
