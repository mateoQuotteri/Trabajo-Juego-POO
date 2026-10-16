package RPG.consola;

import RPG.controlador.ControladorDeBatalla;
import RPG.controlador.ControladorDeJuego;
import RPG.modelo.Partida;
import RPG.modelo.ResultadoDeBatalla;
import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.personajes.Personaje;
import java.util.Scanner;

// "Vista" de prueba por CONSOLA: juega una partida entera (piso 1 al 10)
// sin abrir Swing. Usa EXACTAMENTE los mismos controladores que la
// ventana: si se cambia una regla en el modelo, las dos vistas la toman
// sin tocar nada acá (eso es MVC).
//
// Para correrla: Run 'JuegoConsola.main()' en IntelliJ.
public class JuegoConsola {

    private ControladorDeJuego juego;
    private ControladorDeBatalla batalla;
    private Scanner scanner;

    public JuegoConsola() {
        juego = new ControladorDeJuego();
        batalla = juego.getControladorDeBatalla();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new JuegoConsola().jugar();
    }

    public void jugar() {
        System.out.println("=== RPG - El Ascenso a la Torre (modo consola de prueba) ===");
        juego.nuevaPartida();
        Partida partida = juego.getPartida();

        while (!partida.estaTerminada()) {
            System.out.print(juego.empezarBatallaDelPisoActual());
            while (!batalla.batallaTerminada()) {
                mostrarEstado();
                System.out.print(jugarTurnoDelPersonaje(batalla.getPersonajeEnTurno()));
            }
            ResultadoDeBatalla resultado = juego.cerrarBatalla();
            System.out.println();
            System.out.println("=== Resumen piso " + resultado.getPiso() + ": "
                    + (resultado.ganoLaParty() ? "VICTORIA" : "DERROTA") + " en "
                    + resultado.getRondasJugadas() + " rondas ===");
        }
        System.out.println(partida.estaGanada() ? "¡Completaron la Torre!" : "Fin de la partida.");
    }

    private String jugarTurnoDelPersonaje(Personaje actor) {
        Habilidad basica = actor.getHabilidadBasica();
        Habilidad poderosa = actor.getHabilidadPoderosa();

        System.out.println();
        System.out.println("Turno de " + actor.getNombre() + " (" + actor.getNombreClase() + "). Elegí:");
        System.out.println("  1) Atacar");
        System.out.println("  2) " + basica.getNombre() + " (" + basica.getCostoMana() + " maná)"
                + (batalla.puedeUsarHabilidad(false) ? "" : " [BLOQUEADA]"));
        System.out.println("  3) " + poderosa.getNombre() + " (" + poderosa.getCostoMana() + " maná)"
                + (batalla.puedeUsarHabilidad(true) ? "" : " [BLOQUEADA]"));
        int maximo = 3;
        if (actor.puedeDefender()) {
            System.out.println("  4) Defender");
            maximo = 4;
        }

        int opcion = pedirNumero(1, maximo);
        if (opcion == 1) {
            return batalla.atacar();
        }
        if (opcion == 4) {
            return batalla.defender();
        }
        boolean esPoderosa = (opcion == 3);
        Habilidad elegida = esPoderosa ? poderosa : basica;
        Personaje aliado = null;
        if (elegida.esDeApoyo()) {
            aliado = elegirAliado();
        }
        return batalla.usarHabilidad(esPoderosa, aliado);
    }

    private Personaje elegirAliado() {
        Personaje[] miembros = batalla.getBatalla().getParty().getMiembros();
        System.out.println("¿Sobre quién?");
        for (int i = 0; i < miembros.length; i++) {
            System.out.println("  " + (i + 1) + ") " + miembros[i].getNombre() + " "
                    + miembros[i].getVidaActual() + "/" + miembros[i].getVidaMaxima()
                    + (miembros[i].estaVivo() ? "" : " [CAÍDO]"));
        }
        return miembros[pedirNumero(1, miembros.length) - 1];
    }

    private void mostrarEstado() {
        Enemigo enemigo = batalla.getBatalla().getEnemigo();
        System.out.println();
        System.out.println("--- Piso " + batalla.getBatalla().getPiso() + " | Ronda " + batalla.getBatalla().getNumeroRonda()
                + " | Orden: " + batalla.getBatalla().describirOrdenDeLaRonda() + " ---");
        System.out.println(enemigo.getNombre() + ": " + enemigo.getVidaActual() + "/" + enemigo.getVidaMaxima() + " de vida");
        for (Personaje p : batalla.getBatalla().getParty().getMiembros()) {
            System.out.println("  " + p.getNombre() + " (nv " + p.getNivel() + "): Vida "
                    + p.getVidaActual() + "/" + p.getVidaMaxima() + ", Maná " + p.getManaActual() + "/"
                    + p.getManaMaximo() + p.describirEstados());
        }
    }

    // Pide un número entre min y max; si escriben otra cosa, vuelve a pedir.
    private int pedirNumero(int min, int max) {
        while (true) {
            if (scanner.hasNextInt()) {
                int numero = scanner.nextInt();
                if (numero >= min && numero <= max) {
                    return numero;
                }
            } else if (scanner.hasNext()) {
                scanner.next();
            } else {
                System.exit(0); // se terminó la entrada (útil para pruebas automáticas)
            }
            System.out.print("Opción inválida. Elegí entre " + min + " y " + max + ": ");
        }
    }
}
