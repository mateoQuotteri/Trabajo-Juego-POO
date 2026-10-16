package RPG.modelo;

import RPG.modelo.enemigos.Enemigo;
import RPG.modelo.personajes.Personaje;

// Una batalla: la party contra UN enemigo, en un piso de la Torre.
//
// CAMBIOS 16/10:
// - UN solo enemigo por batalla (antes era un array de 4). Es la
//   recomendación explícita del profe y lo que dice el documento entregado
//   ("los 4 personajes + el enemigo").
// - El orden de turnos (ordenar por Velocidad, desempate al azar) ahora
//   vive acá y no en MotorDeCombate: Batalla es quien conoce a todos los
//   participantes (GRASP Experto) y así coincide con el diagrama de
//   secuencia 1 (Batalla -> ordenar(personajesVivos + enemigo)).
// - La Batalla lleva la cuenta de "a quién le toca" (turno a turno). Esto
//   es lo que permite que la pantalla de Swing respete el orden por
//   Velocidad: la Vista no decide quién actúa, le pregunta al modelo.
public class Batalla {

    private Party party;
    private Enemigo enemigo;
    private int piso;

    private Combatiente[] ordenDeLaRonda;
    private int indiceTurnoActual;
    private int numeroRonda;

    public Batalla(Party party, Enemigo enemigo, int piso) {
        this.party = party;
        this.enemigo = enemigo;
        this.piso = piso;
        this.numeroRonda = 0;
        party.prepararParaBatalla();
        iniciarNuevaRonda();
    }

    // ---------- Orden de turnos ----------

    // Junta a los vivos (party + enemigo) y los ordena por Velocidad. Se
    // llama al empezar cada ronda (documento: "se reordena al empezar cada
    // ronda", porque la velocidad puede cambiar por el Escupitajo del Slime).
    private void iniciarNuevaRonda() {
        numeroRonda++;
        Personaje[] vivos = party.getMiembrosVivos();
        ordenDeLaRonda = new Combatiente[vivos.length + 1];
        for (int i = 0; i < vivos.length; i++) {
            ordenDeLaRonda[i] = vivos[i];
        }
        ordenDeLaRonda[vivos.length] = enemigo;
        ordenarPorVelocidad(ordenDeLaRonda);
        indiceTurnoActual = 0;
    }

    // Selection sort hecho a mano (sin Collections.sort ni Comparator).
    // Empate de velocidad -> se decide al azar (documento).
    private void ordenarPorVelocidad(Combatiente[] orden) {
        for (int i = 0; i < orden.length - 1; i++) {
            int indiceMejor = i;
            for (int j = i + 1; j < orden.length; j++) {
                int velJ = orden[j].getVelocidad();
                int velMejor = orden[indiceMejor].getVelocidad();
                if (velJ > velMejor || (velJ == velMejor && Math.random() < 0.5)) {
                    indiceMejor = j;
                }
            }
            Combatiente temporal = orden[i];
            orden[i] = orden[indiceMejor];
            orden[indiceMejor] = temporal;
        }
    }

    // A quién le toca actuar ahora. Saltea a los que murieron en el medio
    // de la ronda, y si la ronda se terminó arranca otra. Devuelve null si
    // la batalla ya terminó.
    public Combatiente getCombatienteActual() {
        if (estaTerminada()) {
            return null;
        }
        while (true) {
            while (indiceTurnoActual < ordenDeLaRonda.length) {
                Combatiente actual = ordenDeLaRonda[indiceTurnoActual];
                if (actual.estaVivo()) {
                    return actual;
                }
                indiceTurnoActual++;
            }
            iniciarNuevaRonda();
        }
    }

    // ¿Le toca al enemigo? Se compara por identidad con el enemigo de esta
    // batalla (así no hace falta preguntar "instanceof Enemigo").
    public boolean esTurnoDelEnemigo() {
        return getCombatienteActual() == enemigo;
    }

    // El personaje que tiene el turno, o null si le toca al enemigo o la
    // batalla terminó.
    public Personaje getPersonajeEnTurno() {
        Combatiente actual = getCombatienteActual();
        Personaje[] miembros = party.getMiembros();
        for (int i = 0; i < miembros.length; i++) {
            if (miembros[i] == actual) {
                return miembros[i];
            }
        }
        return null;
    }

    // El que estaba actuando ya terminó: pasa al siguiente.
    public void terminarTurnoActual() {
        indiceTurnoActual++;
    }

    // ---------- Estado de la batalla ----------

    public boolean estaTerminada() {
        return !enemigo.estaVivo() || party.estaDerrotada();
    }

    public boolean ganoLaParty() {
        return !enemigo.estaVivo() && !party.estaDerrotada();
    }

    // Texto "Mago -> Goblin -> Guerrero..." para mostrar en pantalla.
    public String describirOrdenDeLaRonda() {
        String texto = "";
        for (int i = 0; i < ordenDeLaRonda.length; i++) {
            if (i > 0) {
                texto += " -> ";
            }
            texto += ordenDeLaRonda[i].getNombre();
        }
        return texto;
    }

    public Party getParty() {
        return party;
    }

    public Enemigo getEnemigo() {
        return enemigo;
    }

    public int getPiso() {
        return piso;
    }

    public int getNumeroRonda() {
        return numeroRonda;
    }
}
