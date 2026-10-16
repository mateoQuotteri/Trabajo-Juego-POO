package RPG.controlador;

import RPG.modelo.Batalla;
import RPG.modelo.GeneradorDeEnemigos;
import RPG.modelo.Party;
import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.personajes.Arquero;
import RPG.modelo.personajes.Curador;
import RPG.modelo.personajes.Guerrero;
import RPG.modelo.personajes.Mago;
import RPG.modelo.personajes.Personaje;

// CONTROLADOR (la "C" de MVC, y GRASP Controlador).
//
// Recibe lo que eligió el jugador ("atacar", "usar habilidad poderosa
// sobre tal aliado", "defender"), le pide al MODELO que lo ejecute, y le
// devuelve a la VISTA un texto con lo que pasó. No dibuja nada ni lee del
// teclado: por eso lo pueden usar TANTO la pantalla de Swing como la
// prueba por consola, sin cambiar una línea.
//
// Reemplaza a MotorDeCombate (que mezclaba lógica de batalla con
// Scanner/System.out) y saca la lógica de juego que estaba metida adentro
// de PantallaBatalla (elegir enemigo por piso, quién cura a quién, turno
// del enemigo...).
//
// Corresponde al ControladorDeBatalla de los 2 diagramas de secuencia.
public class ControladorDeBatalla {

    private GeneradorDeEnemigos generadorDeEnemigos;
    private Batalla batalla;
    private boolean resultadoYaProcesado;

    public ControladorDeBatalla() {
        generadorDeEnemigos = new GeneradorDeEnemigos();
    }

    // ======================================================================
    // ARRANCAR UNA BATALLA
    // ======================================================================

    public String iniciarBatalla(Party party, int piso) {
        Enemigo enemigo = generadorDeEnemigos.crearEnemigoParaPiso(piso);
        batalla = new Batalla(party, enemigo, piso);
        resultadoYaProcesado = false;
        String log = "Piso " + piso + ": aparece " + enemigo.getNombre() + " (nivel " + enemigo.getNivel() + ").\n";
        return log + avanzarHastaTurnoDelJugador();
    }

    // TEMPORAL: hasta que exista la clase Partida/Torre (próximo paso), cada
    // batalla de prueba arranca con una party nueva de nivel 1.
    public String iniciarBatallaDePrueba(int piso) {
        return iniciarBatalla(crearPartyDePrueba(), piso);
    }

    public static Party crearPartyDePrueba() {
        Party party = new Party();
        party.agregarMiembro(new Guerrero("Mateo"));
        party.agregarMiembro(new Mago("Pedro"));
        party.agregarMiembro(new Arquero("Cristian"));
        party.agregarMiembro(new Curador("Evelyn"));
        return party;
    }

    // ======================================================================
    // ACCIONES DEL JUGADOR (para el personaje que tiene el turno)
    // ======================================================================

    public String atacar() {
        Personaje actor = getPersonajeEnTurno();
        if (actor == null) {
            return "";
        }
        String log = actor.atacar(batalla.getEnemigo()) + "\n";
        return log + terminarTurnoDePersonaje(actor, false);
    }

    // poderosa: false = habilidad básica, true = habilidad poderosa.
    // aliadoObjetivo: solo se usa si la habilidad es de apoyo (Curador);
    // si no, el objetivo es siempre el enemigo.
    // Si la acción no es válida (falta maná u objetivo inválido) NO se
    // consume el turno: el mismo personaje sigue eligiendo.
    public String usarHabilidad(boolean poderosa, Personaje aliadoObjetivo) {
        Personaje actor = getPersonajeEnTurno();
        if (actor == null) {
            return "";
        }
        Habilidad habilidad = poderosa ? actor.getHabilidadPoderosa() : actor.getHabilidadBasica();
        if (!actor.tieneManaPara(habilidad)) {
            return "No alcanza el maná para " + habilidad.getNombre() + " (cuesta " + habilidad.getCostoMana()
                    + ", tiene " + actor.getManaActual() + ").\n";
        }
        if (habilidad.esDeApoyo()) {
            if (!habilidad.puedeApuntarA(aliadoObjetivo)) {
                return habilidad.getNombre() + " no se puede usar sobre ese objetivo.\n";
            }
            String log = actor.usarHabilidad(habilidad, aliadoObjetivo) + "\n";
            return log + terminarTurnoDePersonaje(actor, true);
        }
        String log = actor.usarHabilidad(habilidad, batalla.getEnemigo()) + "\n";
        return log + terminarTurnoDePersonaje(actor, true);
    }

