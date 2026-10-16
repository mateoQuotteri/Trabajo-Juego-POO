package RPG.consola;

import RPG.controlador.ControladorDeBatalla;
import RPG.modelo.Party;
import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.personajes.Personaje;
import java.util.Scanner;

// "Vista" de prueba por CONSOLA. Sirve para probar rápido la lógica sin
// abrir la ventana de Swing. Reemplaza a la vieja combinación
// Main.java + MotorDeCombate.java.
//
// Lo importante: usa EXACTAMENTE el mismo ControladorDeBatalla que la
// pantalla de Swing. Si mañana se cambia una regla del juego, se cambia en
// el modelo y las dos vistas la toman sin tocar nada acá (eso es MVC).
//
// Para correrla: ejecutar el main de esta clase (en IntelliJ: clic derecho
// -> Run 'JuegoConsola.main()').
public class JuegoConsola {

    private ControladorDeBatalla controlador;
    private Scanner scanner;

    public JuegoConsola() {
        controlador = new ControladorDeBatalla();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new JuegoConsola().jugar();
    }

    public void jugar() {
        System.out.println("=== RPG - El Ascenso a la Torre (modo consola de prueba) ===");
        System.out.print("¿En qué piso querés probar una batalla? (1 a 10): ");
        int piso = pedirNumero(1, 10);

        Party party = ControladorDeBatalla.crearPartyDePrueba();
        System.out.print(controlador.iniciarBatalla(party, piso));

        while (!controlador.batallaTerminada()) {
            mostrarEstado();
            System.out.print(jugarTurnoDelPersonaje(controlador.getPersonajeEnTurno()));
        }
        mostrarEstado();
    }

    private String jugarTurnoDelPersonaje(Personaje actor) {
        Habilidad basica = actor.getHabilidadBasica();
        Habilidad poderosa = actor.getHabilidadPoderosa();

        System.out.println();
        System.out.println("Turno de " + actor.getNombre() + " (" + actor.getNombreClase() + "). Elegí:");
        System.out.println("  1) Atacar");
        System.out.println("  2) " + basica.getNombre() + " (" + basica.getCostoMana() + " maná)"
                + (controlador.puedeUsarHabilidad(false) ? "" : " [BLOQUEADA]"));
        System.out.println("  3) " + poderosa.getNombre() + " (" + poderosa.getCostoMana() + " maná)"
                + (controlador.puedeUsarHabilidad(true) ? "" : " [BLOQUEADA]"));
        int maximo = 3;
        if (actor.puedeDefender()) {
            System.out.println("  4) Defender");
            maximo = 4;
        }

        int opcion = pedirNumero(1, maximo);
        if (opcion == 1) {
            return controlador.atacar();
        }
        if (opcion == 4) {
            return controlador.defender();
        }
        boolean esPoderosa = (opcion == 3);
        Habilidad elegida = esPoderosa ? poderosa : basica;
        Personaje aliado = null;
        if (elegida.esDeApoyo()) {
            aliado = elegirAliado(actor);
        }
        return controlador.usarHabilidad(esPoderosa, aliado);
    }

    // Lista a TODOS los miembros (también los caídos, para poder usar Revivir).
    private Personaje elegirAliado(Personaje actor) {
        Personaje[] miembros = controlador.getBatalla().getParty().getMiembros();
        System.out.println("¿Sobre quién?");
        for (int i = 0; i < miembros.length; i++) {
            String marca = (miembros[i] == actor) ? " (vos)" : "";
            String caido = miembros[i].estaVivo() ? "" : " [CAÍDO]";
            System.out.println("  " + (i + 1) + ") " + miembros[i].getNombre() + " "
                    + miembros[i].getVidaActual() + "/" + miembros[i].getVidaMaxima() + marca + caido);
        }
        return miembros[pedirNumero(1, miembros.length) - 1];
    }

    private void mostrarEstado() {
        Enemigo enemigo = controlador.getBatalla().getEnemigo();
        System.out.println();
        System.out.println("--- Ronda " + controlador.getBatalla().getNumeroRonda() + " | Orden: "
                + controlador.getBatalla().describirOrdenDeLaRonda() + " ---");
        System.out.println(enemigo.getNombre() + ": " + enemigo.getVidaActual() + "/" + enemigo.getVidaMaxima() + " de vida");
        for (Personaje p : controlador.getBatalla().getParty().getMiembros()) {
            System.out.println("  " + p.getNombre() + " (" + p.getNombreClase() + " nv " + p.getNivel() + "): Vida "
                    + p.getVidaActual() + "/" + p.getVidaMaxima() + ", Maná " + p.getManaActual() + "/"
                    + p.getManaMaximo() + p.describirEstados());
        }
    }

    // Pide un número entre min y max. Si escriben algo que no es número,
    // se descarta y se vuelve a pedir (antes nextInt() tiraba excepción).
    private int pedirNumero(int min, int max) {
        while (true) {
            if (scanner.hasNextInt()) {
                int numero = scanner.nextInt();
                if (numero >= min && numero <= max) {
                    return numero;
                }
            } else {
                scanner.next();
            }
            System.out.print("Opción inválida. Elegí entre " + min + " y " + max + ": ");
        }
    }
}
