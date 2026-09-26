package RPG;

import RPG.Personajes.Combatiente;
import RPG.Personajes.Enemigo;
import RPG.Personajes.Personaje;
import RPG.Personajes.ImplPersonaje.Curador;
import RPG.Personajes.ImplPersonaje.Guerrero;
import java.util.Scanner;

// Controlador (GRASP Controller) que orquesta una Batalla por turnos: arma
// el orden de cada ronda según Velocidad, le pregunta al jugador qué hace
// cada personaje vivo por consola, decide automáticamente qué hace cada
// enemigo vivo, y repite ronda tras ronda hasta que gane la party o los
// enemigos.
//
// Es una clase aparte (no vive ni en Personaje ni en Batalla) por el mismo
// motivo que ya usábamos con RepartidorAtributos/RepartidorHabilidades
// (eliminados): no mezclar la lógica de "quién gana la batalla" con los
// datos de cada combatiente, ni con la entrada/salida por consola.
public class MotorDeCombate {

    private Batalla batalla;
    private Scanner scanner;

    public MotorDeCombate(Batalla batalla, Scanner scanner) {
        this.batalla = batalla;
        this.scanner = scanner;
    }

    // ---------- Bucle principal ----------
    // Juega rondas hasta que un bando se quede sin combatientes vivos.
    public void jugarBatalla() {
        System.out.println();
        System.out.println("=== Empieza la batalla en " + batalla.getLugar() + " ===");

        int numeroRonda = 1;
        while (partyTieneVivos() && enemigosTienenVivos()) {
            System.out.println();
            System.out.println("--- Ronda " + numeroRonda + " ---");
            jugarRonda();
            numeroRonda++;
        }

        System.out.println();
        if (partyTieneVivos()) {
            System.out.println("¡La party ganó la batalla!");
        } else {
            System.out.println("Game over: la party fue derrotada.");
        }
    }