    public String defender() {
        Personaje actor = getPersonajeEnTurno();
        if (actor == null || !actor.puedeDefender()) {
            return "";
        }
        String log = actor.defender() + "\n";
        return log + terminarTurnoDePersonaje(actor, false);
    }

    // ======================================================================
    // CICLO DE TURNOS
    // ======================================================================

    // Cierre común a cualquier acción de un personaje: maná por turno,
    // efectos de fin de turno (veneno, duraciones) y pasar al siguiente.
    private String terminarTurnoDePersonaje(Personaje actor, boolean usoHabilidad) {
        String log = "";
        if (!usoHabilidad) {
            actor.crecerManaPorTurno(); // +10 si no usó habilidad (documento)
        }
        log += lineaSiNoVacia(actor.actualizarEfectosDeFinDeTurno());
        batalla.terminarTurnoActual();
        return log + avanzarHastaTurnoDelJugador();
    }

    // Juega SOLO todo lo que no necesita al jugador: turnos del enemigo y
    // personajes aturdidos. Frena cuando le toca a un personaje que sí
    // puede elegir, o cuando la batalla termina.
    private String avanzarHastaTurnoDelJugador() {
        String log = "";
        while (!batalla.estaTerminada()) {
            if (batalla.esTurnoDelEnemigo()) {
                log += jugarTurnoDelEnemigo();
                continue;
            }
            Personaje personaje = batalla.getPersonajeEnTurno();
            if (personaje.consumirAturdimiento()) {
                log += personaje.getNombre() + " está aturdido y pierde el turno.\n";
                log += lineaSiNoVacia(personaje.actualizarEfectosDeFinDeTurno());
                batalla.terminarTurnoActual();
                continue;
            }
            personaje.iniciarTurno(); // se termina la guardia de Defender
            return log;
        }
        return log + procesarFinDeBatalla();
    }

    private String jugarTurnoDelEnemigo() {
        Personaje objetivo = batalla.getParty().elegirVivoAlAzar();
        String log = batalla.getEnemigo().actuar(objetivo) + "\n";
        batalla.terminarTurnoActual();
        return log;
    }

    // Se ejecuta una sola vez al terminar: experiencia (y más adelante
    // oro/ítems) si ganó la party, o game over si perdió.
    private String procesarFinDeBatalla() {
        if (resultadoYaProcesado) {
            return "";
        }
        resultadoYaProcesado = true;
        if (!batalla.ganoLaParty()) {
            return "Toda la party cayó. GAME OVER.\n";
        }
        Enemigo enemigo = batalla.getEnemigo();
        String log = "¡" + enemigo.getNombre() + " derrotado! Cada personaje vivo gana "
                + enemigo.getExperienciaQueOtorga() + " de experiencia.\n";
        log += lineaSiNoVacia(batalla.getParty().repartirExperiencia(enemigo.getExperienciaQueOtorga()));
        if (batalla.getPiso() == GeneradorDeEnemigos.PISO_JEFE_FINAL) {
            log += "¡VICTORIA! Completaron el ascenso a la Torre.\n";
        }
        // PENDIENTE: recompensas de oro/ítems por lista ponderada (los jefes
        // garantizadas) -> depende de que existan Inventario e Item.
        return log;
    }

    private String lineaSiNoVacia(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "";
        }
        return texto.trim() + "\n";
    }

    // ======================================================================
    // CONSULTAS PARA LA VISTA (qué mostrar / qué botones habilitar)
    // ======================================================================

    public Batalla getBatalla() {
        return batalla;
    }

    public Personaje getPersonajeEnTurno() {
        if (batalla == null || batalla.estaTerminada()) {
            return null;
        }
        return batalla.getPersonajeEnTurno();
    }

    public boolean batallaTerminada() {
        return batalla == null || batalla.estaTerminada();
    }

    public boolean puedeUsarHabilidad(boolean poderosa) {
        Personaje actor = getPersonajeEnTurno();
        if (actor == null) {
            return false;
        }
        Habilidad habilidad = poderosa ? actor.getHabilidadPoderosa() : actor.getHabilidadBasica();
        return actor.tieneManaPara(habilidad);
    }
}
