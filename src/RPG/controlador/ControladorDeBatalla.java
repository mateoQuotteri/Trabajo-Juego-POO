package RPG.controlador;

import RPG.dto.CombatienteDTO;
import RPG.dto.EstadoBatallaDTO;
import RPG.dto.EstadoInventarioDTO;
import RPG.dto.ItemDTO;
import RPG.dto.PersonajeDTO;
import RPG.dto.ResumenBatallaDTO;
import RPG.modelo.Batalla;
import RPG.modelo.Party;
import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.habilidades.Habilidad;
import RPG.modelo.items.Arma;
import RPG.modelo.items.Armadura;
import RPG.modelo.items.Item;
import RPG.modelo.items.PocionMana;
import RPG.modelo.items.PocionVida;
import RPG.modelo.personajes.Personaje;
import RPG.modelo.personajes.Arquero;
import RPG.modelo.personajes.Curador;
import RPG.modelo.personajes.Guerrero;
import RPG.modelo.personajes.Mago;
import RPG.persistencia.PersistenciaPartida;
import RPG.servicios.FabricaDeEnemigos;
import RPG.servicios.GestorDeInventario;
import RPG.servicios.GestorDeProgresion;
import RPG.servicios.GestorDeRecompensas;
import RPG.servicios.GestorDeTurnos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final FabricaDeEnemigos fabricaDeEnemigos = new FabricaDeEnemigos();
    private final GestorDeInventario gestorDeInventario = new GestorDeInventario();
    private final GestorDeProgresion gestorDeProgresion = new GestorDeProgresion();
    private final GestorDeRecompensas gestorDeRecompensas = new GestorDeRecompensas();
    private final GestorDeTurnos gestorDeTurnos = new GestorDeTurnos();

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

    public void guardarPartida() throws IOException {
        if (party == null || batalla == null || !batalla.ganoLaParty() || pisoActual >= 10) {
            throw new IllegalStateException("Solo se puede guardar una partida entre batallas.");
        }
        PersistenciaPartida.guardar(party, pisoActual + 1);
    }

    public void cargarPartida() throws IOException {
        PersistenciaPartida.PartidaCargada partida = PersistenciaPartida.cargar();
        party = partida.getParty();
        pisoActual = partida.getPisoSiguiente();
        batalla = null;
        resumenExperiencia = "";
        resumenRecompensas = "";
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
        Enemigo enemigo = fabricaDeEnemigos.crear(pisoActual);
        batalla = new Batalla(party, enemigo, pisoActual);
        String log = "Piso " + pisoActual + ": aparece " + enemigo.getNombre() + "\n";
        String turnosAutomaticos = gestorDeTurnos.resolverTurnosAutomaticos(batalla);
        if (batalla.estaTerminada()) {
            return log + turnosAutomaticos + terminarBatalla();
        }
        return log + turnosAutomaticos;
    }

    // Botón "Siguiente piso" del resumen.
    public void subirDePiso() {
        pisoActual++;
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
    public String usarHabilidad(boolean poderosa, int indiceAliado) {
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
            Personaje aliado = obtenerMiembro(indiceAliado);
            if (aliado == null) {
                return "Seleccioná un aliado para usar la habilidad\n";
            }
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
        if (actor == null || !actor.puedeDefender()) {
            return "Solo el Guerrero puede defenderse.\n";
        }
        String log = actor.defender() + "\n";
        actor.recuperarMana();
        return log + terminarTurno();
    }
    
    public String usarItem(int indiceItem, int indiceObjetivo) {
        Item item = obtenerItem(indiceItem);
        Personaje objetivo = obtenerMiembro(indiceObjetivo);
        return gestorDeInventario.usarFueraDeBatalla(party, batalla, item, objetivo).getMensaje();
    }

    public String usarItemEnBatalla(int indiceItem, int indiceObjetivo) {
        Personaje actor = batalla == null ? null : batalla.getPersonajeEnTurno();
        Item item = obtenerItem(indiceItem);
        Personaje objetivo = obtenerMiembro(indiceObjetivo);
        GestorDeInventario.Resultado resultado = gestorDeInventario.usarEnBatalla(
                party, batalla, actor, item, objetivo);
        if (!resultado.fueExitoso()) {
            return resultado.getMensaje();
        }
        actor.recuperarMana();
        return resultado.getMensaje() + terminarTurno();
    }

    // ---------- Turnos ----------

    private String terminarTurno() {
        String log = gestorDeTurnos.terminarTurno(batalla);
        return batalla.estaTerminada() ? log + terminarBatalla() : log;
    }

    private String terminarBatalla() {
        if (!batalla.ganoLaParty()) {
            resumenExperiencia = "";
            resumenRecompensas = "";
            return "Toda la party cayó. GAME OVER\n";
        }
        int experienciaGanada = batalla.getEnemigo().getExperienciaQueOtorga();
        GestorDeProgresion.Resultado progresion =
                gestorDeProgresion.distribuirExperiencia(party, experienciaGanada);
        GestorDeRecompensas.Resultado recompensas =
                gestorDeRecompensas.entregar(party, batalla.getEnemigo());
        resumenExperiencia = progresion.getResumen();
        resumenRecompensas = recompensas.getResumen();
        return batalla.getEnemigo().getNombre() + " derrotado. ¡Victoria!\n"
                + progresion.getLog() + recompensas.getLog();
    }

    // ---------- Consultas para las pantallas ----------

    public EstadoBatallaDTO getEstadoBatallaDTO() {
        if (batalla == null) {
            throw new IllegalStateException("No hay una batalla para consultar.");
        }
        List<PersonajeDTO> personajes = crearPersonajesDTO();
        int indiceEnTurno = -1;
        if (!batalla.estaTerminada()) {
            Personaje actor = batalla.getPersonajeEnTurno();
            indiceEnTurno = indiceDeMiembro(actor);
        }
        Enemigo enemigo = batalla.getEnemigo();
        CombatienteDTO enemigoDTO = new CombatienteDTO(enemigo.getNombre(), enemigo.getVidaActual(),
                enemigo.getVidaMaxima(), enemigo.getDefensa(), enemigo.getTurnosEscudoRestantes(),
                enemigo.estaVivo());
        return new EstadoBatallaDTO(batalla.getPiso(), batalla.getNumeroRonda(),
                batalla.getOrdenComoTexto(), enemigoDTO, personajes, crearItemsDTO(),
                indiceEnTurno, batalla.estaTerminada());
    }

    public EstadoInventarioDTO getEstadoInventarioDTO() {
        asegurarPartidaActiva();
        return new EstadoInventarioDTO(party.getOro(), crearPersonajesDTO(), crearItemsDTO());
    }

    public ResumenBatallaDTO getResumenBatallaDTO() {
        if (batalla == null) {
            throw new IllegalStateException("No hay una batalla para resumir.");
        }
        boolean gano = batalla.ganoLaParty();
        boolean puedeContinuar = gano && pisoActual < 10;
        String resultado;
        String detalle;
        if (!gano) {
            resultado = "GAME OVER";
            detalle = "La party cayó en el piso " + batalla.getPiso();
        } else if (pisoActual == 10) {
            resultado = "¡GANASTE EL JUEGO!";
            detalle = "Derrotaste al " + batalla.getEnemigo().getNombre();
        } else {
            resultado = "Piso " + batalla.getPiso() + " superado";
            detalle = batalla.getEnemigo().getNombre() + " derrotado en "
                    + batalla.getNumeroRonda() + " rondas";
        }
        return new ResumenBatallaDTO(resultado, detalle, puedeContinuar,
                resumenExperiencia, resumenRecompensas, party.getOro());
    }

    private List<PersonajeDTO> crearPersonajesDTO() {
        asegurarPartidaActiva();
        Personaje[] miembros = party.getMiembros();
        List<PersonajeDTO> resultado = new ArrayList<PersonajeDTO>(miembros.length);
        for (int i = 0; i < miembros.length; i++) {
            Personaje personaje = miembros[i];
            resultado.add(new PersonajeDTO(i, personaje.getNombre(), personaje.getNombreClase(),
                    personaje.getNivel(), personaje.getVidaActual(), personaje.getVidaMaxima(),
                    personaje.getManaActual(), personaje.getManaMaximo(), personaje.getAtaqueFisico(),
                    personaje.getAtaqueMagico(), personaje.getDefensa(), personaje.getVelocidad(),
                    personaje.getExperiencia(), personaje.getTurnosEscudoRestantes(),
                    personaje.estaVivo(),
                    personaje.getArmaEquipada() == null ? "" : personaje.getArmaEquipada().getNombre(),
                    personaje.getArmaduraEquipada() == null ? "" : personaje.getArmaduraEquipada().getNombre(),
                    personaje.puedeDefender(), personaje.getHabilidadBasica().getNombre(),
                    personaje.getHabilidadPoderosa().getNombre(),
                    personaje.tieneManaPara(personaje.getHabilidadBasica()),
                    personaje.tieneManaPara(personaje.getHabilidadPoderosa())));
        }
        return resultado;
    }

    private List<ItemDTO> crearItemsDTO() {
        asegurarPartidaActiva();
        List<ItemDTO> resultado = new ArrayList<ItemDTO>();
        for (int i = 0; i < party.getInventario().getItems().size(); i++) {
            Item item = party.getInventario().obtenerItem(i);
            resultado.add(new ItemDTO(i, item.getNombre(), item.getDescripcion(),
                    item instanceof Arma || item instanceof Armadura));
        }
        return resultado;
    }

    private Personaje obtenerMiembro(int indice) {
        if (party == null || indice < 0 || indice >= party.getMiembros().length) {
            return null;
        }
        return party.getMiembros()[indice];
    }

    private Item obtenerItem(int indice) {
        if (party == null) {
            return null;
        }
        return party.getInventario().obtenerItem(indice);
    }

    private int indiceDeMiembro(Personaje personaje) {
        if (personaje == null || party == null) {
            return -1;
        }
        Personaje[] miembros = party.getMiembros();
        for (int i = 0; i < miembros.length; i++) {
            if (miembros[i] == personaje) {
                return i;
            }
        }
        return -1;
    }

    private void asegurarPartidaActiva() {
        if (party == null) {
            throw new IllegalStateException("No hay una partida activa.");
        }
    }
}
