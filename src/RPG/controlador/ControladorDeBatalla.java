package RPG.controlador;

import RPG.modelo.Batalla;
import RPG.modelo.Party;
import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.enemigos.Goblin;
import RPG.modelo.enemigos.Jefe;
import RPG.modelo.enemigos.JefeFinal;
import RPG.modelo.enemigos.Slime;
import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.personajes.Arquero;
import RPG.modelo.personajes.Curador;
import RPG.modelo.personajes.Guerrero;
import RPG.modelo.personajes.Mago;
import RPG.modelo.personajes.Personaje;

// CONTROLADOR (la "C" de MVC).
// Las pantallas le avisan qué tocó el jugador; el controlador le pide al
// modelo que lo haga y devuelve un texto con lo que pasó para el log.
// Ninguna pantalla decide reglas del juego: todo pasa por acá.
public class ControladorDeBatalla {

    private Party party;
    private Batalla batalla;
    private int pisoActual;

    // ---------- Partida ----------

    // Botón "Iniciar partida" de la pantalla de inicio.
    public void nuevaPartida() {
        party = new Party();
        party.agregarMiembro(new Guerrero("Mateo"));
        party.agregarMiembro(new Mago("Pedro"));
        party.agregarMiembro(new Arquero("Cristian"));
        party.agregarMiembro(new Curador("Evelyn"));
        pisoActual = 1;
    }

    // Arma la batalla del piso actual. Devuelve el primer texto del log.
    public String empezarBatalla() {
        Enemigo enemigo = crearEnemigo(pisoActual);
        batalla = new Batalla(party, enemigo, pisoActual);
        String log = "Piso " + pisoActual + ": aparece " + enemigo.getNombre() + "\n";
        return log + jugarTurnosDelEnemigo();
    }

    // Documento 1.1: pisos 1-4 y 6-9 enemigos comunes, piso 5 Rey Slime,
    // piso 10 Guardián Ancestral.
    // TODO: el documento dice que el común se elige AL AZAR entre Slime y
    // Goblin (hoy se alterna por piso par/impar) y que se escala +10% por
    // piso (ver Enemigo.escalarSegunPiso).
    private Enemigo crearEnemigo(int piso) {
        if (piso == 5) {
            return new Jefe();
        }
        if (piso == 10) {
            return new JefeFinal();
        }
        if (piso % 2 == 0) {
            return new Goblin();
        }
        return new Slime();
    }

    // Botón "Siguiente piso" del resumen.
    public void subirDePiso() {
        pisoActual++;
    }

    public boolean ganoElJuego() {
        return batalla.ganoLaParty() && pisoActual == 10;
    }

    // ---------- Acciones del jugador ----------

    public String atacar() {
        Personaje actor = batalla.getPersonajeEnTurno();
        String log = actor.atacar(batalla.getEnemigo()) + "\n";
        actor.recuperarMana();
        return log + terminarTurno();
    }

    // poderosa = false -> habilidad básica, true -> habilidad poderosa.
    // aliado: solo se usa si la habilidad cura (Curador).
    public String usarHabilidad(boolean poderosa, Personaje aliado) {
        Personaje actor = batalla.getPersonajeEnTurno();
        Habilidad habilidad;
        if (poderosa) {
            habilidad = actor.getHabilidadPoderosa();
        } else {
            habilidad = actor.getHabilidadBasica();
        }

        // Sin maná suficiente no se puede usar y NO pierde el turno.
        if (!actor.tieneManaPara(habilidad)) {
            return "No alcanza el maná para " + habilidad.getNombre() + "\n";
        }

        String log;
        if (habilidad.esDeApoyo()) {
            if (!aliado.estaVivo()) {
                return "No se puede curar a alguien caído\n";
            }
            log = actor.usarHabilidad(habilidad, aliado) + "\n";
        } else {
            log = actor.usarHabilidad(habilidad, batalla.getEnemigo()) + "\n";
        }
        // Usó habilidad: este turno NO recupera maná (documento).
        return log + terminarTurno();
    }

    public String defender() {
        Personaje actor = batalla.getPersonajeEnTurno();
        String log = actor.defender() + "\n";
        actor.recuperarMana();
        return log + terminarTurno();
    }

    // ---------- Turnos ----------

    private String terminarTurno() {
        // TODO: veneno al final del turno del personaje
        // (Combatiente.aplicarVeneno()).
        batalla.pasarAlSiguienteTurno();
        return jugarTurnosDelEnemigo();
    }

    // Si le toca al enemigo, juega solo. Frena cuando le toca a un
    // personaje (hay que esperar al jugador) o cuando termina la batalla.
    private String jugarTurnosDelEnemigo() {
        String log = "";
        while (!batalla.estaTerminada() && batalla.esTurnoDelEnemigo()) {
            Personaje objetivo = batalla.getParty().elegirVivoAlAzar();
            log = log + batalla.getEnemigo().actuar(objetivo) + "\n";
            batalla.pasarAlSiguienteTurno();
        }
        // TODO: aturdimiento. Si el que sigue está aturdido, pierde el
        // turno: quitarAturdimiento() y pasarAlSiguienteTurno().
        // Ojo que puede tocarle al enemigo después.

        if (batalla.estaTerminada()) {
            log = log + terminarBatalla();
        } else {
            batalla.getPersonajeEnTurno().iniciarTurno();
        }
        return log;
    }

    private String terminarBatalla() {
        if (!batalla.ganoLaParty()) {
            return "Toda la party cayó. GAME OVER\n";
        }
        // TODO: repartir experiencia a los personajes VIVOS
        // (Personaje.ganarExperiencia) con batalla.getEnemigo().getExperienciaQueOtorga().
        // TODO: recompensas de oro / ítems con lista ponderada (los jefes, siempre).
        return batalla.getEnemigo().getNombre() + " derrotado. ¡Victoria!\n";
    }

    // ---------- Consultas para las pantallas ----------

    public Batalla getBatalla() {
        return batalla;
    }

    public Party getParty() {
        return party;
    }

    public int getPisoActual() {
        return pisoActual;
    }

    public Personaje getPersonajeEnTurno() {
        if (batalla.estaTerminada()) {
            return null;
        }
        return batalla.getPersonajeEnTurno();
    }
}
