package RPG.controlador;

import RPG.modelo.Batalla;
import RPG.modelo.Combatiente;
import RPG.modelo.Party;
import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.enemigos.Goblin;
import RPG.modelo.enemigos.Jefe;
import RPG.modelo.enemigos.JefeFinal;
import RPG.modelo.enemigos.Slime;
import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.items.Consumible;
import RPG.modelo.items.Item;
import RPG.modelo.personajes.Personaje;
import RPG.modelo.personajes.Arquero;
import RPG.modelo.personajes.Curador;
import RPG.modelo.personajes.Guerrero;
import RPG.modelo.personajes.Mago;

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
        agregarItemInicial(new Consumible("Poción", "Recupera hasta 25 puntos de vida.", 25, 0));
        agregarItemInicial(new Consumible("Poción", "Recupera hasta 25 puntos de vida.", 25, 0));
        agregarItemInicial(new Consumible("Éter", "Recupera hasta 20 puntos de maná.", 0, 20));
        agregarItemInicial(new Consumible("Éter", "Recupera hasta 20 puntos de maná.", 0, 20));
        pisoActual = 1;
    }

    private void agregarItemInicial(Item item) {
        if (!party.getInventario().agregarItem(item)) {
            throw new IllegalStateException("No se pudo agregar el ítem inicial al inventario.");
        }
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
    private Enemigo crearEnemigo(int piso) {
        if (piso == 5) {
            return new Jefe();
        }
        if (piso == 10) {
            return new JefeFinal();
        }
        Enemigo enemigo = Math.random() < 0.5 ? new Slime() : new Goblin();
        enemigo.escalarSegunPiso(piso);
        return enemigo;
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
    
    public String usarItem(Item item, Personaje objetivo) {
        String error = validarUsoItem(item, objetivo);
        if (error != null) {
            return error;
        }

        Item consumido = party.getInventario().removerItem(item);
        if (consumido == null) {
            return "Ese ítem ya no está en el inventario.\n";
        }
        consumido.usar(objetivo);
        return objetivo.getNombre() + " usó " + consumido.getNombre() + ".\n";
    }

    public String usarItemEnBatalla(Item item, Personaje objetivo) {
        if (batalla == null || batalla.estaTerminada()) {
            return "No hay una batalla en curso.\n";
        }
        Personaje actor = batalla.getPersonajeEnTurno();
        if (actor == null) {
            return "No es el turno de un personaje.\n";
        }

        String error = validarUsoItem(item, objetivo);
        if (error != null) {
            return error;
        }

        Item consumido = party.getInventario().removerItem(item);
        if (consumido == null) {
            return "Ese ítem ya no está en el inventario.\n";
        }
        consumido.usar(objetivo);
        actor.recuperarMana();
        return objetivo.getNombre() + " usó " + consumido.getNombre() + ".\n" + terminarTurno();
    }

    private String validarUsoItem(Item item, Personaje objetivo) {
        if (item == null || objetivo == null) {
            return "Seleccioná un ítem y un personaje objetivo.\n";
        }
        if (!(item instanceof Consumible)) {
            return "Este ítem no se puede usar.\n";
        }
        if (!objetivo.estaVivo()) {
            return "No se puede usar un ítem sobre alguien caído.\n";
        }

        boolean esMiembro = false;
        for (Personaje miembro : party.getMiembros()) {
            if (miembro == objetivo) {
                esMiembro = true;
                break;
            }
        }
        if (!esMiembro) {
            return "El objetivo no pertenece a la party.\n";
        }

        if (!party.getInventario().getItems().contains(item)) {
            return "Ese ítem ya no está en el inventario.\n";
        }
        if (!((Consumible) item).tieneEfectoEn(objetivo)) {
            return "Ese ítem no tendría efecto sobre " + objetivo.getNombre() + ".\n";
        }
        return null;
    }

    // ---------- Turnos ----------

    private String terminarTurno() {
        Combatiente actual = batalla.getCombatienteActual();
        String log = actual.procesarVenenoAlFinDeTurno();
        batalla.pasarAlSiguienteTurno();
        return log + jugarTurnosDelEnemigo();
    }

    // Si le toca al enemigo, juega solo. Frena cuando le toca a un
    // personaje (hay que esperar al jugador) o cuando termina la batalla.
    private String jugarTurnosDelEnemigo() {
        String log = "";
        while (!batalla.estaTerminada()) {
            Combatiente actual = batalla.getCombatienteActual();
            if (actual.estaAturdido()) {
                log = log + actual.getNombre() + " pierde su turno por estar aturdido.\n";
                actual.quitarAturdimiento();
                log = log + actual.procesarVenenoAlFinDeTurno();
                batalla.pasarAlSiguienteTurno();
                continue;
            }

            if (actual == batalla.getEnemigo()) {
                Personaje objetivo = batalla.getParty().elegirVivoAlAzar();
                log = log + batalla.getEnemigo().actuar(objetivo) + "\n";
                log = log + actual.procesarVenenoAlFinDeTurno();
                batalla.pasarAlSiguienteTurno();
                continue;
            }

            ((Personaje) actual).iniciarTurno();
            return log;
        }

        return log + terminarBatalla();
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
