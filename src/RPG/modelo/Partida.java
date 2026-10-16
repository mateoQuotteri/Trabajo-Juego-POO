package RPG.modelo;

import RPG.modelo.personajes.Arquero;
import RPG.modelo.personajes.Curador;
import RPG.modelo.personajes.Guerrero;
import RPG.modelo.personajes.Mago;

// Una partida en curso: la party y el piso de la Torre en el que está.
// Es lo que más adelante se va a guardar en archivo (documento:
// "almacenando el piso alcanzado, los atributos de la party y el estado
// del inventario").
//
// Antes no existía: cada batalla arrancaba con una party nueva de nivel 1,
// así que la experiencia y los niveles no servían para nada.
public class Partida {

    private Party party;
    private int pisoActual;
    private boolean ganada;
    private boolean perdida;

    public Partida(Party party) {
        this.party = party;
        this.pisoActual = 1;
        this.ganada = false;
        this.perdida = false;
    }

    // Partida nueva con la party del documento: un personaje de cada clase
    // (por ahora con los nombres del grupo; se puede pedir al jugador).
    public static Partida crearNueva() {
        Party party = new Party();
        party.agregarMiembro(new Guerrero("Mateo"));
        party.agregarMiembro(new Mago("Pedro"));
        party.agregarMiembro(new Arquero("Cristian"));
        party.agregarMiembro(new Curador("Evelyn"));
        return new Partida(party);
    }

    // Se ganó la batalla del piso actual: se sube un piso, o se gana el
    // juego si era el piso 10 (documento 1.2).
    public void registrarVictoria() {
        if (pisoActual == GeneradorDeEnemigos.PISO_JEFE_FINAL) {
            ganada = true;
        } else {
            pisoActual++;
        }
    }

    // Documento 1.2: si toda la party cae, "se termina la partida inmediatamente".
    public void registrarDerrota() {
        perdida = true;
    }

    public boolean estaTerminada() {
        return ganada || perdida;
    }

    public boolean estaGanada() {
        return ganada;
    }

    public Party getParty() {
        return party;
    }

    public int getPisoActual() {
        return pisoActual;
    }
}
