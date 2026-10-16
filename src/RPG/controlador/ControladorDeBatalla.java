package RPG.controlador;

import RPG.modelo.Batalla;
import RPG.modelo.GeneradorDeEnemigos;
import RPG.modelo.Party;
import RPG.modelo.ResultadoDeBatalla;
import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.personajes.Personaje;

// CONTROLADOR de una batalla (la "C" de MVC, y GRASP Controlador).
// Corresponde al ControladorDeBatalla de los 2 diagramas de secuencia.
//
// Recibe lo que eligió el jugador ("atacar", "usar habilidad poderosa",
// "defender"), le pide al MODELO que lo ejecute y le devuelve a la VISTA un
// texto con lo que pasó. No dibuja nada ni lee del teclado: por eso lo usan
// igual la pantalla de Swing y la prueba por consola.
public class ControladorDeBatalla {

    private GeneradorDeEnemigos generadorDeEnemigos;
    private Batalla batalla;
    private ResultadoDeBatalla resultado;

    public ControladorDeBatalla() {
        generadorDeEnemigos = new GeneradorDeEnemigos();
    }

    // ======================================================================
    // ARRANCAR UNA BATALLA
    // ======================================================================

    public String iniciarBatalla(Party party, int piso) {
        Enemigo enemigo = generadorDeEnemigos.crearEnemigoParaPiso(piso);
        batalla = new Batalla(party, enemigo, piso);
        resultado = null;
        String log = "Piso " + piso + ": aparece " + enemigo.getNombre() + " (nivel " + enemigo.getNivel() + ").\n";
        return log + avanzarHastaTurnoDelJugador();
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
                return habilidad.getNombre() + " solo se puede usar sobre un aliado vivo.\n";
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
    // veneno al final del turno y pasar al siguiente.
    private String terminarTurnoDePersonaje(Personaje actor, boolean usoHabilidad) {
        String log = "";
        if (!usoHabilidad) {
            actor.crecerManaPorTurno(); // +10 si no usó habilidad (documento)
        }
        log += lineaSiNoVacia(actor.actualizarEfectosDeFinDeTurno());
        batalla.terminarTurnoActual();
        return log + avanzarHastaTurnoDelJugador();
    }

    // Juega solo todo lo que no necesita al jugador: turnos del enemigo y
    // personajes aturdidos. Frena cuando le toca a un personaje que puede
    // elegir, o cuando la batalla termina.
    private String avanzarHastaTurnoDelJugador() {
        String log = "";
        while (!batalla.estaTerminada()) {
            if (batalla.esTurnoDelEnemigo()) {
                Personaje objetivo = batalla.getParty().elegirVivoAlAzar();
                log += batalla.getEnemigo().actuar(objetivo) + "\n";
                batalla.terminarTurnoActual();
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

    // Se ejecuta una sola vez al terminar: reparte experiencia si ganó la
    // party y arma el ResultadoDeBatalla para la pantalla de resumen.
    private String procesarFinDeBatalla() {
        if (resultado != null) {
            return "";
        }
        Enemigo enemigo = batalla.getEnemigo();
        if (!batalla.ganoLaParty()) {
            resultado = new ResultadoDeBatalla(batalla.getPiso(), enemigo.getNombre(), false, 0, "",
                    batalla.getNumeroRonda());
            return "Toda la party cayó. GAME OVER.\n";
        }
        int experiencia = enemigo.getExperienciaQueOtorga();
        String subidas = batalla.getParty().repartirExperiencia(experiencia);
        resultado = new ResultadoDeBatalla(batalla.getPiso(), enemigo.getNombre(), true, experiencia, subidas,
                batalla.getNumeroRonda());
        // PENDIENTE: oro/ítems por lista ponderada (jefes: garantizados).
        return "¡" + enemigo.getNombre() + " derrotado! Cada personaje vivo gana " + experiencia + " de experiencia.\n"
                + lineaSiNoVacia(subidas);
    }

    private String lineaSiNoVacia(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "";
        }
        return texto.trim() + "\n";
    }

    // ======================================================================
    // CONSULTAS PARA LA VISTA
    // ======================================================================

    public Batalla getBatalla() {
        return batalla;
    }

    // null mientras la batalla siga en curso.
    public ResultadoDeBatalla getResultado() {
        return resultado;
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
