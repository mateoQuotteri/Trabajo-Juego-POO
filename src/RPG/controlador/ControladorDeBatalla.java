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
import RPG.modelo.items.Escudo;
import RPG.modelo.items.Item;
import RPG.modelo.items.PocionMana;
import RPG.modelo.items.PocionVida;
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
    private String resumenExperiencia = "";
    private String resumenRecompensas = "";

    // ---------- Partida ----------

    // Botón "Iniciar partida" de la pantalla de inicio.
    public void nuevaPartida() {
        party = new Party();
        party.agregarMiembro(new Guerrero("Mateo"));
        party.agregarMiembro(new Mago("Pedro"));
        party.agregarMiembro(new Arquero("Cristian"));
        party.agregarMiembro(new Curador("Evelyn"));
        agregarItemInicial(new PocionVida());
        agregarItemInicial(new PocionVida());
        agregarItemInicial(new PocionMana());

        pisoActual = 1;
    }

    private void agregarItemInicial(Item item) {
        if (!party.getInventario().agregarItem(item)) {
            throw new IllegalStateException("No se pudo agregar el ítem inicial al inventario.");
        }
    }

    // Arma la batalla del piso actual. Devuelve el primer texto del log.
    public String empezarBatalla() {
        resumenExperiencia = "";
        resumenRecompensas = "";
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
        if (consumido instanceof Escudo && actor == objetivo) {
            objetivo.omitirProximoDescuentoEscudo();
        }
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
        if (item instanceof Escudo && (batalla == null || batalla.estaTerminada())) {
            return "El escudo solo se puede usar durante una batalla.\n";
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
        actual.procesarEscudoAlFinDeTurno();
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
                actual.procesarEscudoAlFinDeTurno();
                batalla.pasarAlSiguienteTurno();
                continue;
            }

            if (actual == batalla.getEnemigo()) {
                Personaje objetivo = batalla.getParty().elegirVivoAlAzar();
                log = log + batalla.getEnemigo().actuar(objetivo) + "\n";
                log = log + actual.procesarVenenoAlFinDeTurno();
                actual.procesarEscudoAlFinDeTurno();
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
            resumenExperiencia = "";
            resumenRecompensas = "";
            return "Toda la party cayó. GAME OVER\n";
        }
        int experienciaGanada = batalla.getEnemigo().getExperienciaQueOtorga();
        String log = batalla.getEnemigo().getNombre() + " derrotado. ¡Victoria!\n";
        resumenExperiencia = "";
        resumenRecompensas = "";
        Personaje[] miembrosVivos = party.getMiembrosVivos();
        for (int i = 0; i < miembrosVivos.length; i++) {
            Personaje personaje = miembrosVivos[i];
            int nivelAnterior = personaje.getNivel();
            personaje.ganarExperiencia(experienciaGanada);
            String lineaExperiencia = personaje.getNombre() + " obtiene "
                    + experienciaGanada + " puntos de experiencia.";
            log = log + lineaExperiencia + "\n";
            resumenExperiencia = resumenExperiencia + lineaExperiencia + "\n";
            if (personaje.getNivel() > nivelAnterior) {
                String lineaNivel = personaje.getNombre() + " sube al nivel " + personaje.getNivel() + ".";
                log = log + lineaNivel + "\n";
                resumenExperiencia = resumenExperiencia + lineaNivel + "\n";
            }
        }
        log = log + entregarRecompensas();
        return log;
    }

    private String entregarRecompensas() {
        Enemigo enemigo = batalla.getEnemigo();
        boolean esJefe = enemigo instanceof Jefe || enemigo instanceof JefeFinal;
        int oroGanado = esJefe ? 100 + (int) (Math.random() * 101)
                : 10 + (int) (Math.random() * 21);
        party.agregarOro(oroGanado);
        String recompensa = "Oro: +" + oroGanado + " (total: " + party.getOro() + ")";
        String log = recompensa + "\n";

        if (esJefe || Math.random() < 0.30) {
            Item item = generarItemRecompensa();
            if (party.getInventario().agregarItem(item)) {
                recompensa = recompensa + "\nÍtem: " + item.getNombre();
                log = log + "Ítem obtenido: " + item.getNombre() + ".\n";
            } else {
                recompensa = recompensa + "\nÍtem no obtenido: inventario lleno";
                log = log + "El inventario está lleno; no se pudo guardar " + item.getNombre() + ".\n";
            }
        } else {
            recompensa = recompensa + "\nSin ítem adicional";
        }
        resumenRecompensas = recompensa;
        return log;
    }

    private Item generarItemRecompensa() {
        int seleccion = (int) (Math.random() * 9);
        if (seleccion < 4) {
            return new PocionVida();
        }
        if (seleccion < 7) {
            return new PocionMana();
        }
        return new Escudo();
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

    public String getResumenExperiencia() {
        return resumenExperiencia;
    }
    
    public String getResumenRecompensas() {
        return resumenRecompensas;
    }

    public Personaje getPersonajeEnTurno() {
        if (batalla.estaTerminada()) {
            return null;
        }
        return batalla.getPersonajeEnTurno();
    }
}