    // ---------- Una ronda ----------
    // Arma el orden de turnos de ESTA ronda (documento: se reordena al
    // empezar cada ronda nueva) y hace actuar a cada combatiente vivo en
    // ese orden.
    private void jugarRonda() {
        Combatiente[] orden = armarOrdenDeTurnos();

        System.out.print("Orden de turnos: ");
        for (int i = 0; i < orden.length; i++) {
            System.out.print(orden[i].getNombre() + " (Vel " + orden[i].getVelocidad() + ")");
            if (i < orden.length - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();

        for (Combatiente combatiente : orden) {
            // Un combatiente puede haber muerto durante esta misma ronda
            // (por el turno de otro combatiente anterior), así que se
            // vuelve a chequear antes de dejarlo actuar.
            if (!combatiente.estaVivo()) {
                continue;
            }
            if (!partyTieneVivos() || !enemigosTienenVivos()) {
                return; // la batalla ya se definió a mitad de ronda
            }

            if (combatiente instanceof Personaje) {
                jugarTurnoDePersonaje((Personaje) combatiente);
            } else if (combatiente instanceof Enemigo) {
                jugarTurnoDeEnemigo((Enemigo) combatiente);
            }
        }
    }

    // Junta a los personajes y enemigos VIVOS en un solo array (Combatiente,
    // la clase en común de ambos) y lo ordena por Velocidad de mayor a
    // menor.
    private Combatiente[] armarOrdenDeTurnos() {
        Personaje[] personajes = batalla.getParty().getPersonajes();
        Enemigo[] enemigos = batalla.getEnemigos();

        int cantidadVivos = 0;
        for (Personaje p : personajes) {
            if (p != null && p.estaVivo()) {
                cantidadVivos++;
            }
        }
        for (Enemigo e : enemigos) {
            if (e != null && e.estaVivo()) {
                cantidadVivos++;
            }
        }

        Combatiente[] orden = new Combatiente[cantidadVivos];
        int indice = 0;
        for (Personaje p : personajes) {
            if (p != null && p.estaVivo()) {
                orden[indice] = p;
                indice++;
            }
        }
        for (Enemigo e : enemigos) {
            if (e != null && e.estaVivo()) {
                orden[indice] = e;
                indice++;
            }
        }

        ordenarPorVelocidadDescendente(orden);
        return orden;
    }

    // Selection sort escrito a mano (todavía no vimos Collections.sort ni
    // Comparator): en cada pasada busca al de mayor velocidad entre los que
    // faltan ubicar y lo pone en su lugar. Si hay empate de velocidad entre
    // el mejor candidato actual y uno nuevo, se desempata al azar
    // (documento: "si dos personajes tienen exactamente la misma velocidad,
    // el empate se resuelve al azar").
    private void ordenarPorVelocidadDescendente(Combatiente[] orden) {
        for (int i = 0; i < orden.length - 1; i++) {
            int indiceMejor = i;
            for (int j = i + 1; j < orden.length; j++) {
                boolean esMejor = orden[j].getVelocidad() > orden[indiceMejor].getVelocidad();
                boolean hayEmpate = orden[j].getVelocidad() == orden[indiceMejor].getVelocidad();
                if (esMejor || (hayEmpate && Math.random() < 0.5)) {
                    indiceMejor = j;
                }
            }
            Combatiente temp = orden[i];
            orden[i] = orden[indiceMejor];
            orden[indiceMejor] = temp;
        }
    }

    // ---------- Turno de un personaje (jugador elige por consola) ----------
    private void jugarTurnoDePersonaje(Personaje personaje) {
        // Si un enemigo lo aturdió en un turno anterior, pierde este turno
        // entero (no llega a elegir acción ni a subir maná) y se limpia el
        // aturdimiento para que no se repita solo (documento, sección
        // "Efectos de estado").
        if (personaje.consumirAturdimiento()) {
            System.out.println();
            System.out.println(personaje.getNombre() + " está aturdido y pierde su turno.");
            personaje.actualizarEfectosDeFinDeTurno();
            return;
        }

        boolean esGuerrero = personaje instanceof Guerrero;

        System.out.println();
        System.out.println(personaje.getNombre() + " (Vida " + personaje.getVidaActual() + "/" + personaje.getVidaMaxima()
                + ", Maná " + personaje.getManaActual() + "/" + personaje.getManaMaximo() + "). Elegí tu acción:");
        System.out.println("  1) Atacar");
        System.out.println("  2) " + personaje.getNombreHabilidadBasica() + " (" + personaje.getCostoHabilidadBasica() + " maná)");
        System.out.println("  3) " + personaje.getNombreHabilidadPoderosa() + " (" + personaje.getCostoHabilidadPoderosa() + " maná)");
        if (esGuerrero) {
            System.out.println("  4) Defender");
        }

        int opcionMaxima = esGuerrero ? 4 : 3;
        int opcion = pedirOpcion(opcionMaxima);
        boolean usoHabilidad = false;

        if (opcion == 1) {
            Enemigo objetivo = elegirEnemigoObjetivo();
            if (objetivo != null) {
                personaje.atacar(objetivo);
                System.out.println(personaje.getNombre() + " atacó a " + objetivo.getNombre()
                        + ". Vida restante: " + objetivo.getVidaActual());
            }
        } else if (opcion == 2 || opcion == 3) {
            int costo = (opcion == 2) ? personaje.getCostoHabilidadBasica() : personaje.getCostoHabilidadPoderosa();
            if (!personaje.puedeUsarHabilidad(costo)) {
                System.out.println("No hay maná suficiente. " + personaje.getNombre() + " pierde el turno.");
            } else {
                // El Curador cura a un aliado; el resto ataca a un enemigo
                // (ver Curador.java: mismo método usarHabilidadBasica/
                // Poderosa, pero el objetivo cambia según la clase).
                Combatiente objetivo = (personaje instanceof Curador)
                        ? elegirAliadoObjetivo(personaje)
                        : elegirEnemigoObjetivo();

                if (objetivo != null) {
                    if (opcion == 2) {
                        personaje.usarHabilidadBasica(objetivo);
                    } else {
                        personaje.usarHabilidadPoderosa(objetivo);
                    }
                    usoHabilidad = true;
                    System.out.println(personaje.getNombre() + " usó su habilidad sobre " + objetivo.getNombre() + ".");
                }
            }
        } else if (opcion == 4 && esGuerrero) {
            ((Guerrero) personaje).defender();
            System.out.println(personaje.getNombre() + " se puso en guardia.");
        }

        // Maná por turno: sube +10 solo si NO se usó una habilidad este
        // turno (documento, sección "Cómo funciona el maná").
        if (!usoHabilidad) {
            personaje.crecerManaPorTurno();
        }

        // Al final del turno se van agotando todos los efectos con
        // duración propia: Veneno, Inmortalidad, y los debuffs de
        // Velocidad/Defensa de los ataques especiales (Escupitajo del
        // Slime, Golpe Debilitante del Goblin). Ver Personaje.
        // actualizarEfectosDeFinDeTurno().
        personaje.actualizarEfectosDeFinDeTurno();
    }

    // ---------- Turno de un enemigo (automático) ----------
    // El objetivo se elige al azar entre los personajes vivos de la party
    // (documento, sección "Cómo deciden los enemigos qué hacer"); qué HACE
    // sobre ese objetivo (atacar/aturdir/envenenar) lo decide cada enemigo
    // por su cuenta en actuar(), con su propia lista ponderada (polimorfismo:
    // ver RPG.Personajes.ImplEnemigo.Slime/Goblin/Jefe/JefeFinal).
    private void jugarTurnoDeEnemigo(Enemigo enemigo) {
        Personaje objetivo = elegirPersonajeVivoAlAzar();
        if (objetivo == null) {
            return;
        }
        // Cada enemigo decide y ejecuta su propia acción (atacar / aturdir /
        // envenenar, según su lista ponderada -- ver RPG.Personajes.
        // ImplEnemigo). El mensaje por consola lo imprime el propio
        // Enemigo.ejecutarAccionSobre(), así que acá no hace falta repetirlo.
        enemigo.actuar(objetivo);
    }

    // ---------- Helpers de selección de objetivo ----------
    private Enemigo elegirEnemigoObjetivo() {
        Enemigo[] enemigos = batalla.getEnemigos();
        Enemigo[] vivos = new Enemigo[enemigos.length];
        int cantidadVivos = 0;
        for (Enemigo e : enemigos) {
            if (e != null && e.estaVivo()) {
                vivos[cantidadVivos] = e;
                cantidadVivos++;
            }
        }
        if (cantidadVivos == 0) {
            return null;
        }
        if (cantidadVivos == 1) {
            return vivos[0]; // único vivo: se ataca directo, sin preguntar
        }

        System.out.println("¿A qué enemigo atacás?");
        for (int i = 0; i < cantidadVivos; i++) {
            System.out.println("  " + (i + 1) + ") " + vivos[i].getNombre() + " (Vida " + vivos[i].getVidaActual() + ")");
        }
        int opcion = pedirOpcion(cantidadVivos);
        return vivos[opcion - 1];
    }

    // Usado solo por el Curador: el objetivo de su habilidad es un aliado
    // (incluido ella misma), no un enemigo.
    private Personaje elegirAliadoObjetivo(Personaje curador) {
        Personaje[] personajes = batalla.getParty().getPersonajes();
        Personaje[] vivos = new Personaje[personajes.length];
        int cantidadVivos = 0;
        for (Personaje p : personajes) {
            if (p != null && p.estaVivo()) {
                vivos[cantidadVivos] = p;
                cantidadVivos++;
            }
        }
        if (cantidadVivos == 0) {
            return null;
        }

        System.out.println("¿Sobre quién usás " + curador.getNombreHabilidadBasica() + "/" + curador.getNombreHabilidadPoderosa() + "?");
        for (int i = 0; i < cantidadVivos; i++) {
            String marca = (vivos[i] == curador) ? " (vos misma)" : "";
            System.out.println("  " + (i + 1) + ") " + vivos[i].getNombre() + " (Vida " + vivos[i].getVidaActual()
                    + "/" + vivos[i].getVidaMaxima() + ")" + marca);
        }
        int opcion = pedirOpcion(cantidadVivos);
        return vivos[opcion - 1];
    }

    private Personaje elegirPersonajeVivoAlAzar() {
        Personaje[] personajes = batalla.getParty().getPersonajes();
        int cantidadVivos = 0;
        for (Personaje p : personajes) {
            if (p != null && p.estaVivo()) {
                cantidadVivos++;
            }
        }
        if (cantidadVivos == 0) {
            return null;
        }

        int elegido = (int) (Math.random() * cantidadVivos);
        int contados = 0;
        for (Personaje p : personajes) {
            if (p != null && p.estaVivo()) {
                if (contados == elegido) {
                    return p;
                }
                contados++;
            }
        }
        return null; // no debería llegar acá si cantidadVivos > 0
    }

    // ---------- Helpers de estado y de consola ----------
    private boolean partyTieneVivos() {
        for (Personaje p : batalla.getParty().getPersonajes()) {
            if (p != null && p.estaVivo()) {
                return true;
            }
        }
        return false;
    }

    private boolean enemigosTienenVivos() {
        for (Enemigo e : batalla.getEnemigos()) {
            if (e != null && e.estaVivo()) {
                return true;
            }
        }
        return false;
    }

    // Pide un número entre 1 y máximo por consola, repitiendo mientras la
    // respuesta sea inválida (mismo patrón que ya usaban RepartidorAtributos
    // y RepartidorHabilidades).
    private int pedirOpcion(int maximo) {
        int opcion = -1;
        while (opcion < 1 || opcion > maximo) {
            System.out.print("Elegí una opción (1 a " + maximo + "): ");
            opcion = scanner.nextInt();
            if (opcion < 1 || opcion > maximo) {
                System.out.println("Opción inválida.");
            }
        }
        return opcion;
    }
}
